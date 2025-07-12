package com.cred.platform.processors.commons.model

import scala.beans.BeanProperty

final class PipelineRuleDefinition {
  @BeanProperty var dataSources: Array[Datasource]=_
  @BeanProperty var pipelineFlow: Array[Step]=_
}
