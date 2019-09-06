package uk.gov.hmrc.transitmovementapi.stubs

import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.libs.json.Json
import uk.gov.hmrc.transitmovementapi.models.api.crossing.CrossingSubmission

trait OotCompletionStubs {
  def mockCrossingSubmission(response: Int, crossingSubmission: CrossingSubmission): StubMapping =
    stubFor(
      post(urlPathEqualTo("/office-of-transit-completion/crossing"))
        .withRequestBody(equalToJson(Json.stringify(Json.toJson(crossingSubmission))))
        .willReturn(aResponse().withStatus(response)))

}
