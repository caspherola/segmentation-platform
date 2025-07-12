package com.cred.platform.processors.commons

import com.cred.platform.processors.commons.model.Step
import org.apache.spark.sql.DataFrame

trait Processor {
  val processorName: String

  def process(step: Step, context: ProcessorContext): Unit
  final def addDataframeToContext(
      streamName: String,
      dataframe: DataFrame,
      context: ProcessorContext
  ): Unit = {
    context.addDataframe(streamName, dataframe)
  }
  final def addStreamWriterToContext(
      sw: org.apache.spark.sql.streaming.DataStreamWriter[org.apache.spark.sql.Row],
      context: ProcessorContext
  ): Unit = {
    context.addStreamWriter(sw)
  }
}
