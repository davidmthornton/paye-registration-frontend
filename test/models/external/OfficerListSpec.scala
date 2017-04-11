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

package models.external

import play.api.libs.json.Json
import testHelpers.PAYERegSpec

class OfficerListSpec extends PAYERegSpec {
  val tstOfficerList = OfficerList(
    items = Seq(
      Officer(
        name = "John Jones",
        role = "cic-manager",
        resignedOn = None,
        appointmentLink = None
      ),
      Officer(
        name = "Bob Smythe",
        role = "corporate-director",
        resignedOn = None,
        appointmentLink = None
      )
    )
  )

  val tstOfficerListJson = Json.parse(
    """{
      |  "items" : [ {
      |    "name" : "John Jones",
      |    "officer_role" : "cic-manager"
      |  }, {
      |    "name" : "Bob Smythe",
      |    "officer_role" : "corporate-director"
      |  } ]
      |}""".stripMargin
  )

  "OfficerList" should {
    "read from Json" in {
      Json.fromJson[OfficerList](tstOfficerListJson).get shouldBe tstOfficerList
    }
  }
}
