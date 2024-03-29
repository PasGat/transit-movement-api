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

package uk.gov.hmrc.transitmovementapi.errorhandler

import play.api.Logger
import play.api.mvc.Result
import uk.gov.hmrc.http.{BadRequestException, HeaderCarrier, NotFoundException}
import uk.gov.hmrc.transitmovementapi.errorhandler.ErrorResponse._

import scala.concurrent.{ExecutionContext, Future}

trait ErrorHandling {

  def handleErrors(f: => Future[Result])(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Result] =
    f.recover {
      case e: NotFoundException =>
        Logger.error(s"Resource not found: ${e.getMessage}")
        NotFound.toResult

      case e: BadRequestException =>
        Logger.error(s"Bad request: ${e.getMessage}")
        BadRequest.toResult

      case e: Exception =>
        Logger.error(s"Internal server error: ${e.getMessage}", e)
        InternalServerError.toResult
    }

}
