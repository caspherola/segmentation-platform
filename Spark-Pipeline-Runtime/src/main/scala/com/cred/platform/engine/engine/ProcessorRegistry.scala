package com.cred.platform.engine.engine

import com.cred.platform.processors.commons.Processor

import scala.collection.mutable

object ProcessorRegistry {
  private val processorMap=mutable.Map.empty[String, Processor]

  def registerProcessor(processor: Processor): Unit = {
    processorMap += (processor.processorName -> processor)
  }
  def getProcessor(name: String): Option[Processor] = {
    processorMap.get(name)
  }

  def getAvailableProcessors: scala.collection.Set[String] = {
    processorMap.keySet
  }

}
