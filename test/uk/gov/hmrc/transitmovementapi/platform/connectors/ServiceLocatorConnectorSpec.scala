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

package uk.gov.hmrc.transitmovementapi.platform.connectors

import java.net.URL

import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import play.api.libs.json.Writes
import uk.gov.hmrc.transitmovementapi.platform.models.ServiceDetails
import uk.gov.hmrc.transitmovementapi.utils.BaseSpec
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads, HttpResponse}
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ServiceLocatorConnectorSpec extends BaseSpec {

  trait Setup {
    implicit val hc: HeaderCarrier = HeaderCarrier()
    val serviceLocatorException = new RuntimeException

    val connector = new ServiceLocatorConnector(
      mock[HttpClient],
      new URL("https://SERVICE_LOCATOR"),
      "common-transit-convention",
      "http://common-transit-convention.protected.mdtp") {
      override val handlerOK: HttpResponse => Boolean = _ => true
      override val handlerError: PartialFunction[Throwable, Boolean] = {
        case _ => false
      }
    }
  }

  "registering the application with the service locator" should {

    val registration = ServiceDetails(
      serviceName = "common-transit-convention",
      serviceUrl = "http://common-transit-convention.protected.mdtp",
      metadata = Some(Map("third-party-api" -> "true")))

    "register the JSON API Definition into the Service Locator" in new Setup {

      when(
        connector.http.POST(any[String](), any[ServiceDetails](), any[Seq[(String, String)]]())(
          any[Writes[ServiceDetails]](),
          any[HttpReads[HttpResponse]](),
          any(),
          any()))
        .thenReturn(Future.successful(HttpResponse(200)))

      connector.register().futureValue shouldBe true

      verify(connector.http)
        .POST(
          ArgumentMatchers.eq("https://SERVICE_LOCATOR/registration"),
          ArgumentMatchers.eq(registration),
          ArgumentMatchers.eq(Seq("Content-Type" -> "application/json"))
        )(any[Writes[ServiceDetails]](), any[HttpReads[HttpResponse]](), any(), any())

    }

    "fail registering in service locator" in new Setup {

      when(
        connector.http.POST(any[String](), any[ServiceDetails](), any[Seq[(String, String)]]())(
          any[Writes[ServiceDetails]](),
          any[HttpReads[HttpResponse]](),
          any(),
          any()))
        .thenReturn(Future.failed(serviceLocatorException))

      connector.register().futureValue shouldBe false

      verify(connector.http)
        .POST(
          ArgumentMatchers.eq("https://SERVICE_LOCATOR/registration"),
          ArgumentMatchers.eq(registration),
          ArgumentMatchers.eq(Seq("Content-Type" -> "application/json"))
        )(any[Writes[ServiceDetails]](), any[HttpReads[HttpResponse]](), any(), any())

    }

  }
}
