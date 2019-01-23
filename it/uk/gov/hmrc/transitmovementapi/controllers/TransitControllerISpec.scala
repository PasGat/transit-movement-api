package uk.gov.hmrc.transitmovementapi.controllers

import play.api.libs.json.Json
import uk.gov.hmrc.transitmovementapi.errorhandler.ErrorResponse.CrossingNotFound
import uk.gov.hmrc.transitmovementapi.utils.{DataGenerator, DataSetupISpec}

class TransitControllerISpec extends DataSetupISpec with DataGenerator {

  "POST /crossings/{crossingId}/transits" should {
    "return 204 NO_CONTENT for a successful transits submission" in {
      withMongoCrossingRecord {
        crossingDetails =>
          withTransit(Some(crossingDetails.id.crossingId)) {
            transit =>
              withTransitMetadata{
                transitMetadata =>
                  val result = callRoute(fakeRequest(routes.TransitController.submit(crossingDetails.id.crossingId)).withBody(Json.toJson(List(toTransitSubmission(transit, transitMetadata)))))

                  status(result) shouldBe NO_CONTENT
              }
          }
      }
    }

    "return 404 NOT_FOUND if the crossing does not exist for the supplied crossingId" in {
      withTransit() { transit =>
        withTransitMetadata {
          transitMetadata =>
            val result = callRoute(fakeRequest(routes.TransitController.submit("test-crossing-id")).withBody(Json.toJson(List(toTransitSubmission(transit, transitMetadata)))))

            status(result) shouldBe NOT_FOUND
            contentAsJson(result) shouldBe Json.toJson(CrossingNotFound)
        }
      }
    }
  }

}