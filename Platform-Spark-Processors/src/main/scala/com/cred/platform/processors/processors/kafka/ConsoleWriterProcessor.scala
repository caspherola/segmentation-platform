package com.cred.platform.processors.processors.kafka

import com.cred.platform.processors.commons.{Processor, ProcessorContext}
import com.cred.platform.processors.commons.model.Step

object ConsoleWriterProcessor extends Processor {
  override val processorName: String = "WRITER_CONSOLE"

  override def process(step: Step, context: ProcessorContext): Unit = {
    val inputDf = context.getDataframe(step.inputStream.head)
    if (inputDf == null) {
      throw new IllegalArgumentException("Input DataFrame cannot be null for ConsoleWriterProcessor")
    }
    // Write the DataFrame to the console
    val consoleWriter = inputDf.writeStream
      .format("console")
      .outputMode("append")
      .queryName(step.stepName)

    // Add the console writer to the context for execution
    addStreamWriterToContext(consoleWriter, context)
  }
}
