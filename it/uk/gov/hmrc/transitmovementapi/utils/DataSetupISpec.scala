package uk.gov.hmrc.transitmovementapi.utils

import play.api.libs.json.Json
import uk.gov.hmrc.transitmovementapi.controllers.routes
import uk.gov.hmrc.transitmovementapi.models.api.{CrossingId, CrossingSubmission, TransitId, TransitSubmission}
import uk.gov.hmrc.transitmovementapi.models.data.{Crossing, Transit}

trait DataSetupISpec extends BaseISpec with DataTransformer {
  self: DataGenerator =>

  sealed case class TransitDetails (id: TransitId,   submission: TransitSubmission)
  sealed case class CrossingDetails(id: CrossingId, submission: CrossingSubmission)

  def withMongoTransitAndCrossingRecords(test: (TransitDetails, CrossingDetails) => Unit): Unit = {
    val crossingSubmission = getRandomCrossingSubmission
    val crossingId         = submitCrossing(crossingSubmission)

    val transitSubmission = getRandomTransitSubmission(Some(crossingId.crossingId))
    val transitId         = submitTransit(transitSubmission)

    test(TransitDetails(transitId, transitSubmission), CrossingDetails(crossingId, crossingSubmission))
  }

  def withMongoTransitRecord(test: TransitDetails => Unit): Unit   = {
    val transitSubmission = getRandomTransitSubmission()
    val transitId         = submitTransit(transitSubmission)

    test(TransitDetails(transitId, transitSubmission))
  }

  def withMongoCrossingRecord(test: CrossingDetails => Unit): Unit = {
    val crossingSubmission = getRandomCrossingSubmission
    val crossingId         = submitCrossing(crossingSubmission)

    test(CrossingDetails(crossingId, crossingSubmission))
  }

  def withNoSetup(test: => Unit): Unit = test

  def withTransit(withDefaultCrossingId: Option[String] = None)(test: Transit => Unit): Unit  = test(getRandomTransit(withDefaultCrossingId))

  def withCrossing(test: Crossing => Unit): Unit = test(getRandomCrossing)

  private def submitTransit(transit: TransitSubmission): TransitId     = {
    val transitSubmissionCall = callRoute(fakeRequest(routes.TransitController.submit()).withBody(Json.toJson(transit)))
    status(transitSubmissionCall) shouldBe OK
    contentAsJson(transitSubmissionCall).as[TransitId]
  }

  private def submitCrossing(crossing: CrossingSubmission): CrossingId = {
    val crossingSubmissionCall = callRoute(fakeRequest(routes.CrossingController.submit()).withBody(Json.toJson(crossing)))
    status(crossingSubmissionCall) shouldBe OK
    contentAsJson(crossingSubmissionCall).as[CrossingId]
  }
}
