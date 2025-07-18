package com.cred.platform.processors.processors.kafka

import com.cred.platform.processors.commons.{Processor, ProcessorContext}
import com.cred.platform.processors.commons.model.Step

import scala.collection.JavaConverters

object KafkaReadProcessor extends Processor{

  override val processorName: String = "READ_KAFKA"

  override def process(step: Step, context: ProcessorContext): Unit = {
    val kafkaSource= context.getPlan.getDatasource(step.params.get("sourceName"))
    val kafkaParams = JavaConverters.mapAsScalaMap(step.params)
    if (kafkaParams == null || kafkaParams.isEmpty) {
      throw new IllegalArgumentException("Kafka parameters cannot be null or empty for KafkaReadProcessor")
    }
  val readerOps= kafkaParams.filter(x=> x._1.startsWith("reader.option")).map(x=>x._1.stripPrefix("reader.option")->x._2)
    // Read from Kafka using Spark Structured Streaming
    val df = context.sparkSession
      .readStream
      .format("kafka")
      .options(readerOps)
      .options(JavaConverters.mapAsScalaMap(kafkaSource.params))
      .load()

    // Add the DataFrame to the context for further processing
    addDataframeToContext(step.outputStream.head, df, context)
  }
}
