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
import javax.inject.Named
import play.api.libs.json.JsValue
import play.api.mvc.{Action, ControllerComponents}
import uk.gov.hmrc.transitmovementapi.errorhandler.ErrorHandling
import uk.gov.hmrc.transitmovementapi.helpers.ValidatedAction
import uk.gov.hmrc.transitmovementapi.models.api.transit.TransitSubmission
import uk.gov.hmrc.transitmovementapi.services.TransitService

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TransitController @Inject()(
  cc:                                                                                               ControllerComponents,
  transitService:                                                                                   TransitService,
  validatedAction:                                                                                  ValidatedAction,
  @Named("featureFlags.officeOfTransitCompletionStubEnabled") officeOfTransitCompletionStubEnabled: Boolean)(implicit ec: ExecutionContext)
    extends BaseApiController(cc)
    with ErrorHandling {

  def submit(): Action[JsValue] = validatedAction.async(parse.json) { implicit request =>
    handleErrors {
      withValidJson[TransitSubmission] { transit =>
        if (officeOfTransitCompletionStubEnabled) Future.successful(NoContent) else transitService.submitTransit(transit).map(_ => NoContent)
      }
    }
  }
}
