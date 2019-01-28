package uk.gov.hmrc.transitmovementapi.controllers

import play.api.libs.json.Json
import uk.gov.hmrc.transitmovementapi.errorhandler.ErrorResponse
import uk.gov.hmrc.transitmovementapi.helpers.{BaseISpec, DataGenerator}

class TransitControllerISpec extends BaseISpec with DataGenerator {

  "POST /crossings/{crossingId}/transits" should {
    "return 204 NO_CONTENT for a successful transits submission" in {
      withMongoTransit {
        transit =>
          withTransit(Some(???)) {
            transit =>
              withTransitMetadata{
                transitMetadata =>
                  val result = callRoute(fakeRequest(routes.TransitController.submit()).withBody(Json.toJson(List(toTransitSubmission(transit, transitMetadata)))))

                  status(result) shouldBe NO_CONTENT
              }
          }
      }
    }

    "return 404 NOT_FOUND if the crossing does not exist for the supplied crossingId" in {
      withTransit() { transit =>
        withTransitMetadata {
          transitMetadata =>
            val result = callRoute(fakeRequest(routes.TransitController.submit()).withBody(Json.toJson(List(toTransitSubmission(transit, transitMetadata)))))

            status(result) shouldBe NOT_FOUND
            contentAsJson(result) shouldBe Json.toJson(ErrorResponse.CrossingNotFound)
        }
      }
    }
  }

}