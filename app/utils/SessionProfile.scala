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

package utils

import connectors.KeystoreConnect
import enums.CacheKeys
import models.external.CurrentProfile
import play.api.mvc.Result
import play.api.mvc.Results.Redirect
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait SessionProfile {

  val keystoreConnector: KeystoreConnect

  def withCurrentProfile(f: => CurrentProfile => Future[Result])(implicit hc: HeaderCarrier): Future[Result] = {
    keystoreConnector.fetchAndGet[CurrentProfile](CacheKeys.CurrentProfile.toString) flatMap {
      case Some(currentProfile) => f(currentProfile)
      case None => Future.successful(Redirect(controllers.userJourney.routes.PayeStartController.startPaye()))
    }
  }

}
