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

import play.api.libs.json.{Format, Json}
import uk.gov.hmrc.transitmovementapi.models.api.TransitSubmission
import uk.gov.hmrc.transitmovementapi.models.types.ModelTypes.{CrossingId, MovementReferenceNumber}
import uk.gov.hmrc.transitmovementapi.models.types._

case class TransitEvent(userId: String,
                        deviceId: String,
                        mrn: MovementReferenceNumber,
                        crossingId: CrossingId)

object TransitEvent {
  def fromSubmission(transit: TransitSubmission, crossingId: CrossingId): TransitEvent = TransitEvent(
    userId = transit.userId,
    deviceId = transit.deviceId,
    mrn = transit.movementReferenceNumber,
    crossingId = crossingId
  )

  implicit val format: Format[TransitEvent] = Json.format[TransitEvent]
}
