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
import uk.gov.hmrc.transitmovementapi.models.api.{CrossingId, CrossingSubmission}
import uk.gov.hmrc.transitmovementapi.models.data.Crossing
import uk.gov.hmrc.transitmovementapi.repositories.CrossingRepository
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CrossingService @Inject()(crossingRepository: CrossingRepository)(implicit ec: ExecutionContext) {

  def submitCrossing(crossingSubmission: CrossingSubmission)(implicit hc: HeaderCarrier): Future[CrossingId] = {
    crossingRepository.getCrossing(crossingSubmission).flatMap {
      case Some(crossing) => Future.successful(CrossingId.fromCrossing(crossing))
      case _              => crossingRepository.create(Crossing.fromCrossingSubmission(crossingSubmission))
    }
  }

  def getAllCrossings()(implicit hc: HeaderCarrier): Future[List[Crossing]] = crossingRepository.getAll()

  def getCrossing(crossingId: String)(implicit hc: HeaderCarrier): Future[Crossing] = crossingRepository.get(crossingId)

}
