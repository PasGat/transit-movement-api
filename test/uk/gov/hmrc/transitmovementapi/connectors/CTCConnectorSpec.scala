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

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import uk.gov.hmrc.http.Upstream4xxResponse
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import uk.gov.hmrc.transitmovementapi.helpers.{BaseSpec, DataGenerator}
import uk.gov.hmrc.transitmovementapi.models.api.CtcTransitSubmission

import scala.concurrent.Future

class CTCConnectorSpec extends BaseSpec with DataGenerator {

  val mockHttpClient: HttpClient = mock[HttpClient]
  val ctcConnector: CtcConnector = new CtcConnector(new URL("http://localhost:9266"))(mockHttpClient, ec)

  "postTransit" should {
    "not fail if 409 is given" in {
      withTransit {
        transit =>
          when(mockHttpClient.POST(any(), any(), any())(any(), any(), any(), any())).thenReturn(Future.failed(Upstream4xxResponse("", 409, 409)))

          val result: Unit = await(ctcConnector.postTransit(CtcTransitSubmission.fromSubmissionRequest(transit.submission)))

          result shouldBe ()
      }
    }
  }
}
