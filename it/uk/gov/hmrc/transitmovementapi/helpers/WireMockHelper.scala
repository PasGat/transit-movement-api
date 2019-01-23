package uk.gov.hmrc.transitmovementapi.helpers

import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.client.{MappingBuilder, ResponseDefinitionBuilder}
import com.github.tomakehurst.wiremock.stubbing.StubMapping

trait WireMockHelper {
  this: ApplicationSetupISpec =>

  def stub(method: MappingBuilder, response: ResponseDefinitionBuilder): StubMapping =
    stubFor(method.willReturn(response))
}
