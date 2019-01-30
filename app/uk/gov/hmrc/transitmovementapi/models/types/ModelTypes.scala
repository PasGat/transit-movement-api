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
import eu.timepit.refined.boolean.{And, Not, Or}
import eu.timepit.refined.char.{Digit, UpperCase, Whitespace}
import eu.timepit.refined.collection.{Empty, Forall}
import eu.timepit.refined.generic.Equal
import eu.timepit.refined.numeric.GreaterEqual
import eu.timepit.refined.string.MatchesRegex

object ModelTypes {
  /**
    * Since there are no hard-defined implicit values for Refined's special types, a custom one had to be created.
    *
    * In order to be able to serialize/deserialize these types, import the refinedReads and refinedWrites from the 'types'
    * package object.
    */

  type Id = String
  type MovementReferenceNumber = Refined[String, ValidMovementReferenceNumber]
  type VehicleReferenceNumber  = Refined[String, ValidVehicleReferenceNumber]
  type DeparturePort           = Refined[String, ValidDeparturePort]
  type Duration                = Refined[Int, GreaterEqual[shapeless.nat._0]]
  type DestinationPort         = Refined[String, ValidDestinationPort]
  type Carrier                 = Refined[String, ValidCarrier]
  type MrnCaptureMethod        = Refined[String, ValidMRNCaptureMethod]

  private type ValidMRNCaptureMethod        = Or[Equal[W.`"SCAN"`.T], Equal[W.`"MANUAL"`.T]]
  private type ValidMovementReferenceNumber = MatchesRegex[W.`"""\\d{2}[a-zA-Z]{2}[a-zA-Z0-9]{14}"""`.T]
  private type ValidVehicleReferenceNumber  = And[Not[Empty], Forall[Or[Whitespace, Or[UpperCase, Digit]]]]
  private type ValidDeparturePort           = MatchesRegex[W.`"""(Calais)|(Coquelles)|(Dublin)|(Dunkirk)"""`.T]
  private type ValidDestinationPort         = MatchesRegex[W.`"(Dover)|(Folkestone)|(Holyhead)"`.T]
  private type ValidCarrier                 = MatchesRegex[W.`"""(Irish Ferries)|(DFDS)|(Eurotunnel)|(P&O)|(Stena Line)"""`.T]
}
