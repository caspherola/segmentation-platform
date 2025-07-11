package processors.kafka

import com.cred.segmentation.commons.{Processor, ProcessorContext}
import com.cred.segmentation.commons.model.Step
import org.apache.spark.sql.Row
import org.apache.spark.sql.streaming.DataStreamWriter

import scala.collection.JavaConverters
import scala.collection.JavaConverters.mapAsScalaMapConverter

object KafkaWriteProcessor extends Processor{

  override val processorName: String = "WRITE_KAFKA"

  override def process(step: Step, context: ProcessorContext): Unit = {
    val kafkaSink= context.getPlan.getDatasource(step.params.get("sourceName"))
    val kafkaSinkConfig: Map[String, String] = kafkaSink.params.asScala.toMap
    val params= JavaConverters.mapAsScalaMap(step.params)
    val writerOps = params.filter(x => x._1.startsWith("writer.option"))
      .map(x => x._1.stripPrefix("writer.option") -> x._2)
    val inputDf=context.getDataframe(step.inputStream(0))
    val outputKafkaStream= inputDf.writeStream.format("kafka").options(kafkaSinkConfig++writerOps).queryName(step.stepName)
    context.addStreamWriter(outputKafkaStream)
  }
}
