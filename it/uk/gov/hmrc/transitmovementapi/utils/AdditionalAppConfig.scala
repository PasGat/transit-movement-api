package uk.gov.hmrc.transitmovementapi.utils

import scala.collection.mutable

trait AdditionalAppConfig {
  val additionalAppConfig: mutable.Map[String, Any] = mutable.Map.empty
}
