package uk.gov.hmrc.transitmovementapi.controllers

import play.api.libs.json.Json
import uk.gov.hmrc.transitmovementapi.errorhandler.ErrorResponse.{CrossingNotFound, DuplicateTransit}
import uk.gov.hmrc.transitmovementapi.models.api.TransitId
import uk.gov.hmrc.transitmovementapi.utils.{DataGenerator, DataSetupISpec}

class TransitControllerISpec extends DataSetupISpec with DataGenerator {

  "POST /crossings/transits" should {
    "return 200 OK with a transit ID for a successful transit submission" in {
      withMongoCrossingRecord {
            crossingDetails =>
              withTransit(Some(crossingDetails.id.crossingId)) {
                transit =>
                  val result = callRoute(fakeRequest(routes.TransitController.submit()).withBody(Json.toJson(toTransitSubmission(transit))))

                  status(result) shouldBe OK

                  val createdTransitId = contentAsJson(result).as[TransitId]
                  contentAsJson(result) shouldBe Json.toJson(createdTransitId)
              }
      }
    }

    "return 409 TRANSIT_ALREADY_EXISTS for a duplicate transit submission" in {
      withMongoTransitAndCrossingRecords{ (transitDetails, _) =>
        val duplicateTransit = callRoute(fakeRequest(routes.TransitController.submit()).withBody(Json.toJson(transitDetails.submission)))

        status(duplicateTransit) shouldBe CONFLICT
        contentAsJson(duplicateTransit) shouldBe Json.toJson(DuplicateTransit)
      }
    }

    "return 404 NOT_FOUND if the crossing does not exist for the supplied crossingId" in {
      withTransit() { transit =>
        val result = callRoute(fakeRequest(routes.TransitController.submit()).withBody(Json.toJson(toTransitSubmission(transit))))

        status(result) shouldBe NOT_FOUND
        contentAsJson(result) shouldBe Json.toJson(CrossingNotFound)
      }
    }
  }

}