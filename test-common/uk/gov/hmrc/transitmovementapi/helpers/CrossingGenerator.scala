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
import uk.gov.hmrc.transitmovementapi.models.api.crossing.{Crossing, CrossingSubmission, TransitUnit}
import uk.gov.hmrc.transitmovementapi.models.types.ModelTypes._
import uk.gov.hmrc.transitmovementapi.models.types._
import wolfendale.scalacheck.regexp.RegexpGen

import scala.util.Random

trait CrossingGenerator {

  private[helpers] def getRandomCrossingSubmission: CrossingSubmission =
    crossingSubmissionGenerator().sample.get

  private def crossingSubmissionGenerator(): Gen[CrossingSubmission] =
    for {
      crossing     <- crossingGenerator
      transitUnits <- transitUnitsGenerator
    } yield CrossingSubmission(crossing, transitUnits)

  private def crossingGenerator: Gen[Crossing] =
    for {
      carrier              <- carrierGenerator
      scheduledArrivalTime <- Gen.const(Instant.now)
      departurePort        <- departurePortGenerator
      destinationPort      <- destinationPortGenerator
    } yield Crossing(carrier, scheduledArrivalTime, departurePort, destinationPort)

  private def movementReferenceNumberGenerator: Gen[MovementReferenceNumber] =
    RegexpGen.from("\\d{2}[a-zA-Z]{2}[a-zA-Z0-9]{14}").map(mrn => Json.toJson(mrn).as[MovementReferenceNumber])

  private def transitUnitsGenerator: Gen[List[TransitUnit]] =
    for {
      transitUnits <- Gen.listOfN(Random.nextInt(100), transitUnitGenerator)
    } yield transitUnits

  private def transitUnitGenerator: Gen[TransitUnit] =
    for {
      movementReferenceNumbers   <- Gen.listOfN(Random.nextInt(10), movementReferenceNumberGenerator)
      transitUnitType            <- transitUnitTypeGenerator
      transitUnitReferenceNumber <- transitUnitReferenceNumberGenerator
    } yield TransitUnit(transitUnitType, transitUnitReferenceNumber, movementReferenceNumbers)

  private def transitUnitTypeGenerator: Gen[TransitUnitType] =
    Gen.oneOf[TransitUnitType]("Container", "Trailer", "Vehicle")

  private def transitUnitReferenceNumberGenerator: Gen[TransitUnitReferenceNumber] =
    RegexpGen
      .from("""^[\-A-Z0-9 ]{1,20}$""")
      .map(transitUnitReferenceNumber => Json.toJson(transitUnitReferenceNumber).as[TransitUnitReferenceNumber])

  private def departurePortGenerator: Gen[DeparturePort] =
    Gen.oneOf[DeparturePort](
      "Amsterdam",
      "Bilbao",
      "Botlek",
      "Brevik",
      "Calais",
      "Coquelles",
      "Cuxhaven",
      "Dublin",
      "Dunkirk",
      "Esbjerg",
      "Gothenburg",
      "Hirtshals",
      "Hook of Holland",
      "Leixoes",
      "Moerdijk",
      "Rosslare",
      "Rotterdam",
      "Santander",
      "Zeebrugge"
    )

  private def destinationPortGenerator: Gen[DestinationPort] =
    Gen.oneOf[DestinationPort](
      "Belfast",
      "Blyth",
      "Dover",
      "Felixstowe",
      "Fishguard",
      "Folkestone",
      "Harwich",
      "Heysham",
      "Holyhead",
      "Hull",
      "Immingham",
      "Killingholme",
      "Liverpool",
      "Newcastle",
      "Pembroke",
      "Purfleet",
      "Teesport",
      "Thamesport",
      "Tilbury"
    )

  private def carrierGenerator: Gen[Carrier] =
    Gen.oneOf[Carrier]("A2B", "Brittany", "Cobelfret", "DFDS", "Eurotunnel", "Irish Ferries", "P&O", "Seatruck", "Stena Line")
}
