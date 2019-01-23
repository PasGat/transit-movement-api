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

import uk.gov.hmrc.transitmovementapi.models.api.{CrossingSubmission, TransitMetadata, TransitSubmission}
import uk.gov.hmrc.transitmovementapi.models.data.{Crossing, Transit}

trait DataTransformer {
  /**
    * From an API perspective, transforming from an entity to a submission is not required - at least for now.
    * However, extracting the id from an entity to a CrossingId object is needed at times.
    * Same for transforming from a request to an entity which will be persisted.
    */

  def toTransitSubmission(transit: Transit, transitMetadata: TransitMetadata): TransitSubmission = {
    TransitSubmission(
      movementReferenceNumber = transit.movementReferenceNumber,
      vehicleReferenceNumber = transit.vehicleReferenceNumber,
      captureMethod = transit.captureMethod,
      captureDateTime = transit.captureDateTime,
      auditData = transitMetadata
    )
  }

  def toCrossingSubmission(crossing: Crossing): CrossingSubmission = {
    CrossingSubmission(
      departureDateTime = crossing.departureDateTime,
      departurePort = crossing.departurePort,
      destinationPort = crossing.destinationPort,
      duration = crossing.duration,
      carrier = crossing.carrier,
      captureDateTime = crossing.captureDateTime
    )
  }

}
