package com.cred.segmentation
package commons

import com.cred.segmentation.commons.model.Step
import org.apache.spark.sql.DataFrame

trait Processor {
  val processorName: String

  def process(step: Step, context: ProcessorContext): Unit


}
