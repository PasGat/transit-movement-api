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

package uk.gov.hmrc.transitmovementapi.models.data

import java.time.Instant

import play.api.libs.functional.syntax.{unlift, _}
import play.api.libs.json._
import reactivemongo.bson.BSONObjectID
import uk.gov.hmrc.transitmovementapi.models.api.TransitSubmission
import uk.gov.hmrc.transitmovementapi.models.types.ModelTypes._
import uk.gov.hmrc.transitmovementapi.models.types._

case class Transit(transitId: CrossingId = BSONObjectID.generate().stringify,
                   movementReferenceNumber: MovementReferenceNumber,
                   vehicleReferenceNumber: Option[VehicleReferenceNumber],
                   crossingId: CrossingId,
                   createdDateTime: Instant = Instant.now,
                   captureMethod: MrnCaptureMethod,
                   captureDateTime: Instant)

object Transit {
  def fromSubmission(crossingId: CrossingId, transit: TransitSubmission): Transit = Transit(
    crossingId = crossingId,
    movementReferenceNumber = transit.movementReferenceNumber,
    vehicleReferenceNumber = transit.vehicleReferenceNumber,
    captureMethod = transit.captureMethod,
    captureDateTime = transit.captureDateTime
  )

  implicit val reads: Reads[Transit] = (
    (__ \ "_id").read[String] and
      (__ \ "movementReferenceNumber").read[MovementReferenceNumber] and
      (__ \ "vehicleReferenceNumber").readNullable[VehicleReferenceNumber] and
      (__ \ "crossingId").read[String] and
      (__ \ "createdDateTime").read[Instant] and
      (__ \ "captureMethod").read[MrnCaptureMethod] and
      (__ \ "captureDateTime").read[Instant]
    ) (Transit.apply _)

  implicit val writes: OWrites[Transit] = (
    (__ \ "_id").write[String] and
      (__ \ "movementReferenceNumber").write[MovementReferenceNumber] and
      (__ \ "vehicleReferenceNumber").writeNullable[VehicleReferenceNumber] and
      (__ \ "crossingId").write[String] and
      (__ \ "createdDateTime").write[Instant]and
      (__ \ "captureMethod").write[MrnCaptureMethod] and
      (__ \ "captureDateTime").write[Instant]
    ) (unlift(Transit.unapply))

  implicit val format: OFormat[Transit] =
    OFormat(reads, writes)

}

