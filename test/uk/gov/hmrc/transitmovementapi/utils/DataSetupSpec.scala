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

package uk.gov.hmrc.transitmovementapi.utils

import uk.gov.hmrc.transitmovementapi.models.data.{Crossing, Transit}

trait DataSetupSpec extends BaseSpec {
  self: DataGenerator =>

  def withTransit(test: Transit => Unit): Unit   = test(getRandomTransit())

  def withCrossing(test: Crossing => Unit): Unit = test(getRandomCrossing)

  def withTransitAndCrossing(test: (Transit, Crossing) => Unit): Unit =
    withTransit {
      transit =>
        withCrossing {
          crossing =>
            test(transit, crossing)
        }
    }

  def withNoSetup(test: => Unit): Unit = test
}