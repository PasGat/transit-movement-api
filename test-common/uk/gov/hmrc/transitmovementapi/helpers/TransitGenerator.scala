/*
 * Copyright 2019 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.transitmovementapi.helpers

import java.time.Instant

import eu.timepit.refined.auto._
import org.scalacheck.Gen
import play.api.libs.json.Json
import uk.gov.hmrc.transitmovementapi.models.api.transit.{CrossingWithDuration, TransitSubmission}
import uk.gov.hmrc.transitmovementapi.models.types.ModelTypes._
import uk.gov.hmrc.transitmovementapi.models.types._
import wolfendale.scalacheck.regexp.RegexpGen

trait TransitGenerator {

  private[helpers] def getRandomTransitSubmission: TransitSubmission =
    transitSubmissionGenerator().sample.get

  private def transitSubmissionGenerator(): Gen[TransitSubmission] =
    for {
      mrn                        <- movementReferenceNumberGenerator
      transitUnitType            <- transitUnitTypeGenerator
      transitUnitReferenceNumber <- transitUnitReferenceNumberGenerator
      transitMetadata            <- transitMetadataGenerator()
      crossingDetails            <- crossingDetailsGenerator()
    } yield TransitSubmission(crossingDetails, mrn, transitUnitType, transitUnitReferenceNumber, transitMetadata)

  private def transitMetadataGenerator(): Gen[TransitMetadata] =
    for {
      captureMethod   <- captureMethodGenerator
      captureDateTime <- Gen.const(Instant.now)
    } yield TransitMetadata(captureMethod, captureDateTime)

  private def crossingDetailsGenerator(): Gen[CrossingWithDuration] =
    for {
      departureDateTime <- Gen.option(Instant.now)
      departurePort     <- departurePortGenerator
      destinationPort   <- destinationPortGenerator
      duration          <- Gen.choose(0, 1000).map(i => Json.toJson(i).as[Duration])
      carrier           <- carrierGenerator
    } yield CrossingWithDuration(departurePort, destinationPort, duration, carrier)

  private def movementReferenceNumberGenerator: Gen[MovementReferenceNumber] =
    RegexpGen.from("\\d{2}[a-zA-Z]{2}[a-zA-Z0-9]{14}").map(mrn => Json.toJson(mrn).as[MovementReferenceNumber])

  private def transitUnitTypeGenerator: Gen[TransitUnitType] =
    Gen.oneOf[TransitUnitType]("Container", "Trailer", "Vehicle")

  private def transitUnitReferenceNumberGenerator: Gen[TransitUnitReferenceNumber] =
    RegexpGen
      .from("""^[\-A-Z0-9 ]{1,20}$""")
      .map(transitUnitReferenceNumber => Json.toJson(transitUnitReferenceNumber).as[TransitUnitReferenceNumber])

  private def captureMethodGenerator: Gen[MrnCaptureMethod] =
    Gen.oneOf[MrnCaptureMethod]("Scan", "Manual")

  private def departurePortGenerator: Gen[DeparturePort] =
    Gen.oneOf[DeparturePort]("Calais", "Coquelles", "Dublin", "Dunkirk")

  private def destinationPortGenerator: Gen[DestinationPort] =
    Gen.oneOf[DestinationPort]("Dover", "Folkestone", "Holyhead")

  private def carrierGenerator: Gen[Carrier] =
    Gen.oneOf[Carrier]("Irish Ferries", "DFDS", "Eurotunnel", "P&O", "Stena Line")
}
