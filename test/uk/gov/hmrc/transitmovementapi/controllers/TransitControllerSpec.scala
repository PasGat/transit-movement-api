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
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.transitmovementapi.errorhandler.ErrorResponse.{CrossingNotFound, InternalServerError}
import uk.gov.hmrc.transitmovementapi.errorhandler._
import uk.gov.hmrc.transitmovementapi.services.TransitService
import uk.gov.hmrc.transitmovementapi.helpers.{DataGenerator, BaseSpec, DataTransformer}

import scala.concurrent.Future


class TransitControllerSpec extends BaseSpec with DataGenerator with DataTransformer {

  val mockTransitService: TransitService = mock[TransitService]
  val mockAuditConnector: AuditConnector = mock[AuditConnector]
  val controller: TransitController = new TransitController(mockTransitService)

  "submit" should {
    "return 204 NO_CONTENT if the transit submission is successful" in {
      withTransit {
        transit =>
          withTransitMetadata {
            metadata =>
              when(mockTransitService.submitTransits(any(), any())(any())).thenReturn(Future.successful(()))

              val result = controller.submit("test-crossing-id")(fakeRequest.withBody(Json.toJson(List(toTransitSubmission(transit, metadata)))))

              status(result) shouldBe NO_CONTENT
          }
      }
    }

    "return 400 BAD_REQUEST if the json body is invalid" in {
      withNoSetup {
        when(mockTransitService.submitTransits(any(), any())(any())).thenReturn(Future.successful(()))

        val result = controller.submit("test-crossing-id")(fakeRequest.withBody(Json.obj("invalid" -> "json")))

        status(result) shouldBe BAD_REQUEST
      }
    }

    "return 404 CROSSING_NOT_FOUND if the crossing does not exist for the supplied crossingId" in {
      withTransit {
        transit =>
          withTransitMetadata {
            metadata =>
              when(mockTransitService.submitTransits(any(), any())(any())).thenReturn(Future.failed(CrossingNotFoundException("Crossing does not exist")))

              val result = controller.submit("test-crossing-id")(fakeRequest.withBody(Json.toJson(List(toTransitSubmission(transit, metadata)))))

              status(result) shouldBe NOT_FOUND
              contentAsJson(result) shouldBe Json.toJson(CrossingNotFound)
          }
      }
    }

    "return 500 INTERNAL_SERVER_ERROR if any errors occur server side when handling the submitted transit data" in {
      withTransit {
        transit =>
          withTransitMetadata {
            metadata =>
              when(mockTransitService.submitTransits(any(), any())(any())).thenReturn(Future.failed(new InternalServerException("Failed to create transit")))

              val result = controller.submit("test-crossing-id")(fakeRequest.withBody(Json.toJson(List(toTransitSubmission(transit, metadata)))))

              status(result) shouldBe INTERNAL_SERVER_ERROR
              contentAsJson(result) shouldBe Json.toJson(InternalServerError)
          }
      }
    }

  }

}
