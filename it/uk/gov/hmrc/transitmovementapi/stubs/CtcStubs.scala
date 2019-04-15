package uk.gov.hmrc.transitmovementapi.stubs

import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.libs.json.Json
import reactivemongo.bson.BSONObjectID
import uk.gov.hmrc.transitmovementapi.models.api.{CtcTransitSubmission, TransitSubmission}

trait CtcStubs {
  def internalServerErrorRequest(transitSubmission: TransitSubmission): StubMapping = {
    stubFor(post(urlPathEqualTo("/common-transit-convention/transits"))
      .withRequestBody(equalToJson(Json.stringify(Json.toJson(CtcTransitSubmission.fromSubmissionRequest(transitSubmission)))))
      .willReturn(serverError()))
  }

  def invalidTransitSubmission(transitSubmission: TransitSubmission): StubMapping = {
    stubFor(post(urlPathEqualTo("/common-transit-convention/transits"))
      .withRequestBody(equalToJson(Json.stringify(Json.toJson(CtcTransitSubmission.fromSubmissionRequest(transitSubmission)))))
      .willReturn(badRequest()))
  }

  def validTransitSubmission(transitSubmission: TransitSubmission): StubMapping = {
    stubFor(post(urlPathEqualTo("/common-transit-convention/transits"))
      .withRequestBody(equalToJson(Json.stringify(Json.toJson(CtcTransitSubmission.fromSubmissionRequest(transitSubmission)))))
      .willReturn(ok(s"""{ "transitId": ${BSONObjectID.generate().stringify} """)))
  }

  def conflictErrorRequest(transitSubmission: TransitSubmission): StubMapping = stubFor(post(urlPathEqualTo("/common-transit-convention/transits"))
    .withRequestBody(equalToJson(Json.stringify(Json.toJson(CtcTransitSubmission.fromSubmissionRequest(transitSubmission)))))
    .willReturn(aResponse.withStatus(409)))
}
