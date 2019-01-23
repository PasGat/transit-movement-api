package uk.gov.hmrc.transitmovementapi.helpers

import scala.collection.mutable

trait AdditionalAppConfig {
  val additionalAppConfig: mutable.Map[String, Any] = mutable.Map.empty
}
