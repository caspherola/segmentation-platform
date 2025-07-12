package com.cred.segmentation
package commons

import com.cred.segmentation.commons.plan.Plan
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.apache.spark.sql.streaming.DataStreamWriter

import scala.collection.mutable


abstract class ProcessorContext {
  private[this] val streamNameDataframeMap= scala.collection.mutable.Map.empty[String, DataFrame]
  var sparkSession: SparkSession =_
  def addDataframe(streamName: String, dataframe: DataFrame): Unit
  def getDataframe(streamName: String): DataFrame
  def addStreamWriter(sw: DataStreamWriter[Row]): Any
  def getStreamWriter: mutable.Buffer[DataStreamWriter[Row]]
  def getPlan: Plan

}
