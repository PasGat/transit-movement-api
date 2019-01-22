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

import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito.when
import play.api.libs.json.Json
import play.api.test.Helpers._
import uk.gov.hmrc.transitmovementapi.errorhandler.ErrorResponse.{CrossingNotFound, DuplicateTransit, InternalServerError}
import uk.gov.hmrc.transitmovementapi.errorhandler._
import uk.gov.hmrc.transitmovementapi.models.api.TransitId
import uk.gov.hmrc.transitmovementapi.services.TransitService
import uk.gov.hmrc.transitmovementapi.utils.{DataGenerator, DataSetupSpec, DataTransformer}
import uk.gov.hmrc.http.InternalServerException

import scala.concurrent.Future


class TransitControllerSpec extends DataSetupSpec with DataGenerator with DataTransformer  {

  val mockTransitService: TransitService = mock[TransitService]
  val controller: TransitController = new TransitController(mockTransitService)

  "submit" should {
    "return 200 OK if the transit submission is successful" in {
      withTransit {
        transit =>
          when(mockTransitService.submitTransit(any())(any())).thenReturn(Future.successful(TransitId.fromTransit(transit)))

          val result = controller.submit(fakeRequest.withBody(Json.toJson(toTransitSubmission(transit))))

          status(result) shouldBe OK
          contentAsJson(result) shouldBe Json.toJson(TransitId.fromTransit(transit))
      }
    }

    "return 400 BAD_REQUEST if the json body is invalid" in {
      withTransit {
        transit =>
          when(mockTransitService.submitTransit(any())(any())).thenReturn(Future.successful(TransitId.fromTransit(transit)))

          val result = controller.submit(fakeRequest.withBody(Json.obj("invalid" -> "json")))

          status(result) shouldBe BAD_REQUEST
      }
    }

    "return 404 NOT_FOUND if the crossing does not exist for the supplied crossingId" in {
      withTransit {
        transit =>
          when(mockTransitService.submitTransit(any())(any())).thenReturn(Future.failed(CrossingNotFoundException("Crossing does not exist")))

          val result = controller.submit(fakeRequest.withBody(Json.toJson(toTransitSubmission(transit))))

          status(result) shouldBe NOT_FOUND
          contentAsJson(result) shouldBe Json.toJson(CrossingNotFound)
      }
    }

    "return 409 TRANSIT_ALREADY_EXISTS if the crossing does not exist for the supplied crossingId" in {
      withTransit {
        transit =>
          when(mockTransitService.submitTransit(any())(any())).thenReturn(Future.failed(DuplicateTransitException("Transit already exists")))

          val result = controller.submit(fakeRequest.withBody(Json.toJson(toTransitSubmission(transit))))

          status(result) shouldBe CONFLICT
          contentAsJson(result) shouldBe Json.toJson(DuplicateTransit)
      }
    }

    "return 500 INTERNAL_SERVER_ERROR if the transit creation fails" in {
      withTransit {
        transit =>
          when(mockTransitService.submitTransit(any())(any())).thenReturn(Future.failed(new InternalServerException("Failed to create transit")))

          val result = controller.submit(fakeRequest.withBody(Json.toJson(toTransitSubmission(transit))))

          status(result) shouldBe INTERNAL_SERVER_ERROR
          contentAsJson(result) shouldBe Json.toJson(InternalServerError)
      }
    }

  }

}
