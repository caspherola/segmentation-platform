package com.cred.platform.engine.engine

import com.cred.segmentation.commons.ProcessorContext
import com.cred.segmentation.commons.plan.Plan
import org.apache.spark.sql.streaming.DataStreamWriter
import org.apache.spark.sql.{DataFrame, Row}

import scala.collection.mutable

class ProcessorContextImpl(plan: Plan) extends ProcessorContext{
  private[this] val streamNameDataframeMap= scala.collection.mutable.Map.empty[String, DataFrame]
  private[this] val streamNameWriterMap= scala.collection.mutable.ListBuffer.empty[DataStreamWriter[Row]]

  override def addDataframe(streamName: String, dataframe: DataFrame): Unit = {
    streamNameDataframeMap(streamName)=dataframe
  }

  override def getDataframe(streamName: String): DataFrame = {
    streamNameDataframeMap(streamName)
  }

  override def addStreamWriter(sw: DataStreamWriter[Row]): Any = {
    streamNameWriterMap += sw
  }

  override def getStreamWriter: mutable.Buffer[DataStreamWriter[Row]] = {
    streamNameWriterMap
  }

  override def getPlan: Plan = {
    plan
  }
}
