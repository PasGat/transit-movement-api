package uk.gov.hmrc.transitmovementapi.helpers
import uk.gov.hmrc.transitmovementapi.models.api.crossing.CrossingSubmission
import uk.gov.hmrc.transitmovementapi.models.api.transit.TransitSubmission

trait BaseISpec extends ApplicationSetupISpec {
  self: DataGenerator =>

  def withNoSetup(test: => Unit): Unit = test

  def withCrossing(test: CrossingSubmission => Unit): Unit = test(getRandomCrossingSubmission)

  def withTransit(test: TransitSubmission => Unit): Unit = test(getRandomTransitSubmission)
}
