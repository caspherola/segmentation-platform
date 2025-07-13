package com.cred.platform.engine


import com.cred.platform.engine.engine.{ProcessorRegistry, SparkJobManager}
import com.cred.platform.engine.rules.RuleRunner
import com.cred.platform.engine.utility.WebClient.fetchJobData
import com.cred.platform.processors.commons.Processor
import com.cred.platform.processors.commons.model.PipelineDefinition
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import io.circe.Json
import io.circe.yaml.syntax.AsYaml
import io.circe.parser._
import org.apache.commons.io.IOUtils
import org.apache.commons.lang.CharSet
import org.reflections.Reflections
import org.reflections.scanners.Scanners

import java.nio.charset.Charset

object Bootstrap {
  val basePackage = "com.cred.platform.processors"
  var runtimeReflection = new Reflections(basePackage, Scanners.values())
  def main(args: Array[String]): Unit = {
    try {
      if (args.length < 1) {
        throw new IllegalArgumentException("Please provide the path to the job manager file as an argument.")
      }
      registerProcessor(runtimeReflection)
      runSparkJob(getJobManager(getPipelineContext(args(0))))
    }catch {
      case exception: Exception =>
        println(s"An error occurred during the bootstrap process: ${exception.getMessage}")
        exception.printStackTrace()
    }
  }

  def registerProcessor(reflections: Reflections): Unit = {
    val processorClasses = reflections.getSubTypesOf(classOf[Processor])
    processorClasses.forEach { processorClass =>
      try {
        val constrcutor=Class.forName(processorClass.getCanonicalName).getDeclaredConstructors()
        constrcutor(0).setAccessible(true)
        val processor: Processor = constrcutor(0).newInstance().asInstanceOf[Processor]
        ProcessorRegistry.registerProcessor(processor)
      } catch {
        case e: Exception =>
          println(s"Failed to register processor: ${processorClass.getName}, error: ${e.getMessage}")
      }
    }
  }

  def getJobManager(path: String): SparkJobManager = {
    val inputStream = IOUtils.toInputStream(path, Charset.defaultCharset)
    RuleRunner.getSparkJobs(inputStream, path)
  }

  def runSparkJob(sparkJobManager: SparkJobManager): Unit={
    if (sparkJobManager == null) {
      throw new IllegalArgumentException("SparkJobManager cannot be null")
    }
    sparkJobManager.runStreamingHobs()
    sparkJobManager.awaitTermination()

  }

  def getPipelineContext(jobId: String): String= {
    val mapper=new ObjectMapper()
    var yamlString: String = null
    val result= fetchJobData(jobId).get
    val pipelineResponseParse=mapper.readTree(result)
    val rawPipeline=pipelineResponseParse.at("/config_data").toString
    try {
      val jsonValue= parse(rawPipeline).getOrElse(Json.Null)
    }catch {
      case e: Exception =>
        throw new IllegalArgumentException(s"Failed to parse pipeline JSON: ${e.getMessage}")
    }
    rawPipeline
  }
}
