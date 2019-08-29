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

import java.time.{Duration, Instant}

import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.transitmovementapi.models.api.transit.TransitSubmission
import uk.gov.hmrc.transitmovementapi.models.types.ModelTypes._
import uk.gov.hmrc.transitmovementapi.models.types._

case class OfficeOfTransitSubmission(
  movementReferenceNumber:    MovementReferenceNumber,
  transitUnitType:            TransitUnitType,
  transitUnitReferenceNumber: TransitUnitReferenceNumber,
  captureMethod:              MrnCaptureMethod,
  estimatedArrivalTime:       Instant,
  departurePort:              DeparturePort,
  destinationPort:            DestinationPort,
  carrier:                    Carrier)

object OfficeOfTransitSubmission {
  def fromSubmissionRequest(transitSubmission: TransitSubmission): OfficeOfTransitSubmission =
    OfficeOfTransitSubmission(
      movementReferenceNumber    = transitSubmission.movementReferenceNumber,
      transitUnitType            = transitSubmission.transitUnitType,
      transitUnitReferenceNumber = transitSubmission.transitUnitReferenceNumber,
      captureMethod              = transitSubmission.transitMetadata.captureMethod,
      estimatedArrivalTime =
        transitSubmission.transitMetadata.captureDateTime.plus(Duration.ofMinutes(transitSubmission.crossingDetails.duration.value)),
      departurePort   = transitSubmission.crossingDetails.departurePort,
      destinationPort = transitSubmission.crossingDetails.destinationPort,
      carrier         = transitSubmission.crossingDetails.carrier
    )

  implicit val format: OFormat[OfficeOfTransitSubmission] = Json.format[OfficeOfTransitSubmission]
}
