package uk.gov.hmrc.transitmovementapi.controllers

import play.api.libs.json.Json
import uk.gov.hmrc.transitmovementapi.errorhandler.ErrorResponse
import uk.gov.hmrc.transitmovementapi.helpers.{BaseISpec, DataGenerator, WireMockConfig, WireMockSupport}
import uk.gov.hmrc.transitmovementapi.stubs.OotCompletionStubs

class TransitControllerISpec extends BaseISpec with DataGenerator with OotCompletionStubs with WireMockSupport with WireMockConfig {

  "POST /transits" when {
    "return 204 NO_CONTENT for a successful transit submission" in {
      withTransit { transit =>
        validTransitSubmission(transit)
        val result = callRoute(fakeRequest(routes.TransitController.submit()).withBody(Json.toJson(transit)))

        status(result) shouldBe NO_CONTENT
      }
    }

    "return 204 NO_CONTENT even when office-of-transit-completion is returning 409 Conflict" in {
      withTransit { transit =>
        conflictErrorRequest(transit)
        val result = callRoute(fakeRequest(routes.TransitController.submit()).withBody(Json.toJson(transit)))

        status(result) shouldBe NO_CONTENT
      }
    }

    "return 400 BAD_REQUEST for bad body" in {
      withTransit { transit =>
        invalidTransitSubmission(transit)
        val result = callRoute(fakeRequest(routes.TransitController.submit()).withBody(Json.toJson(transit)))

        status(result)        shouldBe BAD_REQUEST
        contentAsJson(result) shouldBe Json.toJson(ErrorResponse.BadRequest)
      }
    }

    "return 500 INTERNAL_SERVER_ERROR" in {
      withTransit { transit =>
        internalServerErrorRequest(transit)
        val result = callRoute(fakeRequest(routes.TransitController.submit()).withBody(Json.toJson(transit)))

        status(result)        shouldBe INTERNAL_SERVER_ERROR
        contentAsJson(result) shouldBe Json.toJson(ErrorResponse.InternalServerError)
      }
    }
  }
}
