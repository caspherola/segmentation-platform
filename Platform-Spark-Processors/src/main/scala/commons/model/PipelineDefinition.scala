package com.cred.segmentation
package commons.model

import scala.beans.BeanProperty

final class PipelineDefinition {
  @BeanProperty var ruleSetInfo: java.util.Map[String, String]= _
  @BeanProperty var sparkContextConfig: java.util.Map[String, String] = _
  @BeanProperty var pipelineRuleDefinition: PipelineRuleDefinition = _
}
