package com.cred.platform.engine.rules

import com.cred.platform.processors.commons.model.PipelineDefinition
import org.apache.hadoop.shaded.org.jline.utils.InputStreamReader
import org.yaml.snakeyaml.{DumperOptions, Yaml}
import org.yaml.snakeyaml.constructor.Constructor
import com.fasterxml.jackson.databind.ObjectMapper
import org.yaml.snakeyaml.representer.Representer

import java.io.InputStream
import scala.util.Try

object RuleReader {
  // Your existing code for RuleReader
  def readPipelineDefinition(inputStream: InputStream): Try[PipelineDefinition] = {
    val yaml=new Yaml(new Constructor(classOf[PipelineDefinition]))
    Try(yaml.load[PipelineDefinition](new InputStreamReader(inputStream)))

  }
}

