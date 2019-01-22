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

import com.google.inject._
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.transitmovementapi.errorhandler.ErrorHandling
import uk.gov.hmrc.transitmovementapi.models.api.TransitSubmission
import uk.gov.hmrc.transitmovementapi.services.TransitService
import uk.gov.hmrc.transitmovementapi.utils.HeaderValidator

import scala.concurrent.ExecutionContext

@Singleton
class TransitController @Inject()(transitService: TransitService)(implicit ec: ExecutionContext) extends BaseApiController with HeaderValidator with ErrorHandling {

  def submit: Action[JsValue] = validateAccept(acceptHeaderValidationRules).async(parse.json) { implicit request =>
    handleErrors {
      withValidJson[TransitSubmission] {
        transitSubmission =>
          transitService.submitTransit(transitSubmission).map(transitId => Ok(Json.toJson(transitId)))
      }
    }
  }

}