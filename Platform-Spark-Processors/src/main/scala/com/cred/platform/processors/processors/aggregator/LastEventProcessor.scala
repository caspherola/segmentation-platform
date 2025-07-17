package com.cred.platform.processors.processors.aggregator

import com.cred.platform.processors.commons.{Processor, ProcessorContext}
import com.cred.platform.processors.commons.model.Step
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.{col, expr, udf}
import org.apache.spark.sql.types.TimestampType
import redis.clients.jedis.{Jedis, JedisPool, JedisPoolConfig}

import java.sql.Timestamp
import scala.collection.JavaConverters

object LastEventProcessor extends Processor {

  override val processorName: String = "LAST_EVENT_PROCESSOR"

  // Redis connection pool (singleton)
  @volatile private var jedisPool: Option[JedisPool] = None

  private def getJedisPool: JedisPool = {
    if (jedisPool.isEmpty) {
      synchronized {
        if (jedisPool.isEmpty) {
          val poolConfig = new JedisPoolConfig()
          poolConfig.setMaxTotal(20)
          poolConfig.setMaxIdle(10)
          poolConfig.setMinIdle(5)
          poolConfig.setTestOnBorrow(true)
          poolConfig.setTestOnReturn(true)
          poolConfig.setBlockWhenExhausted(true)

          // Default Redis configuration - can be made configurable
          val redisHost = sys.env.getOrElse("REDIS_HOST", "localhost")
          val redisPort = sys.env.getOrElse("REDIS_PORT", "6379").toInt
          val redisPassword = sys.env.get("REDIS_PASSWORD")

          jedisPool = Some(
            if (redisPassword.isDefined) {
              new JedisPool(poolConfig, redisHost, redisPort, 2000, redisPassword.get)
            } else {
              new JedisPool(poolConfig, redisHost, redisPort, 2000)
            }
          )
        }
      }
    }
    jedisPool.get
  }

  // UDF for Redis operations
  private def createLastEventUDF(windowDurationSeconds: Long) = {
    udf((primaryKey: String) => {
      if (primaryKey == null) {
        null
      } else {
        val currentTime = new Timestamp(System.currentTimeMillis())
        processLastEvent(primaryKey, currentTime, windowDurationSeconds, shouldInsert = true)
      }
    }).asNondeterministic()
  }

  // Conditional UDF that applies logic only when condition is met
  private def createConditionalLastEventUDF(windowDurationSeconds: Long) = {
    udf((primaryKey: String, conditionValue: Boolean) => {
      if (primaryKey == null) {
        null
      } else {
        val currentTime = new Timestamp(System.currentTimeMillis())
        processLastEvent(primaryKey, currentTime, windowDurationSeconds, shouldInsert = conditionValue)
      }
    })
  }

  private def processLastEvent(primaryKey: String, eventTime: Timestamp, windowDurationSeconds: Long, shouldInsert: Boolean): Timestamp = {
    val pool = getJedisPool
    var jedis: Jedis = null

    try {
      jedis = pool.getResource
      val currentTimeMillis = System.currentTimeMillis()
      val eventTimeMillis = eventTime.getTime
      val windowDurationMillis = windowDurationSeconds * 1000

      if (shouldInsert) {
        // Condition is TRUE - Get stored timestamp BEFORE inserting new record
        val storedTimestampStr = jedis.get(primaryKey)
        val storedTimestamp = Option(storedTimestampStr).map(_.toLong)

        // Insert/update Redis with the current event time
        jedis.set(primaryKey, eventTimeMillis.toString)

        // Set TTL to prevent Redis from growing indefinitely
        // TTL is set to 2x window duration to ensure we don't lose data prematurely
        jedis.expire(primaryKey, (windowDurationSeconds * 2).toInt)

        // Return the previously stored event if it was within the window
        storedTimestamp match {
          case Some(storedTime) =>
            // Check if enough time has passed since the stored event
            if (currentTimeMillis - storedTime <= windowDurationMillis) {
              new Timestamp(storedTime)
            } else {
              null
            }
          case None => null // No previous event stored
        }
      } else {
        // Condition is FALSE - only check if key exists in Redis, don't insert anything
        val storedTimestampStr = jedis.get(primaryKey)
        val storedTimestamp = Option(storedTimestampStr).map(_.toLong)
        // Return the previously stored event if it was within the window
        storedTimestamp match {
          case Some(storedTime) =>
            // Check if enough time has passed since the stored event
            if (currentTimeMillis - storedTime <= windowDurationMillis) {
              new Timestamp(storedTime)
            } else {
              null
            }
          case None => null // No previous event stored
        }
      }

    } catch {
      case ex: Exception =>
        // Log error and return null to avoid breaking the stream
        println(s"Error processing last event for key $primaryKey: ${ex.getMessage}")
        null
    } finally {
      if (jedis != null) {
        jedis.close()
      }
    }
  }

  override def process(step: Step, context: ProcessorContext): Unit = {
    val inputDf = context.getDataframe(step.inputStream(0))
    val params = JavaConverters.mapAsScalaMap(step.params)

    // Extract required parameters
    val primaryKeyColumn = params.getOrElse("primaryKey",
      throw new IllegalArgumentException("primaryKey parameter is required"))
    val windowDuration = params.getOrElse("windowDuration",
      throw new IllegalArgumentException("windowDuration parameter is required"))

    // Extract optional condition parameter
    val conditionExpression = params.get("condition")

    // Parse window duration (expecting format like "30s", "5m", "1h")
    val windowDurationSeconds = parseWindowDuration(windowDuration)

    // Validate that required columns exist
    validateColumns(inputDf, primaryKeyColumn)

    val outputDf = conditionExpression match {
      case Some(condition) =>
        // Apply conditional logic
        val lastEventUDF = createConditionalLastEventUDF(windowDurationSeconds)

        // Add condition evaluation column temporarily
        val dfWithCondition = inputDf.withColumn("_temp_condition", expr(condition))

        // Apply UDF with condition-based insert logic
        val dfWithLastEvent = dfWithCondition.withColumn(
          "lastEvent",
          lastEventUDF(col(primaryKeyColumn), col("_temp_condition"))
        )

        // Remove temporary condition column
        dfWithLastEvent.drop("_temp_condition")

      case None =>
        // Apply to all records when no condition is specified
        val lastEventUDF = createLastEventUDF(windowDurationSeconds)
        inputDf.withColumn(
          "lastEvent",
          lastEventUDF(col(primaryKeyColumn))
        )
    }

    context.addDataframe(step.outputStream.head, outputDf)
  }

  private def parseWindowDuration(duration: String): Long = {
    val pattern = """(\d+)([smh])""".r
    duration.toLowerCase match {
      case pattern(value, unit) =>
        val num = value.toLong
        unit match {
          case "s" => num
          case "m" => num * 60
          case "h" => num * 3600
          case _ => throw new IllegalArgumentException(s"Unsupported time unit: $unit")
        }
      case _ => throw new IllegalArgumentException(s"Invalid window duration format: $duration. Expected format: 30s, 5m, 1h")
    }
  }

  private def validateColumns(df: DataFrame, primaryKeyColumn: String): Unit = {
    val columns = df.columns.toSet

    if (!columns.contains(primaryKeyColumn)) {
      throw new IllegalArgumentException(s"Primary key column '$primaryKeyColumn' not found in DataFrame")
    }
  }

  // Cleanup method to close Redis connections
  def shutdown(): Unit = {
    jedisPool.foreach(_.close())
    jedisPool = None
  }
}