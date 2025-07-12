package com.cred.platform.processors.processors.transformers

import com.cred.platform.processors.commons.{Processor, ProcessorContext}
import com.cred.platform.processors.commons.model.Step
import org.apache.spark.sql.functions.expr

import scala.collection.JavaConverters

object FilterProcessor extends Processor {

  override val processorName: String = "FILTER"

  override def process(step: Step, context: ProcessorContext): Unit = {
    val inputDf = context.getDataframe(step.inputStream(0))
    val preticate=expr(step.params.get("filerPredicate"))
    if (inputDf == null) {
      throw new IllegalArgumentException("Input DataFrame cannot be null for FilterProcessor")
    }
    val outputDf= inputDf.filter(preticate)

    context.addDataframe(step.outputStream.head, outputDf)
  }

}
