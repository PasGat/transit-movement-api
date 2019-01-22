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

package uk.gov.hmrc.transitmovementapi.services

import com.google.inject.{Inject, Singleton}
import uk.gov.hmrc.transitmovementapi.models.api.{TransitId, TransitSubmission}
import uk.gov.hmrc.transitmovementapi.models.data.Transit
import uk.gov.hmrc.transitmovementapi.repositories.{CrossingRepository, TransitRepository}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TransitService @Inject()(transitRepository: TransitRepository,
                               crossingRepository: CrossingRepository)
                              (implicit ec: ExecutionContext) {

  def getByCrossingId(crossingId: String)(implicit hc: HeaderCarrier): Future[List[Transit]] = transitRepository.getByCrossingId(crossingId)

  def submitTransit(transitSubmission: TransitSubmission)(implicit hc: HeaderCarrier): Future[TransitId] = {
    for {
      _       <- crossingRepository.get(transitSubmission.crossingId) // preliminary check for the existence of the crossing
      transit <- transitRepository.create(transitSubmission)
    } yield TransitId.fromTransit(transit)
  }

}
