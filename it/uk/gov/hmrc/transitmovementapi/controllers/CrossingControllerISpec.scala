package uk.gov.hmrc.transitmovementapi.controllers

import play.api.libs.json.Json
import uk.gov.hmrc.transitmovementapi.errorhandler.ErrorResponse
import uk.gov.hmrc.transitmovementapi.helpers.{BaseISpec, DataGenerator, WireMockConfig, WireMockSupport}
import uk.gov.hmrc.transitmovementapi.stubs.OotCompletionStubs

class CrossingControllerISpec extends BaseISpec with DataGenerator with OotCompletionStubs with WireMockSupport with WireMockConfig {

  "POST /crossing" when {
    "return 204 NO_CONTENT for a successful crossing submission" in {
      withCrossing { crossing =>
        mockCrossingSubmission(204, crossing)

        val result = callRoute(fakeRequest(routes.CrossingController.submit()).withBody(Json.toJson(crossing)))

        status(result) shouldBe NO_CONTENT
      }
    }

    "return 204 NO_CONTENT even when office-of-transit-completion is returning 409 Conflict" in {
      withCrossing { crossing =>
        mockCrossingSubmission(409, crossing)

        val result = callRoute(fakeRequest(routes.CrossingController.submit()).withBody(Json.toJson(crossing)))

        status(result) shouldBe NO_CONTENT
      }
    }

    "return 400 BAD_REQUEST for bad body" in {
      withCrossing { crossing =>
        mockCrossingSubmission(400, crossing)

        val result = callRoute(fakeRequest(routes.CrossingController.submit()).withBody(Json.toJson(crossing)))

        status(result)        shouldBe BAD_REQUEST
        contentAsJson(result) shouldBe Json.toJson(ErrorResponse.BadRequest)
      }
    }

    "return 500 INTERNAL_SERVER_ERROR" in {
      withCrossing { crossing =>
        mockCrossingSubmission(500, crossing)

        val result = callRoute(fakeRequest(routes.CrossingController.submit()).withBody(Json.toJson(crossing)))

        status(result)        shouldBe INTERNAL_SERVER_ERROR
        contentAsJson(result) shouldBe Json.toJson(ErrorResponse.InternalServerError)
      }
    }
  }
}
