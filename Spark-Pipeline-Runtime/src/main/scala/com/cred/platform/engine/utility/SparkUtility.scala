package com.cred.platform.engine.utility

import com.cred.platform.engine.rules.RuleRunner
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

object SparkUtility {
  var spark: SparkSession=null

  def getSparkSession: SparkSession = {
    if (spark == null) {
      var sparkContextConfig: Map[String, String] = null
      val sparkConfig = new SparkConf()
      if (RuleRunner.pipelinePlan != null) {
        val sparkContextConfigMap = RuleRunner.pipelinePlan.getCheckpointingInfo()
        sparkContextConfigMap.foreach { keyValue =>
          sparkConfig.set(keyValue._1, keyValue._2)
        }
      }
      spark = SparkSession.builder().config(sparkConfig).getOrCreate()
    }
      spark
  }

}
