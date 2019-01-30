package uk.gov.hmrc.transitmovementapi.helpers

trait WireMockConfig {
  me: AdditionalAppConfig with WireMockSupport =>

  additionalAppConfig ++= setWireMockPort(
    "common-transit-convention"
  ) + ("auditing.consumer.baseUri.port" -> mockServerPort)

  private def setWireMockPort(services: String*): Map[String, Any] =
    services.foldLeft(Map.empty[String, Any]) {
      case (map, service) => map + (s"microservice.services.$service.port" -> mockServerPort)
    }

}
