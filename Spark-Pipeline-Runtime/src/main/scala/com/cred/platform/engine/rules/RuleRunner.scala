package com.cred.platform.engine.rules

import com.cred.platform.engine.engine.{SparkJobManager, SparkOrchestrator}
import com.cred.platform.engine.planner.PlaneCreator
import com.cred.platform.engine.utility.SparkUtility
import com.cred.segmentation.commons.model.PipelineDefinition
import com.cred.segmentation.commons.plan.Plan

import java.io.InputStream
import scala.util.Try

object RuleRunner {
  var pipelinePlan: Plan=null
  private def getPipelineDefinition(inputStream: Any): Try[PipelineDefinition] = {

    inputStream match {
      case is: InputStream =>
        RuleReader.readPipelineDefinition(is)
      case _ =>
        Try(throw new IllegalArgumentException("Input stream is not of type InputStream"))
    }
  }

  private def getPipelinePlan(plan: PipelineDefinition): Plan={
    val planCreator= new PlaneCreator()
    planCreator.createPlan(plan)

  }

  def getSparkJobs(inputStream: Any): SparkJobManager={
    val pipelineDefinition=getPipelineDefinition(inputStream).get
    pipelinePlan = getPipelinePlan(pipelineDefinition)
    SparkUtility.getSparkSession
    SparkOrchestrator.processPlan(pipelinePlan)
  }

}
