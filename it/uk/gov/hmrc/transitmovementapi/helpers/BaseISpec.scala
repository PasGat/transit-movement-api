package uk.gov.hmrc.transitmovementapi.helpers

import play.api.libs.json.Json
import uk.gov.hmrc.transitmovementapi.controllers.routes
import uk.gov.hmrc.transitmovementapi.models.api.TransitSubmission
import uk.gov.hmrc.transitmovementapi.models.data.Transit

trait BaseISpec extends ApplicationSetupISpec with DataTransformer {
  self: DataGenerator =>

  sealed case class TransitDetails(submission: TransitSubmission)

  def withMongoTransit(test: TransitDetails => Unit): Unit = {
    val transitSubmission = getRandomTransitSubmission(???)

    test(TransitDetails(transitSubmission))
  }

  def withNoSetup(test: => Unit): Unit = test

  def withTransit(withDefaultCrossingId: Option[String] = None)(test: Transit => Unit): Unit = test(getRandomTransit(withDefaultCrossingId))

  def withTransitMetadata(test: TransitMetadata => Unit): Unit = test(getRandomMetadata)

  private def submitTransit(transit: TransitSubmission): Unit = {
    val transitSubmissionCall = callRoute(fakeRequest(routes.TransitController.submit()).withBody(Json.toJson(transit)))
    status(transitSubmissionCall) shouldBe NO_CONTENT
  }
}
