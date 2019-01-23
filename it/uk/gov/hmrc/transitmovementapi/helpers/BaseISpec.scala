package uk.gov.hmrc.transitmovementapi.helpers

import play.api.libs.json.Json
import uk.gov.hmrc.transitmovementapi.controllers.routes
import uk.gov.hmrc.transitmovementapi.models.api.{CrossingId, CrossingSubmission, TransitMetadata, TransitSubmission}
import uk.gov.hmrc.transitmovementapi.models.data.{Crossing, Transit}

trait BaseISpec extends ApplicationSetupISpec with DataTransformer {
  self: DataGenerator =>

  sealed case class TransitDetails(submission: TransitSubmission)

  sealed case class CrossingDetails(id: CrossingId, submission: CrossingSubmission)

  def withMongoTransitAndCrossingRecords(test: (TransitDetails, CrossingDetails) => Unit): Unit = {
    val crossingSubmission = getRandomCrossingSubmission
    val crossingId = submitCrossing(crossingSubmission)

    val transitSubmission = getRandomTransitSubmission(Some(crossingId.crossingId))
  
    test(TransitDetails(transitSubmission), CrossingDetails(crossingId, crossingSubmission))
  }

  def withMongoTransitRecord(test: TransitDetails => Unit): Unit = {
    val transitSubmission = getRandomTransitSubmission()

    test(TransitDetails(transitSubmission))
  }

  def withMongoCrossingRecord(test: CrossingDetails => Unit): Unit = {
    val crossingSubmission = getRandomCrossingSubmission
    val crossingId = submitCrossing(crossingSubmission)

    test(CrossingDetails(crossingId, crossingSubmission))
  }

  def withNoSetup(test: => Unit): Unit = test

  def withTransit(withDefaultCrossingId: Option[String] = None)(test: Transit => Unit): Unit = test(getRandomTransit(withDefaultCrossingId))

  def withCrossing(test: Crossing => Unit): Unit = test(getRandomCrossing)

  def withTransitMetadata(test: TransitMetadata => Unit): Unit = test(getRandomMetadata)

  private def submitTransit(crossingId: String, transit: TransitSubmission): Unit = {
    val transitSubmissionCall = callRoute(fakeRequest(routes.TransitController.submit(crossingId)).withBody(Json.toJson(List(transit))))
    status(transitSubmissionCall) shouldBe NO_CONTENT
  }

  private def submitCrossing(crossing: CrossingSubmission): CrossingId = {
    val crossingSubmissionCall = callRoute(fakeRequest(routes.CrossingController.submit()).withBody(Json.toJson(crossing)))
    status(crossingSubmissionCall) shouldBe OK
    contentAsJson(crossingSubmissionCall).as[CrossingId]
  }
}
