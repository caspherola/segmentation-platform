package com.cred.platform.engine.rules

import com.cred.platform.engine.engine.{SparkJobManager, SparkOrchestrator}
import com.cred.platform.engine.planner.PlaneCreator
import com.cred.platform.engine.utility.SparkUtility
import com.cred.platform.processors.commons.model.PipelineDefinition
import com.cred.platform.processors.commons.plan.Plan
import com.fasterxml.jackson.databind.ObjectMapper
import org.yaml.snakeyaml.constructor.Constructor
import org.yaml.snakeyaml.representer.Representer
import org.yaml.snakeyaml.{DumperOptions, Yaml}

import java.io.InputStream
import scala.util.Try

object RuleRunner {
  private val jsonMapper = new ObjectMapper()
  // Configure SnakeYAML
  private val dumperOptions = new DumperOptions()
  dumperOptions.setIndent(2)
  dumperOptions.setPrettyFlow(true)
  dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK)

  private val representer = new Representer(dumperOptions)
  private val constructor = new Constructor(classOf[PipelineDefinition])
  private val yaml = new Yaml(constructor, representer, dumperOptions)
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

  def getSparkJobs(inputStream: Any, jsonString: String): SparkJobManager={
    val result = jsonString.replace("\\", "").stripPrefix("\"").stripSuffix("\"")

    val pipelineDefinition = jsonMapper.readValue(result, classOf[PipelineDefinition])
    pipelinePlan = getPipelinePlan(pipelineDefinition)
    SparkUtility.getSparkSession
    SparkOrchestrator.processPlan(pipelinePlan)
  }

}
