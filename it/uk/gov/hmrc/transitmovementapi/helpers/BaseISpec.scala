package uk.gov.hmrc.transitmovementapi.helpers

trait BaseISpec extends ApplicationSetupISpec {
  self: DataGenerator =>

  def withNoSetup(test: => Unit): Unit = test

  def withTransit(test: TransitSubmissionWithId => Unit): Unit = test(getRandomTransitSubmission)
}
