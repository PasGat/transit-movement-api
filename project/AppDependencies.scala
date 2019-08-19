import sbt._

object AppDependencies {

  import play.core.PlayVersion
  import play.sbt.PlayImport._

  private val bootstrapPlay26Version = "0.44.0"
  private val domainVersion          = "5.2.0"
  private val hmrcTestVersion        = "3.9.0-play-26"
  private val scalaTestVersion       = "3.0.4"
  private val wireMockVersion        = "2.22.0"
  private val mockitoVersion         = "2.15.0"
  private val scalaTestPlusVersion   = "3.1.2"
  private val scalacheckVersion      = "1.14.0"
  private val refinedVersion         = "0.9.2"
  private val scalacheckRegexVersion = "0.1.1"

  val compile: Seq[ModuleID] = Seq(
    ws,
    "uk.gov.hmrc"       %% "bootstrap-play-26"              % bootstrapPlay26Version,
    "eu.timepit"        %% "refined"                        % refinedVersion,
    "uk.gov.hmrc"       %% "domain"                         % domainVersion
  )

  trait TestDependencies {
    lazy val scope: String = "test"
    lazy val test: Seq[ModuleID] = ???
  }

  object Test {
    def apply(): Seq[ModuleID] = new TestDependencies {
      override lazy val test: Seq[ModuleID] = testCommon(scope)
    }.test
  }

  object IntegrationTest {
    def apply(): Seq[ModuleID] = new TestDependencies {
      override lazy val scope: String = "it"

      override lazy val test: Seq[ModuleID] = testCommon(scope) ++ Seq(
        "com.github.tomakehurst" % "wiremock-jre8" % wireMockVersion % scope
      )
    }.test
  }

  private def testCommon(scope: String) = Seq(
    "uk.gov.hmrc"            %% "hmrctest"              % hmrcTestVersion        % scope,
    "com.typesafe.play"      %% "play-test"             % PlayVersion.current    % scope,
    "org.scalacheck"         %% "scalacheck"            % scalacheckVersion      % scope,
    "org.scalatest"          %% "scalatest"             % scalaTestVersion       % scope,
    "org.scalatestplus.play" %% "scalatestplus-play"    % scalaTestPlusVersion   % scope,
    "org.mockito"             % "mockito-core"          % mockitoVersion         % scope,
    "wolfendale"             %% "scalacheck-gen-regexp" % scalacheckRegexVersion % scope
  )

  def apply(): Seq[ModuleID] = compile ++ Test() ++ IntegrationTest()
}