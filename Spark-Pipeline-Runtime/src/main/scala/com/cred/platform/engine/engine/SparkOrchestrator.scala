package com.cred.platform.engine.engine

import com.cred.platform.engine.utility.SparkUtility
import com.cred.platform.processors.commons.plan.Plan

object SparkOrchestrator {

  def processPlan(plan: Plan): SparkJobManager ={
    val newContext = new ProcessorContextImpl(plan)
    val spark= SparkUtility.getSparkSession
    newContext.sparkSession=spark
    for(step <- plan) {
      ProcessorRegistry.getProcessor(step.stepType.toUpperCase()) match {
        case Some(processor)=> processor.process(step, newContext)
        case None=>
          val availableProcessors = ProcessorRegistry.getAvailableProcessors.toString()
          throw new RuntimeException(s"Processor ${step.stepType} not found in registry. Available processors: $availableProcessors")
      }
    }
    new SparkJobManager(newContext.getStreamWriter)
  }
}
