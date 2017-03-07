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

package controllers.test

import builders.AuthBuilder
import connectors.test.TestCoHoAPIConnect
import services.CoHoAPISrv
import testHelpers.PAYERegSpec
import play.api.test.Helpers.{OK, BAD_REQUEST}
import org.mockito.Mockito.when
import org.mockito.Matchers
import play.api.test.FakeRequest
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpResponse}

import scala.concurrent.Future

class TestCoHoControllerSpec extends PAYERegSpec {

  val mockCoHoAPIService = mock[CoHoAPISrv]
  val mockTestAPIConnector = mock[TestCoHoAPIConnect]

  val testHttpResponse = new HttpResponse {
    override def status = OK
  }

  class Setup {
    val controller = new TestCoHoCtrl {
      override val testCoHoAPIConnector = mockTestAPIConnector
      override val coHoAPIService = mockCoHoAPIService
      override val messagesApi = mockMessages
      override val authConnector = mockAuthConnector
    }
  }

  "coHoCompanyDetailsSetup" should {
    "return an OK" when {
      "the company details page has been rendered" in new Setup {
        AuthBuilder.showWithAuthorisedUser(controller.coHoCompanyDetailsSetup, mockAuthConnector) { result =>
          status(result) shouldBe OK
        }
      }
    }
  }

  "submitCoHoCompanyDetailsSetup" should {
    "return an OK" when {
      "the registration ID has been fetched and the test company details submitted" in new Setup {
        val request = FakeRequest().withFormUrlEncodedBody(
          "companyName" -> "testCompanyName",
          "sicCodes[0]" -> "1234567890",
          "descriptions[0]" -> "testDescription-0",
          "sicCodes[1]" -> "9283745",
          "descriptions[1]" -> "testDescription-1",
          "sicCodes[2]" -> "",
          "descriptions[2]" -> "",
          "sicCodes[3]" -> "",
          "descriptions[3]" -> "",
          "sicCodes[4]" -> "",
          "descriptions[4]" -> ""
        )

        when(mockCoHoAPIService.fetchRegistrationID(Matchers.any[HeaderCarrier]()))
          .thenReturn(Future.successful("testRegId"))

        when(mockTestAPIConnector.addCoHoCompanyDetails(Matchers.any())(Matchers.any[HeaderCarrier]()))
          .thenReturn(Future.successful(testHttpResponse))

        AuthBuilder.submitWithAuthorisedUser(controller.submitCoHoCompanyDetailsSetup, mockAuthConnector, request) {
          result =>
            status(result) shouldBe OK
        }
      }
    }

    "return a BAD_REQUEST" when {
      "the form values are invalid" in new Setup {
        val request = FakeRequest().withFormUrlEncodedBody("invalidKey" -> "invalidValue")

        AuthBuilder.submitWithAuthorisedUser(controller.submitCoHoCompanyDetailsSetup, mockAuthConnector, request) {
          result =>
            status(result) shouldBe BAD_REQUEST
        }
      }
    }
  }

  "coHoCompanyDetailsTearDown" should {
    "return an OK" when {
      "the company details have been torn down" in new Setup {
        when(mockTestAPIConnector.tearDownCoHoCompanyDetails()(Matchers.any[HeaderCarrier]()))
          .thenReturn(Future.successful(testHttpResponse))

        AuthBuilder.showWithAuthorisedUser(controller.coHoCompanyDetailsTearDown, mockAuthConnector) { result =>
          status(result) shouldBe OK
        }
      }
    }
  }
}
