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

import play.api.libs.functional.syntax._
import play.api.libs.json._
import reactivemongo.bson.BSONObjectID
import uk.gov.hmrc.transitmovementapi.models.types.ModelTypes._
import uk.gov.hmrc.transitmovementapi.models.types._

case class Crossing(crossingId: CrossingId = BSONObjectID.generate().stringify,
                    departureDateTime: Instant,
                    departurePort: DeparturePort,
                    destinationPort: DestinationPort,
                    duration: Int,
                    carrier: Carrier,
                    createdDateTime: Instant = Instant.now)

object Crossing {

  implicit val reads: Reads[Crossing] = (
    (__ \ "_id").read[String] and
      (__ \ "departureDateTime").read[Instant] and
      (__ \ "departurePort").read[DeparturePort] and
      (__ \ "destinationPort").read[DestinationPort] and
      (__ \ "duration").read[Int] and
      (__ \ "carrier").read[Carrier] and
      (__ \ "createdDateTime").read[Instant]
    ) (Crossing.apply _)

  implicit val writes: OWrites[Crossing] = (
    (__ \ "_id").write[String] and
      (__ \ "departureDateTime").write[Instant] and
      (__ \ "departurePort").write[DeparturePort] and
      (__ \ "destinationPort").write[DestinationPort] and
      (__ \ "duration").write[Int] and
      (__ \ "carrier").write[Carrier] and
      (__ \ "createdDateTime").write[Instant]
    ) (unlift(Crossing.unapply))

  implicit val format: OFormat[Crossing] =
    OFormat(reads, writes)

}
