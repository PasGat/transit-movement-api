/*
 * Copyright 2019 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.transitmovementapi.helpers

import play.api.libs.json.Json
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.{AuditConnector, AuditResult}
import uk.gov.hmrc.play.audit.model.ExtendedDataEvent
import uk.gov.hmrc.transitmovementapi.models.api.TransitSubmission
import uk.gov.hmrc.transitmovementapi.models.types._

import scala.concurrent.{ExecutionContext, Future}

trait AuditEvents {
  val auditConnector: AuditConnector
  implicit val ec: ExecutionContext

  def audit(event: => Future[ExtendedDataEvent], failureMessage: String => String)(implicit hc: HeaderCarrier): Future[AuditResult] =
    event.flatMap(auditConnector.sendExtendedEvent)

  def sendTransitEvent(transitSubmission: TransitSubmission)(implicit hc: HeaderCarrier): Future[ExtendedDataEvent] = {
    Future {
      ExtendedDataEvent(
        "transit-movement-api",
        "transitDetailsSubmission",
        detail = Json.toJson(transitSubmission)
      )
    }
  }
}