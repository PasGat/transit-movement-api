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

package uk.gov.hmrc.transitmovementapi.connectors

import javax.inject.{Inject, Singleton}
import uk.gov.hmrc.http.{HeaderCarrier, NotFoundException}
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import uk.gov.hmrc.play.config.ServicesConfig
import uk.gov.hmrc.transitmovementapi.errorhandler.CrossingNotFoundException
import uk.gov.hmrc.transitmovementapi.models.api.{CrossingId, CrossingSubmission, TransitSubmission}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CtcConnector @Inject()(appConfig: ServicesConfig)(implicit httpClient: HttpClient, ec: ExecutionContext) {
  lazy val ctcUrl: String = appConfig.baseUrl("common-transit-convention")

  def postCrossing(crossingSubmission: CrossingSubmission)(implicit headerCarrier: HeaderCarrier): Future[CrossingId] = {
    val url = ctcUrl + "/crossings"
    httpClient.POST[CrossingSubmission, CrossingId](url, crossingSubmission)
  }

  def postTransit(crossingId: String, transit: TransitSubmission)(implicit headerCarrier: HeaderCarrier): Future[Unit] = {
    val url = ctcUrl + s"/crossings/$crossingId/transits"
    httpClient.POST(url, transit)
      .map(_ => ())
      .recoverWith {
        case _: NotFoundException =>
          Future.failed(CrossingNotFoundException("Crossing not found"))
      }
  }
}
