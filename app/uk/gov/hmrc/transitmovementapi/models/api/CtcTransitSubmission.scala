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

package uk.gov.hmrc.transitmovementapi.models.api

import java.time.Instant

import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.transitmovementapi.models.types.ModelTypes._
import uk.gov.hmrc.transitmovementapi.models.types._

case class CtcTransitSubmission(movementReferenceNumber: MovementReferenceNumber,
                                vehicleReferenceNumber: Option[VehicleReferenceNumber],
                                captureMethod: MrnCaptureMethod,
                                captureDateTime: Instant,
                                departureDateTime: Option[Instant],
                                departurePort: DeparturePort,
                                destinationPort: DestinationPort,
                                duration: Duration,
                                carrier: Carrier,
                                vessel: Option[String]
                               )

object CtcTransitSubmission {
  def fromSubmissionRequest(transitSubmission: TransitSubmission): CtcTransitSubmission = {
    CtcTransitSubmission(
      movementReferenceNumber = transitSubmission.movementReferenceNumber,
      vehicleReferenceNumber  = transitSubmission.vehicleReferenceNumber,
      captureMethod           = transitSubmission.transitMetadata.captureMethod,
      captureDateTime         = transitSubmission.transitMetadata.captureDateTime,
      departureDateTime       = transitSubmission.crossingDetails.departureDateTime,
      departurePort           = transitSubmission.crossingDetails.departurePort,
      destinationPort         = transitSubmission.crossingDetails.destinationPort,
      duration                = transitSubmission.crossingDetails.duration,
      carrier                 = transitSubmission.crossingDetails.carrier,
      vessel                  = transitSubmission.crossingDetails.vessel
    )
  }

  implicit val format: OFormat[CtcTransitSubmission] = Json.format[CtcTransitSubmission]
}