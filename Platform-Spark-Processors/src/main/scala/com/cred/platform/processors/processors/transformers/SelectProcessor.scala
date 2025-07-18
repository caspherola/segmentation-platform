package com.cred.platform.processors.processors.transformers

import com.cred.platform.processors.commons.{Processor, ProcessorContext}
import com.cred.platform.processors.commons.model.Step
import org.apache.spark.sql.functions.expr

object SelectProcessor extends Processor{

  override val processorName: String = "SELECT"

  override def process(step: Step, context: ProcessorContext): Unit = {

    val inputDf = context.getDataframe(step.inputStream(0))
    if (inputDf == null) {
      throw new IllegalArgumentException("Input DataFrame cannot be null for SelectProcessor")
    }
    val selectColums= step.params.get("columnNames").trim.split(",").map(x=>expr(x))
    val outputDf=inputDf.select(selectColums:_*)
    context.addDataframe(step.outputStream.head, outputDf)
  }
}
