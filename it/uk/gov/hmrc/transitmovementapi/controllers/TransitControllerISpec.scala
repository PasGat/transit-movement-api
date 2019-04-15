package uk.gov.hmrc.transitmovementapi.controllers

import play.api.libs.json.Json
import uk.gov.hmrc.transitmovementapi.errorhandler.ErrorResponse
import uk.gov.hmrc.transitmovementapi.helpers.{BaseISpec, DataGenerator, WireMockConfig, WireMockSupport}
import uk.gov.hmrc.transitmovementapi.stubs.CtcStubs

class TransitControllerISpec extends BaseISpec with DataGenerator with CtcStubs with WireMockSupport with WireMockConfig {

  "POST /transits" when {
    "return 200 OK for a successful transit submission" in {
      withTransit {
        transit =>
          validTransitSubmission(transit.submission)
          val result = callRoute(fakeRequest(routes.TransitController.submit()).withBody(Json.toJson(transit.submission)))

          status(result) shouldBe OK
      }
    }

    "return 200 OK even when CTC is returning 409 Conflict" in {
      withTransit {
        transit =>
          conflictErrorRequest(transit.submission)
          val result = callRoute(fakeRequest(routes.TransitController.submit()).withBody(Json.toJson(transit.submission)))

          status(result) shouldBe OK
      }
    }

    "return 400 BAD_REQUEST for bad body" in {
      withTransit { transit =>
        invalidTransitSubmission(transit.submission)
        val result = callRoute(fakeRequest(routes.TransitController.submit()).withBody(Json.toJson(transit.submission)))

        status(result) shouldBe BAD_REQUEST
        contentAsJson(result) shouldBe Json.toJson(ErrorResponse.BadRequest)
      }
    }

    "return 500 INTERNAL_SERVER_ERROR" in {
      withTransit { transit =>
        internalServerErrorRequest(transit.submission)
        val result = callRoute(fakeRequest(routes.TransitController.submit()).withBody(Json.toJson(transit.submission)))

        status(result) shouldBe INTERNAL_SERVER_ERROR
        contentAsJson(result) shouldBe Json.toJson(ErrorResponse.InternalServerError)
      }
    }
  }
}