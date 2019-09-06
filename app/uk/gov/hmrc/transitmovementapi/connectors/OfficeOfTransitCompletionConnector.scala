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

import java.net.URL

import javax.inject.{Inject, Named, Singleton}
import uk.gov.hmrc.http.{HeaderCarrier, Upstream4xxResponse}
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import uk.gov.hmrc.transitmovementapi.models.api.crossing.CrossingSubmission

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class OfficeOfTransitCompletionConnector @Inject()(
  @Named("office-of-transit-completion.baseUrl") officeOfTransitCompletionUrl: URL
)(implicit httpClient:                                                         HttpClient, ec: ExecutionContext) {

  def postCrossing(
    crossingSubmission:     CrossingSubmission
  )(implicit headerCarrier: HeaderCarrier): Future[Unit] = {
    val url = officeOfTransitCompletionUrl + s"/office-of-transit-completion/crossing"

    httpClient
      .POST(url, crossingSubmission)
      .map(_ => ())
      .recover {
        case Upstream4xxResponse(_, 409, _, _) => ()
      }
  }

}
