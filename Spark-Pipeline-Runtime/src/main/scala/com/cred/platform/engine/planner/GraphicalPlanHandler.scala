package com.cred.platform.engine.planner

import com.cred.platform.processors.commons.model.Step

import scala.collection.mutable

class GraphicalPlanHandler (steps: Array[Step], edges: Map[String, List[Step]]) {

  private val reverseTopology= mutable.Stack[Step]()
  private def orderSteps(curStep: Step, visited: mutable.HashSet[Step]): Unit = {
    visited+=curStep
    if(curStep.getOutputStream!=null) {
      for(outputStream <- curStep.getOutputStream) {
        if (edges.get(outputStream)!=None) {
          for(nextStep <- edges.get(outputStream).get){
            if (!visited.contains(nextStep)) {
              orderSteps(nextStep, visited)
            }
          }
      }}
      reverseTopology.push(curStep)
    }
  }

  def getTopologicalIterator: Iterator[Step] = {
    val startStep= steps.filter(x=>x.inputStream == null || x.inputStream.isEmpty)
    val visited: mutable.HashSet[Step]=new mutable.HashSet[Step]()
    for (startSteo<- startStep) {
        orderSteps(startSteo, visited)
    }
    reverseTopology.iterator
  }


}
