package com.cred.platform.processors.processors.aggregator

import com.cred.platform.processors.commons.{Processor, ProcessorContext}
import com.cred.platform.processors.commons.model.Step
import org.apache.spark.sql.functions.{col, window, max, min, sum, count, avg, when, expr}

object GroupbyProcessor extends Processor {
  override val processorName: String = "GROUPBY"

  override def process(step: Step, context: ProcessorContext): Unit = {
    val inputDf = context.getDataframe(step.inputStream(0))
    if (inputDf == null) {
      throw new IllegalArgumentException("Input DataFrame cannot be null for GroupbyProcessor")
    }

    // Extract parameters
    val groupByCols = step.params.get("groupByColumns").split(",")
    if (groupByCols.isEmpty) {
      throw new IllegalArgumentException("groupByColumns must be provided in step parameters")
    }

    val aggregationsStr = step.params.get("aggregations")
    if (aggregationsStr == null || aggregationsStr.isEmpty) {
      throw new IllegalArgumentException("aggregations must be provided in step parameters")
    }

    // Parse aggregation config
    val aggregationsList = aggregationsStr.split(",").map { agg =>
      val parts = agg.split(":")
      if (parts.length != 3) throw new IllegalArgumentException(s"Invalid aggregation format: $agg. Expected format: outputCol:type:inputCol")
      (parts(0), parts(1), parts(2))
    }

    // Handle time window if specified
    val windowDuration = Option(step.params.get("windowDuration"))
    val slideDuration = Option(step.params.get("slideDuration"))
    val watermarkDuration = Option(step.params.get("watermarkDuration")).getOrElse("10 seconds")
    val timestampCol = Option(step.params.get("timestampColumn")).getOrElse("timestamp")

    // Create the initial grouping
    val baseDF = windowDuration.map { duration =>
      val dfWithWatermark = inputDf.withWatermark(timestampCol, watermarkDuration)
      val windowExpr = slideDuration.map { slide =>
        window(expr(timestampCol), duration, slide)
      }.getOrElse(window(expr(timestampCol), duration))
      dfWithWatermark.withColumn("window", windowExpr)
    }.getOrElse(inputDf)

    // Build aggregation expressions
    val aggExprs = aggregationsList.map { case (outputCol, aggType, inputCol) =>
      aggType.toLowerCase match {
        case "max" => max(expr(inputCol)).as(outputCol)
        case "min" => min(expr(inputCol)).as(outputCol)
        case "sum" => sum(expr(inputCol)).as(outputCol)
        case "count" => count(when(expr(inputCol).isNotNull, true)).as(outputCol)
        case "avg" => avg(expr(inputCol)).as(outputCol)
        case unknown => throw new IllegalArgumentException(s"Unsupported aggregation type: $unknown")
      }
    }

    // Perform grouping and aggregation
    val resultDf = windowDuration.map { _ =>
      val groupCols = Seq(col("window")) ++ groupByCols.map(expr)
      baseDF.groupBy(groupCols: _*)
        .agg(aggExprs.head, aggExprs.tail: _*)
        .select(
          Seq(
            col("window.start").as("window_start"),
            col("window.end").as("window_end")
          ) ++ groupByCols.map(expr) ++ aggExprs: _*
        )
    }.getOrElse {
      baseDF.groupBy(groupByCols.map(expr): _*)
        .agg(aggExprs.head, aggExprs.tail: _*)
    }

    // Save the result
    context.addDataframe(step.outputStream(0), resultDf)
  }
}
