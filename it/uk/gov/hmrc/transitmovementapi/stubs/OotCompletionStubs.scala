package uk.gov.hmrc.transitmovementapi.stubs

import java.util.UUID

import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import eu.timepit.refined.refineMV
import play.api.libs.json.Json
import uk.gov.hmrc.transitmovementapi.models.api.OfficeOfTransitSubmission
import uk.gov.hmrc.transitmovementapi.models.api.crossing.CrossingSubmission
import uk.gov.hmrc.transitmovementapi.models.api.transit.TransitSubmission

trait OotCompletionStubs {
  def internalServerErrorRequest(transitSubmission: TransitSubmission): StubMapping =
    stubFor(
      post(urlPathEqualTo("/office-of-transit-completion/transits"))
        .withRequestBody(equalToJson(Json.stringify(Json.toJson(OfficeOfTransitSubmission.fromSubmissionRequest(transitSubmission)))))
        .willReturn(serverError()))

  def invalidTransitSubmission(transitSubmission: TransitSubmission): StubMapping =
    stubFor(
      post(urlPathEqualTo("/office-of-transit-completion/transits"))
        .withRequestBody(equalToJson(Json.stringify(Json.toJson(OfficeOfTransitSubmission.fromSubmissionRequest(transitSubmission)))))
        .willReturn(badRequest()))

  def validTransitSubmission(transitSubmission: TransitSubmission): StubMapping =
    stubFor(
      post(urlPathEqualTo("/office-of-transit-completion/transits"))
        .withRequestBody(equalToJson(Json.stringify(Json.toJson(OfficeOfTransitSubmission.fromSubmissionRequest(transitSubmission)))))
        .willReturn(ok(s"""{ "transitId": ${UUID.randomUUID().toString} """)))

  def conflictErrorRequest(transitSubmission: TransitSubmission): StubMapping =
    stubFor(
      post(urlPathEqualTo("/office-of-transit-completion/transits"))
        .withRequestBody(equalToJson(Json.stringify(Json.toJson(OfficeOfTransitSubmission.fromSubmissionRequest(transitSubmission)))))
        .willReturn(aResponse.withStatus(409)))

  def mockCrossingSubmission(response: Int, crossingSubmission: CrossingSubmission): StubMapping =
    stubFor(
      post(urlPathEqualTo("/office-of-transit-completion/crossing"))
        .withRequestBody(equalToJson(Json.stringify(Json.toJson(crossingSubmission))))
        .willReturn(aResponse().withStatus(response)))

}
