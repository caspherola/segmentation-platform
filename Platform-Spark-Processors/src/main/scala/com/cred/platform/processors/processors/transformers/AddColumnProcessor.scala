package com.cred.platform.processors.processors.transformers

import com.cred.platform.processors.commons.{Processor, ProcessorContext}
import com.cred.platform.processors.commons.model.Step
import org.apache.spark.sql.functions.expr

import scala.collection.JavaConverters

object AddColumnProcessor extends Processor{

  override val processorName: String = "ADD_COLUMNS"

  override def process(step: Step, context: ProcessorContext): Unit = {
    val inputDf= context.getDataframe(step.inputStream(0))
    val param=JavaConverters.mapAsScalaMap(step.params)
    val outputDf = param.foldLeft(inputDf)(
      (df, kv) => {
        if(kv._1.startsWith("column.")) df.withColumn(kv._1.stripPrefix("column."), expr(kv._2)) else df})

    context.addDataframe(step.outputStream.head, outputDf)
  }
}
