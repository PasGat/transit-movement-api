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

package uk.gov.hmrc.transitmovementapi.models.types

import eu.timepit.refined._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric.GreaterEqual
import eu.timepit.refined.string.MatchesRegex

object ModelTypes {

  /**
    * In order to be able to serialize/deserialize these types, import the refinedReads and refinedWrites from the 'types'
    * package object.
    */
  type MovementReferenceNumber    = Refined[String, ValidMovementReferenceNumber]
  type TransitUnitReferenceNumber = Refined[String, ValidTransitUnitReferenceNumber]
  type TransitUnitType            = Refined[String, ValidTransitUnitType]
  type DeparturePort              = Refined[String, ValidDeparturePort]
  type DestinationPort            = Refined[String, ValidDestinationPort]
  type Carrier                    = Refined[String, ValidCarrier]
  type MrnCaptureMethod           = Refined[String, ValidMrnCaptureMethod]

  private type ValidMrnCaptureMethod =
    MatchesRegex[W.`"(Scan)|(Manual)"`.T]
  private type ValidMovementReferenceNumber =
    MatchesRegex[W.`"""\\d{2}[a-zA-Z]{2}[a-zA-Z0-9]{14}"""`.T]
  private type ValidTransitUnitType =
    MatchesRegex[W.`"(Vehicle)|(Container)|(Trailer)"`.T]
  private type ValidTransitUnitReferenceNumber =
    MatchesRegex[W.`"""^[\\-A-Z0-9 ]{1,20}$"""`.T]
  private type ValidDeparturePort =
    MatchesRegex[
      W.`"""(Amsterdam)|(Bilbao)|(Botlek)|(Brevik)|(Calais)|(Coquelles)|(Cuxhaven)|(Dublin)|(Dunkirk)|(Esbjerg)|(Gothenburg)|(Hirtshals)|(Hook of Holland)|(Leixoes)|(Moerdijk)|(Rosslare)|(Rotterdam)|(Santander)|(Zeebrugge)"""`.T]
  private type ValidDestinationPort =
    MatchesRegex[
      W.`"(Belfast)|(Blyth)|(Dover)|(Felixstowe)|(Fishguard)|(Folkestone)|(Harwich)|(Heysham)|(Holyhead)|(Hull)|(Immingham)|(Killingholme)|(Liverpool)|(Newcastle)|(Pembroke)|(Purfleet)|(Teesport)|(Thamesport)|(Tilbury)"`.T]
  private type ValidCarrier = MatchesRegex[
    W.`"""(A2B)|(Brittany)|(Cobelfret)|(Seatruck)|(Irish Ferries)|(DFDS)|(Eurotunnel)|(P&O)|(Stena Line)"""`.T
  ]

}
