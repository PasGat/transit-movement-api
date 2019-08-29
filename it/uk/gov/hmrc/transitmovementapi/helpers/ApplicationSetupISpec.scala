package uk.gov.hmrc.transitmovementapi.helpers

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.{Status => _, _}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http._
import play.api.inject.Injector
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc._
import play.api.test._
import play.api.{Application, Mode}

import scala.concurrent.ExecutionContext.global
import scala.concurrent.{ExecutionContext, Future}

abstract class ApplicationSetupISpec
  extends WordSpec with GuiceOneAppPerSuite with BeforeAndAfterEach with BeforeAndAfterAll with Matchers
    with Inspectors with ScalaFutures with DefaultAwaitTimeout with Writeables with EssentialActionCaller
    with RouteInvokers with LoneElement with Inside with OptionValues with Results with Status with HeaderNames
    with MimeTypes with HttpProtocol with HttpVerbs with ResultExtractors with WireMockHelper with AdditionalAppConfig {

  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  def injector: Injector = app.injector

  def cc: ControllerComponents = injector.instanceOf[ControllerComponents]

  implicit def ec: ExecutionContext = global

  override implicit val patienceConfig: PatienceConfig =
    PatienceConfig(timeout = Span(10, Seconds), interval = Span(50, Millis))

  val acceptHeader: (String, String) = "Accept" -> "application/vnd.hmrc.1.0+json"

  def fakeRequest(call: Call): FakeRequest[AnyContentAsEmpty.type] = FakeRequest(call).withHeaders(acceptHeader)

  additionalAppConfig ++= Map(
    "metrics.enabled" -> false,
    "api.apiPlatformUrl" -> "api.service.hmrc.gov.uk",
    "auditing.enabled" -> true
  )

  override def fakeApplication(): Application =
    GuiceApplicationBuilder()
      .disable[com.kenshoo.play.metrics.PlayModule]
      .configure(additionalAppConfig.toMap)
      .in(Mode.Test)
      .build()

  def callRoute[A](req: Request[A])(implicit app: Application, w: Writeable[A]): Future[Result] = {
    route(app, req) match {
      case None => fail("Route does not exist")
      case Some(fResult) =>
        fResult.recoverWith {
          case t: Throwable => throw t
        }
    }
  }
}
