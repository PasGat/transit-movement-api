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

package uk.gov.hmrc.transitmovementapi.controllers

import play.api.Logger
import play.api.libs.json._
import play.api.mvc.{Controller, Request, RequestHeader, Result}
import uk.gov.hmrc.http.{BadRequestException, HeaderCarrier}
import uk.gov.hmrc.play.HeaderCarrierConverter

import scala.concurrent.{ExecutionContext, Future}

abstract class BaseApiController extends Controller {

  protected val logger: Logger = play.api.Logger(this.getClass)

  implicit def hc(implicit rh: RequestHeader): HeaderCarrier =
    HeaderCarrierConverter.fromHeadersAndSessionAndRequest(rh.headers, request = Some(rh))

  def withValidJson[T](f: T => Future[Result])(implicit ec: ExecutionContext, hc: HeaderCarrier, request: Request[JsValue], r: Reads[T]): Future[Result] = {
    request.body.validate[T] match {
      case JsSuccess(t, _) => f(t)
      case JsError(errors) => Future.failed(new BadRequestException(errors.toString()))
    }
  }
}