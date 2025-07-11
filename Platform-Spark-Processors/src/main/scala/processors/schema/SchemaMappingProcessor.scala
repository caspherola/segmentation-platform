package processors.schema

import com.cred.segmentation.commons.{Processor, ProcessorContext}
import com.cred.segmentation.commons.model.Step
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
    val schemaMapping = step.params.get("schema")
    if (schemaMapping == null || schemaMapping.isEmpty) {
      throw new IllegalArgumentException("Schema mapping must be provided in the step parameters")
    }
    val selectExpr: mutable.MutableList[String] = mutable.MutableList("CAST(key AS STRING) AS key", "CAST(value AS STRING) AS value")
    val schema = DataType.fromJson(schemaMapping).asInstanceOf[StructType]
    val selectFields = mutable.MutableList("data.*")

    // Apply the schema to the value column using from_json
    val parsedDf = inputDf
      .selectExpr(selectExpr: _*)
      .withColumn("data", from_json(col("value"), schema))
      .selectExpr(selectFields: _*)
    context.addDataframe(step.outputStream.head, parsedDf)
  }
}
