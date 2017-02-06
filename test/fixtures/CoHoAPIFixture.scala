/*
 * Copyright 2017 HM Revenue & Customs
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

package fixtures

import models.external.{AreaOfIndustry, CHROAddress, CoHoCompanyDetailsModel}

trait CoHoAPIFixture {

  val validCoHoCompanyDetailsResponse = CoHoCompanyDetailsModel(
    registrationID = "12345",
    companyName = "Test Company",
    areasOfIndustry = Seq(
      AreaOfIndustry(
        sicCode = "100",
        description = "Chips"
      ),
      AreaOfIndustry(
        sicCode = "101",
        description = "Fish"
      )
    )
  )

  val validCoHoROAddressResponse = CHROAddress(
    premises = "14",
    addressLine1 = "St Test Walker",
    addressLine2 = Some("Testley"),
    locality = "Testford",
    country = Some("UK"),
    poBox = Some("TE BO"),
    postalCode = Some("TE1 1ST"),
    region = Some("Testshire")
  )

}
