package com.cred.segmentation
package commons.model

import scala.beans.BeanProperty

final class Step {
  @BeanProperty var stepName: String= ""
  @BeanProperty var stepType: String = ""
  @BeanProperty var inputStream: Array[String] = _
  @BeanProperty var outputStream: Array[String]= _
  @BeanProperty var params: java.util.Map[String, String] = _
}
