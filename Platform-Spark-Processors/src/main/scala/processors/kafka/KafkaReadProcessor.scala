package processors.kafka

import com.cred.segmentation.commons.{Processor, ProcessorContext}
import com.cred.segmentation.commons.model.Step

import scala.collection.JavaConverters

object KafkaReadProcessor extends Processor{

  override val processorName: String = "READ_KAFKA"

  override def process(step: Step, context: ProcessorContext): Unit = {

    val inputStream = step.inputStream
    if (inputStream == null || inputStream.isEmpty) {
      throw new IllegalArgumentException("Input stream cannot be null or empty for KafkaReadProcessor")
    }
    val kafkaParams = JavaConverters.mapAsScalaMap(step.params)
    if (kafkaParams == null || kafkaParams.isEmpty) {
      throw new IllegalArgumentException("Kafka parameters cannot be null or empty for KafkaReadProcessor")
    }
  val readerOps= kafkaParams.filter(x=> x._1.startsWith("reader.option")).map(x=>x._1.stripPrefix("reader.option")->x._2)
    // Read from Kafka using Spark Structured Streaming
    val df = context.sparkSession
      .readStream
      .format("kafka")
      .options(readerOps)
      .load()

    // Add the DataFrame to the context for further processing
    addDataframeToContext(step.outputStream.head, df, context)
  }
}
