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

import org.joda.time.format.ISODateTimeFormat

sealed trait FeatureSwitch {
  def name: String
  def enabled: Boolean
}

case class BooleanFeatureSwitch(name: String, enabled: Boolean) extends FeatureSwitch

object FeatureSwitch {

  private[utils] def systemPropertyName(name: String) = s"feature.$name"

  private[utils] def getProperty(name: String): FeatureSwitch = {
    val value = sys.props.get(systemPropertyName(name))

    value match {
      case Some("true") => BooleanFeatureSwitch(name, enabled = true)
      case _ => BooleanFeatureSwitch(name, enabled = false)
    }
  }

  private[utils] def setProperty(name: String, value: String): FeatureSwitch = {
    sys.props += ((systemPropertyName(name), value))
    getProperty(name)
  }

  def enable(fs: FeatureSwitch): FeatureSwitch = setProperty(fs.name, "true")
  def disable(fs: FeatureSwitch): FeatureSwitch = setProperty(fs.name, "false")

  def apply(name: String, enabled: Boolean = false): FeatureSwitch = getProperty(name)
  def unapply(fs: FeatureSwitch): Option[(String, Boolean)] = Some(fs.name -> fs.enabled)
}

object PAYEFeatureSwitches extends PAYEFeatureSwitches {
  val addressService = "addressService"
}

trait PAYEFeatureSwitches {

  val addressService: String

  def addressFrontend = FeatureSwitch.getProperty(addressService)

  def apply(name: String): Option[FeatureSwitch] = name match {
    case "addressService" => Some(addressFrontend)
    case _ => None
  }

}