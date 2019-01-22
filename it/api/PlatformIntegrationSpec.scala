package api

import akka.stream.Materializer
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.core.WireMockConfiguration._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfterEach, TestData}
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.http.LazyHttpErrorHandler
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.{Application, Mode}
import uk.gov.hmrc.transitmovementapi.platform.controllers.DocumentationController
import uk.gov.hmrc.transitmovementapi.platform.models.ServiceDetails
import uk.gov.hmrc.play.test.UnitSpec

/**
  * Testcase to verify the capability of integration with the API platform.
  *
  * 1, To integrate with API platform the service needs to register itself to the service locator by calling the /registration endpoint and providing
  * - application name
  * - application url
  *
  * 2a, To expose API's to Third Party Developers, the service needs to define the APIs in a definition.json and make it available under api/definition GET endpoint
  * 2b, For all of the endpoints defined in the definition.json a documentation.xml needs to be provided and be available under api/documentation/[version]/[endpoint name] GET endpoint
  * Example: api/documentation/1.0/Fetch-Some-Data
  */
class PlatformIntegrationSpec
  extends UnitSpec with GuiceOneAppPerTest with MockitoSugar with ScalaFutures with BeforeAndAfterEach {

  val stubHost = "localhost"
  val stubPort = sys.env.getOrElse("WIREMOCK_SERVICE_LOCATOR_PORT", "11112").toInt
  val wireMockServer = new WireMockServer(wireMockConfig().port(stubPort))

  override def newAppForTest(testData: TestData): Application =
    GuiceApplicationBuilder()
      .disable[com.kenshoo.play.metrics.PlayModule]
      .configure("run.mode" -> "Stub")
      .configure(Map(
        "appName" -> "application-name",
        "appUrl" -> "http://microservice-name.service",
        "metrics.enabled" -> false,
        "auditing.enabled" -> false,
        "microservice.services.service-locator.host" -> stubHost,
        "microservice.services.service-locator.port" -> stubPort,
        "microservice.services.service-locator.enabled" -> true,
        "microservice.services.metrics.graphite.enabled" -> false
      ))
      .in(Mode.Test)
      .build()

  override def beforeEach() {
    wireMockServer.start()
    WireMock.configureFor(stubHost, stubPort)
    stubFor(post(urlMatching("/registration")).willReturn(aResponse().withStatus(204)))
  }

  trait Setup {
    implicit lazy val actorSystem = app.actorSystem
    implicit lazy val materializer: Materializer = app.materializer

    val documentationController = new DocumentationController(LazyHttpErrorHandler, Seq("1234567890")) {}
    val request = FakeRequest()
  }

  "microservice" should {
    "register itself to service-locator" in new Setup {
      def regPayloadStringFor(serviceName: String, serviceUrl: String): String =
        Json.toJson(ServiceDetails(serviceName, serviceUrl, Some(Map("third-party-api" -> "true")))).toString

      verify(
        1,
        postRequestedFor(urlMatching("/registration"))
          .withHeader("content-type", equalTo("application/json"))
          .withRequestBody(equalTo(regPayloadStringFor("application-name", "http://microservice-name.service")))
      )
    }

    "provide definition endpoint and documentation endpoint for each api" in new Setup {
      def normalizeEndpointName(endpointName: String): String = endpointName.replaceAll(" ", "-")

      def verifyDocumentationPresent(version: String, endpointName: String) {
        withClue(s"Getting documentation version '$version' of endpoint '$endpointName'") {
          val documentationResult = documentationController.documentation(version, endpointName)(request)
          status(documentationResult) shouldBe 200
        }
      }

      val result = documentationController.definition()(request)
      status(result) shouldBe 200

      val jsonResponse = jsonBodyOf(result).futureValue

      val versions: Seq[String] = (jsonResponse \\ "version") map (_.as[String])
      val endpointNames: Seq[Seq[String]] =
        (jsonResponse \\ "endpoints").map(_ \\ "endpointName").map(_.map(_.as[String]))

      versions
        .zip(endpointNames)
        .flatMap {
          case (version, endpoint) => {
            endpoint.map(endpointName => (version, endpointName))
          }
        }
        .foreach { case (version, endpointName) => verifyDocumentationPresent(version, endpointName) }
    }

    "provide definition including the whitelisted app ids" in new Setup {
      val result = documentationController.definition()(request)
      status(result) shouldBe 200

      val jsonResponse = jsonBodyOf(result).futureValue

      val whitelistedIds: Seq[String] =
        (jsonResponse \ "api" \ "versions" \ 0 \ "access" \ "whitelistedApplicationIds").as[Seq[String]]

      whitelistedIds should contain("1234567890")
    }

    "provide raml documentation" in new Setup {
      val result = documentationController.raml("1.0", "application.raml")(request)

      status(result) shouldBe 200
      bodyOf(result).futureValue should startWith("#%RAML 1.0")
    }
  }

  override protected def afterEach() = {
    wireMockServer.stop()
    wireMockServer.resetMappings()
  }
}