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

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import play.api.libs.json.Json
import play.api.test.Helpers._
import uk.gov.hmrc.http.InternalServerException
import uk.gov.hmrc.transitmovementapi.errorhandler.ErrorResponse.InternalServerError
import uk.gov.hmrc.transitmovementapi.helpers.{BaseSpec, DataGenerator, ValidatedAction}
import uk.gov.hmrc.transitmovementapi.services.CrossingService

import scala.concurrent.Future

class CrossingControllerSpec extends BaseSpec with DataGenerator {

  val mockCrossingService: CrossingService    = mock[CrossingService]
  val controller:          CrossingController = new CrossingController(cc, mockCrossingService, new ValidatedAction(cc), false)

  "submit" should {
    "return 204 NO_CONTENT if the crossing submission is successful" in {
      withCrossing { crossing =>
        when(mockCrossingService.submitCrossing(any())(any())).thenReturn(Future.successful(()))

        val result = controller.submit()(fakeRequest.withBody(Json.toJson(crossing)))

        status(result) shouldBe NO_CONTENT
      }
    }
  }

  "return 400 BAD_REQUEST if the json body is invalid" in {
    withNoSetup {
      when(mockCrossingService.submitCrossing(any())(any())).thenReturn(Future.successful(()))

      val result = controller.submit()(fakeRequest.withBody(Json.obj("invalid" -> "json")))

      status(result) shouldBe BAD_REQUEST
    }
  }

  "return 500 INTERNAL_SERVER_ERROR if any errors occur server side when handling the submitted crossing data" in {
    withCrossing { crossing =>
      when(mockCrossingService.submitCrossing(any())(any()))
        .thenReturn(Future.failed(new InternalServerException("Failed to process crossing information")))

      val result = controller.submit()(fakeRequest.withBody(Json.toJson(crossing)))

      status(result)        shouldBe INTERNAL_SERVER_ERROR
      contentAsJson(result) shouldBe Json.toJson(InternalServerError)
    }
  }
}
