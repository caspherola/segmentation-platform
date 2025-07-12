package com.cred.platform.processors.commons.model

import scala.beans.BeanProperty

final class Datasource {
  @BeanProperty var sourceType: String=""
  @BeanProperty var sourceName: String = ""
  @BeanProperty var params: java.util.Map[String, String]=_;

}
