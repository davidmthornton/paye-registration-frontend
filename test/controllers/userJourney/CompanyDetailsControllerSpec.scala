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

package controllers.userJourney

import builders.AuthBuilder
import enums.DownstreamOutcome
import fixtures.{PAYERegistrationFixture, S4LFixture}
import models.view.{Address, CompanyDetails => CompanyDetailsView, TradingName => TradingNameView}
import org.jsoup._
import org.mockito.Matchers
import org.mockito.Mockito._
import play.api.http.Status
import play.api.i18n.MessagesApi
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{CoHoAPIService, CompanyDetailsService, S4LService}
import testHelpers.PAYERegSpec
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.Future

class CompanyDetailsControllerSpec extends PAYERegSpec with S4LFixture with PAYERegistrationFixture {
  val mockS4LService = mock[S4LService]
  val mockCompanyDetailsService = mock[CompanyDetailsService]
  val mockCoHoService = mock[CoHoAPIService]

  class Setup {
    val controller = new CompanyDetailsCtrl {
      override val s4LService = mockS4LService
      override val keystoreConnector = mockKeystoreConnector
      override val authConnector = mockAuthConnector
      override val companyDetailsService = mockCompanyDetailsService
      override val cohoService = mockCoHoService
      implicit val messagesApi: MessagesApi = fakeApplication.injector.instanceOf[MessagesApi]
    }
  }

  val tstTradingNameModel = TradingNameView(differentName = true, tradingName = Some("test trading name"))

  val fakeRequest = FakeRequest("GET", "/")
  implicit val materializer = fakeApplication.materializer

  "calling the tradingName action" should {

    "return 303 for an unauthorised user" in new Setup {
      val result = controller.tradingName()(FakeRequest())
      status(result) shouldBe Status.SEE_OTHER
    }

    "show the correctly pre-populated trading name page when data has already been entered" in new Setup {
      val cName = "Tst Company Name"
      when(mockCompanyDetailsService.getCompanyDetails(Matchers.any())).thenReturn(Future.successful(validCompanyDetailsViewModel))

      AuthBuilder.showWithAuthorisedUser(controller.tradingName, mockAuthConnector) {
        (response: Future[Result]) =>
          status(response) shouldBe Status.OK
          val result = Jsoup.parse(bodyOf(response))
          result.body().getElementById("pageHeading").text() shouldBe s"Does the company trade under any other names than $cName?"
          result.body.getElementById("differentName-true").parent.classNames().contains("selected") shouldBe true
          result.body.getElementById("differentName-false").parent.classNames().contains("selected") shouldBe false
          result.body().getElementById("tradingName").attr("value") shouldBe validCompanyDetailsViewModel.tradingName.get.tradingName.get
      }
    }

    "show the correctly pre-populated trading name page when negative data has already been entered" in new Setup {
      val cName = "Tst Company Name"
      val negativeTradingNameCompanyDetails = validCompanyDetailsViewModel.copy(tradingName = Some(negativeTradingNameViewModel))
      when(mockCompanyDetailsService.getCompanyDetails(Matchers.any())).thenReturn(Future.successful(negativeTradingNameCompanyDetails))

      AuthBuilder.showWithAuthorisedUser(controller.tradingName, mockAuthConnector) {
        (response: Future[Result]) =>
          status(response) shouldBe Status.OK
          val result = Jsoup.parse(bodyOf(response))
          result.body().getElementById("pageHeading").text() shouldBe s"Does the company trade under any other names than $cName?"
          result.body.getElementById("differentName-true").parent.classNames().contains("selected") shouldBe false
          result.body.getElementById("differentName-false").parent.classNames().contains("selected") shouldBe true
          result.body().getElementById("tradingName").attr("value") shouldBe ""
      }
    }

    "show a blank trading name page when no Trading Name data has been entered" in new Setup {
      val cName = "Tst Company Name"
      val noTradingNameCompanyDetails = validCompanyDetailsViewModel.copy(tradingName = None)
      when(mockCompanyDetailsService.getCompanyDetails(Matchers.any())).thenReturn(Future.successful(noTradingNameCompanyDetails))

      AuthBuilder.showWithAuthorisedUser(controller.tradingName, mockAuthConnector) {
        (response: Future[Result]) =>
          status(response) shouldBe Status.OK
          val result = Jsoup.parse(bodyOf(response))
          result.body().getElementById("pageHeading").text() shouldBe s"Does the company trade under any other names than $cName?"
          result.body.getElementById("differentName-true").parent.classNames().contains("selected") shouldBe false
          result.body.getElementById("differentName-false").parent.classNames().contains("selected") shouldBe false
          result.body().getElementById("tradingName").attr("value") shouldBe ""
      }
    }

    "show a blank trading name page when no Company Details data has been entered" in new Setup {
      val cName = "Tst Company Name"
      val defaultCompanyDetailsView = CompanyDetailsView(None, cName, None, validROAddress)
      when(mockCompanyDetailsService.getCompanyDetails(Matchers.any())).thenReturn(Future.successful(defaultCompanyDetailsView))

      AuthBuilder.showWithAuthorisedUser(controller.tradingName, mockAuthConnector) {
        response =>
          status(response) shouldBe Status.OK
          val result = Jsoup.parse(bodyOf(response))
          result.body().getElementById("pageHeading").text() shouldBe s"Does the company trade under any other names than $cName?"
          result.body.getElementById("differentName-true").parent.classNames().contains("selected") shouldBe false
          result.body.getElementById("differentName-false").parent.classNames().contains("selected") shouldBe false
          result.body().getElementById("tradingName").attr("value") shouldBe ""
      }
    }
  }

  "calling the submitTradingName action" should {

    "return 303 for an unauthorised user" in new Setup {
      val result = controller.submitTradingName()(FakeRequest())
      status(result) shouldBe Status.SEE_OTHER
    }
    "redirect to the confirm ro address page when a user enters valid data" in new Setup {
      when(mockCompanyDetailsService.submitTradingName(Matchers.any())(Matchers.any())).thenReturn(Future.successful(DownstreamOutcome.Success))
      AuthBuilder.submitWithAuthorisedUser(controller.submitTradingName(), mockAuthConnector, fakeRequest.withFormUrlEncodedBody(
        "differentName" -> "true",
        "tradingName" -> "Tradez R us"
      )) {
        result =>
          status(result) shouldBe Status.SEE_OTHER
          result.header.headers("Location") shouldBe "/register-for-paye/registered-office-address"
      }
    }

    "show an error page when there is an error response from the microservice" in new Setup {
      when(mockCompanyDetailsService.submitTradingName(Matchers.any())(Matchers.any())).thenReturn(Future.successful(DownstreamOutcome.Failure))
      AuthBuilder.submitWithAuthorisedUser(controller.submitTradingName(), mockAuthConnector, fakeRequest.withFormUrlEncodedBody(
        "differentName" -> "true",
        "tradingName" -> "Tradez R us"
      )) {
        result =>
          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      }
    }

    "return 400 when a user enters no data" in new Setup {
      when(mockCompanyDetailsService.submitTradingName(Matchers.any())(Matchers.any())).thenReturn(Future.successful(DownstreamOutcome.Success))
      when(mockCoHoService.getStoredCompanyName()(Matchers.any())).thenReturn(Future.successful("tst Name"))
      AuthBuilder.submitWithAuthorisedUser(controller.submitTradingName(), mockAuthConnector, fakeRequest.withFormUrlEncodedBody(
      )) {
        result =>
          status(result) shouldBe Status.BAD_REQUEST
      }
    }

    "return 400 when a user enters invalid data" in new Setup {
      when(mockCoHoService.getStoredCompanyName()(Matchers.any())).thenReturn(Future.successful("tst Name"))
      AuthBuilder.submitWithAuthorisedUser(controller.submitTradingName(), mockAuthConnector, fakeRequest.withFormUrlEncodedBody(
        "differentName" -> "true"
      )) {
        result =>
          status(result) shouldBe Status.BAD_REQUEST
      }
    }
  }

  "roAddress" should {
    "return an ok" when {
      "the user is authorised to view the page" in new Setup {

        val testAddress =
          Address(
            "testL1",
            "testL2",
            Some("testL3"),
            Some("testL4"),
            Some("testPostCode"),
            None
          )

        when(mockCompanyDetailsService.getCompanyDetails(Matchers.any[HeaderCarrier]()))
          .thenReturn(Future.successful(validCompanyDetailsViewModel))

        AuthBuilder.showWithAuthorisedUser(controller.roAddress, mockAuthConnector) {
          result =>
            status(result) shouldBe OK
        }
      }
    }

    "return a forbidden" when {
      "the user is not authorised to view the page" in new Setup {
        AuthBuilder.showWithAuthorisedUser(controller.confirmRO, mockAuthConnector) {
          result =>
            status(result) shouldBe SEE_OTHER
            redirectLocation(result) shouldBe Some(s"${controllers.userJourney.routes.AddressLookupController.redirectToLookup()}")
        }
      }
    }
  }
}
