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

import javax.inject.{Inject, Named}
import play.api.Logger
import play.api.http.{ContentTypes, HeaderNames}
import uk.gov.hmrc.transitmovementapi.platform.models.ServiceDetails
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.{ExecutionContext, Future}

class ServiceLocatorConnector @Inject()(
                                         val http: HttpClient,
                                         @Named("service-locator.baseUrl") serviceLocatorBaseUrl: URL,
                                         @Named("appName") appName: String,
                                         @Named("appUrl") appUrl: String)(implicit executionContext: ExecutionContext) {

  val logger: Logger = Logger(this.getClass)

  val metadata: Option[Map[String, String]] = Some(Map("third-party-api" -> "true"))

  val handlerOK: HttpResponse => Boolean = { _ =>
    logger.info("Service is registered on the service locator"); true
  }

  val handlerError: PartialFunction[Throwable, Boolean] = {
    case e: Throwable => logger.error(s"Service could not register on the service locator", e); false
  }

  def register(): Future[Boolean] = {
    implicit val hc: HeaderCarrier = new HeaderCarrier

    val registration = ServiceDetails(appName, appUrl, metadata)

    http
      .POST[ServiceDetails, HttpResponse](
      s"$serviceLocatorBaseUrl/registration",
      registration,
      Seq(HeaderNames.CONTENT_TYPE -> ContentTypes.JSON))
      .map(handlerOK)
      .recover(handlerError)

  }
}
