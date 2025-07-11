package com.cred.platform.engine.engine

import com.cred.platform.engine.utility.SparkUtility
import org.apache.spark.sql.Row
import org.apache.spark.sql.streaming.{DataStreamWriter, StreamingQuery}

class SparkJobManager (streamWriter: Seq[DataStreamWriter[Row]]) {
  var streamingQueries: Seq[StreamingQuery] = _

  def runStreamingHobs(): Unit = {
    try {
      if(streamWriter.isEmpty){
        throw new IllegalArgumentException("No streaming jobs to run")
      }
      streamingQueries=streamWriter.map(x=>x.start())
    }catch {
      case exception: Exception =>
        println(s"Error while starting streaming jobs: ${exception.getMessage}")
        throw exception
    }
  }

  def awaitTermination(): Unit ={
    SparkUtility.getSparkSession.streams.awaitAnyTermination()
  }

}
