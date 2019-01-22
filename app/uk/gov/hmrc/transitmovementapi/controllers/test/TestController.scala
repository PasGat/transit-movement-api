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

package uk.gov.hmrc.transitmovementapi.controllers.test

import com.google.inject._
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.transitmovementapi.services.test.TestService
import uk.gov.hmrc.play.bootstrap.controller.BaseController

import scala.concurrent.ExecutionContext

@Singleton
class TestController @Inject()(testService: TestService)(implicit ec: ExecutionContext) extends BaseController {

  def clearAll: Action[AnyContent] = Action.async { implicit request =>
    testService.clearAll.map(_ => NoContent).recover {
      case e: Exception => InternalServerError(s"Failed to clear data: ${e.getMessage}")
    }
  }
}