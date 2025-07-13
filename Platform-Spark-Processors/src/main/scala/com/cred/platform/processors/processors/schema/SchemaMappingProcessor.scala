package com.cred.platform.processors.processors.schema

import com.cred.platform.processors.commons.{Processor, ProcessorContext}
import com.cred.platform.processors.commons.model.Step
import com.cred.platform.processors.processors.WebClient
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.{col, from_json}
import org.apache.spark.sql.types.{DataType, StructType}

import scala.collection.mutable

object SchemaMappingProcessor extends Processor{

  override val processorName: String = "SCHEMA_MAPPING"

  override def process(step: Step, context: ProcessorContext): Unit = {
    val spark=context.sparkSession
    val inputDf = context.getDataframe(step.inputStream(0))
    if (inputDf == null) {
      throw new IllegalArgumentException("Input DataFrame cannot be null for SchemaMappingProcessor")
    }
    // Extract the schema mapping from the step parameters
    val response = WebClient.fetchJobData(step.params.get("eventId")).get
    val mapper=new ObjectMapper()
    val jsonData=mapper.readTree(response)
    val schemaMapping=jsonData.at("/schema").toString
    println(s"Schema mapping: $schemaMapping")

    if (schemaMapping == null || schemaMapping.isEmpty) {
      throw new IllegalArgumentException("Schema mapping must be provided in the step parameters")
    }
    val result = schemaMapping.replace("\\", "").stripPrefix("\"").stripSuffix("\"")
    val selectExpr: mutable.MutableList[String] = mutable.MutableList("CAST(key AS STRING) AS key", "CAST(value AS STRING) AS value")
    val schema = DataType.fromJson(result).asInstanceOf[StructType]
    val selectFields = mutable.MutableList("data.*", "key")

    // Apply the schema to the value column using from_json
    val parsedDf = inputDf
      .selectExpr(selectExpr: _*)
      .withColumn("data", from_json(col("value"), schema))
      .selectExpr(selectFields: _*)
    context.addDataframe(step.outputStream.head, parsedDf)
  }
}
