package com.cred.segmentation
package commons.plan

import commons.model.{Datasource, Step}

trait Plan extends Iterable [Step]{
  def validPlan():Boolean
  def getDatasource(sourceName: String): Datasource
  def getRuleSetDefinition(): Map[String, String]
  def getCheckpointingInfo(): Map[String, String]

  override def iterator: Iterator[Step]

}
