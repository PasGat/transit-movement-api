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
import uk.gov.hmrc.transitmovementapi.errorhandler.ErrorResponse._
import uk.gov.hmrc.http.{BadRequestException, HeaderCarrier, NotFoundException}

import scala.concurrent.{ExecutionContext, Future}

trait ErrorHandling {

  def handleErrors(f: => Future[Result])(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Result] = {
    f.recover {
      case _: NotFoundException =>
        Logger.info("Resource not found")
        NotFound.toResult

      case _: BadRequestException =>
        Logger.info("Bad request")
        BadRequest().toResult

      case _: CrossingNotFoundException =>
        Logger.info("Crossing not found")
        CrossingNotFound.toResult

      case e: DuplicateTransitException =>
        Logger.info(e.message)
        DuplicateTransit.toResult

      case r: MalformedBodyException =>
        Logger.info(r.getMessage)
        BadRequest("Badly formatted body data").toResult

      case e: Exception =>
        Logger.warn(s"Internal server error: ${e.getMessage}", e)
        InternalServerError.toResult
    }
  }

}