package com.cred.platform.engine


import com.cred.platform.engine.engine.{ProcessorRegistry, SparkJobManager}
import com.cred.platform.engine.rules.RuleRunner
import com.cred.platform.engine.utility.WebClient.fetchJobData
import com.cred.segmentation.commons.Processor
import com.fasterxml.jackson.databind.ObjectMapper
import io.circe.yaml.syntax.AsYaml
import io.circe.{Json, parser}
import org.apache.commons.io.IOUtils
import org.apache.commons.lang.CharSet
import org.reflections.Reflections
import org.reflections.scanners.Scanners

import java.nio.charset.Charset

object Bootstrap {
  val basePackage = "com.cred.segmentation"
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
    val processorClasses = reflections.getSubTypesOf(classOf[com.cred.segmentation.commons.Processor])
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
    RuleRunner.getSparkJobs(inputStream)
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
    val result= fetchJobData(jobId)
    if(result== null || result.isEmpty) {
      throw new IllegalArgumentException(s"Job data for jobId $jobId is empty or null")
    }
    val pipelineResponseParse=mapper.readTree(result.get)
    val rawPipeline=pipelineResponseParse.at("/pipeline").toString
    try {
      val jsonValue= parser.parse(rawPipeline).getOrElse(Json.Null)
      if(jsonValue.isNull){
        yamlString=jsonValue.asYaml.spaces2
      }
    }catch {
      case e: Exception =>
        throw new IllegalArgumentException(s"Failed to parse pipeline JSON: ${e.getMessage}")
    }
    yamlString
  }
}
