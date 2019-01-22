package uk.gov.hmrc.transitmovementapi.utils

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, Suite}
import uk.gov.hmrc.play.it.Port

trait WireMockSupport extends BeforeAndAfterAll with BeforeAndAfterEach {
  me: Suite =>

  val mockServerHost: String = "localhost"
  val mockServerPort: Int = Port.randomAvailable
  val mockServerUrl = s"http://$mockServerHost:$mockServerPort"

  val mockServer = new WireMockServer(wireMockConfig().port(mockServerPort))

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    WireMock.configureFor("localhost", mockServerPort)
    mockServer.start()
  }

  override protected def beforeEach(): Unit = {
    super.beforeEach()
  }

  override protected def afterEach(): Unit = {
    WireMock.reset()
    super.afterEach()
  }

  override protected def afterAll(): Unit = {
    mockServer.stop()
    super.afterAll()
  }

}
