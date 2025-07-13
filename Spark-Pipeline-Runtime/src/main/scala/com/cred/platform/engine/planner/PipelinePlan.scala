package com.cred.platform.engine.planner

import com.cred.platform.processors.commons.model.{Datasource, Step}
import com.cred.platform.processors.commons.plan.Plan

class PipelinePlan(steps: Array[Step], dataSources: Array[Datasource], ruleSetInfo: Map[String, String], checkpointingConfig: Map[String, String]) extends Plan {

  val edges = steps.filter(x=>x.inputStream!=null)
    .foldLeft(Map.empty[String, List[Step]])((x,y)=>x++y.inputStream.
    foldLeft(Map.empty[String, List[Step]])((a,b)=>a+(b->(y::x.getOrElse(b,Nil)))))

  val datasourceMap = dataSources.foldLeft(Map.empty[String, Datasource])((x, y) => x + (y.sourceName -> y))


  override def toString: String = {
    s"PipelinePlan(steps=${steps.mkString(", ")}, dataSources=${dataSources.mkString(", ")})"
  }
  val handleGraph= new GraphicalPlanHandler(steps, edges)

  override def validPlan(): Boolean = true

  override def getDatasource(sourceName: String): Datasource = {
    datasourceMap(sourceName)
  }

  override def getRuleSetDefinition(): Map[String, String] = {
    ruleSetInfo
  }

  override def getCheckpointingInfo(): Map[String, String] = {
    checkpointingConfig
  }

  override def iterator: Iterator[Step] = {
    handleGraph.getTopologicalIterator
  }
}
