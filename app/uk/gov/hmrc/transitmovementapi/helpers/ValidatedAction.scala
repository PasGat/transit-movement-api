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

import javax.inject.Inject
import play.api.libs.json.Json
import play.api.mvc.Results._
import play.api.mvc._
import uk.gov.hmrc.transitmovementapi.errorhandler.ErrorResponse.InvalidAcceptHeader

import scala.concurrent.{ExecutionContext, Future}
import scala.util.matching.Regex
import scala.util.matching.Regex.Match

class ValidatedAction @Inject()(cc: ControllerComponents) extends ActionBuilder[Request, AnyContent] {

  val validateVersion: String => Boolean = _ == "1.0"

  val validateContentType: String => Boolean = _ == "json"

  val matchHeader: String => Option[Match] = new Regex("""^application/vnd[.]{1}hmrc[.]{1}(.*?)[+]{1}(.*)$""", "version", "contenttype") findFirstMatchIn _

  val acceptHeaderValidationRules: Option[String] => Boolean =
    _ flatMap (a => matchHeader(a) map (res => validateContentType(res.group("contenttype")) && validateVersion(res.group("version")))) getOrElse false

  override val parser: BodyParser[AnyContent] = cc.parsers.defaultBodyParser
  override protected val executionContext: ExecutionContext = cc.executionContext

  override def invokeBlock[A](request: Request[A], block: Request[A] => Future[Result]): Future[Result] = {
    if (acceptHeaderValidationRules(request.headers.get("Accept")))
      block(request)
    else
      Future.successful(Status(InvalidAcceptHeader.httpStatusCode)(Json.toJson(InvalidAcceptHeader)))
  }

}
