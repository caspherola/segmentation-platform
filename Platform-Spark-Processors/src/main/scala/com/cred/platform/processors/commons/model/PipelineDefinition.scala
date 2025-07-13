package com.cred.platform.processors.commons.model

import scala.beans.BeanProperty
import java.util.{HashMap, Map => JavaMap}

final class PipelineDefinition {
  @BeanProperty var ruleSetInfo: JavaMap[String, String] = new HashMap[String, String]()
  @BeanProperty var sparkContextConfig: JavaMap[String, String] = new HashMap[String, String]()
  @BeanProperty var pipelineRuleDefinition: PipelineRuleDefinition = new PipelineRuleDefinition()

  // No-arg constructor is implicit
  def this(ruleSetInfo: JavaMap[String, String],
           sparkContextConfig: JavaMap[String, String],
           pipelineRuleDefinition: PipelineRuleDefinition) = {
    this()
    this.ruleSetInfo = ruleSetInfo
    this.sparkContextConfig = sparkContextConfig
    this.pipelineRuleDefinition = pipelineRuleDefinition
  }

  override def toString: String = s"PipelineDefinition($ruleSetInfo, $sparkContextConfig, $pipelineRuleDefinition)"
}