package uk.gov.hmrc.transitmovementapi.controllers

import play.api.libs.json.Json
import uk.gov.hmrc.transitmovementapi.models.api.CrossingId
import uk.gov.hmrc.transitmovementapi.helpers.BaseISpec
import uk.gov.hmrc.transitmovementapi.helpers.DataGenerator

class CrossingControllerISpec extends BaseISpec with DataGenerator {
  "POST /crossings" should {
    "return 200 OK with a crossing ID for a successful crossing submission" in {
      withCrossing {
        crossing =>
          val result = callRoute(fakeRequest(routes.CrossingController.submit()).withBody(Json.toJson(toCrossingSubmission(crossing))))

          status(result) shouldBe OK
      }
    }

    "return 200 OK with an existing crossing ID if the crossing already exists" in {
      withMongoCrossingRecord {
        crossing =>
          val result = callRoute(fakeRequest(routes.CrossingController.submit()).withBody(Json.toJson(crossing.submission)))

          status(result) shouldBe OK
          contentAsJson(result).as[CrossingId] shouldBe crossing.id
      }
    }
  }
}