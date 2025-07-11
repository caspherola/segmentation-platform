package com.cred.platform.engine.planner

import com.cred.segmentation.commons.model.PipelineDefinition

import scala.collection.JavaConverters.mapAsScalaMapConverter
import scala.collection.mutable
import scala.collection.immutable


class PlaneCreator {
  def createPlan(pipeline: PipelineDefinition):PipelinePlan={
    new PipelinePlan(pipeline.pipelineRuleDefinition.pipelineFlow, pipeline.pipelineRuleDefinition.dataSources,
      convertToImmutable(pipeline.ruleSetInfo.asScala),convertToImmutable(pipeline.sparkConextConfig.asScala))
  }

  def convertToImmutable(param: mutable.Map[String, String])={
    immutable.Map(Option(param).getOrElse(mutable.Map()).toList: _*)
  }

}
