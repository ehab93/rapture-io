/**********************************************************************************************\
* Rapture I/O Library                                                                          *
* Version 0.9.0                                                                                *
*                                                                                              *
* The primary distribution site is                                                             *
*                                                                                              *
*   http://rapture.io/                                                                         *
*                                                                                              *
* Copyright 2010-2014 Jon Pretty, Propensive Ltd.                                              *
*                                                                                              *
* Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file    *
* except in compliance with the License. You may obtain a copy of the License at               *
*                                                                                              *
*   http://www.apache.org/licenses/LICENSE-2.0                                                 *
*                                                                                              *
* Unless required by applicable law or agreed to in writing, software distributed under the    *
* License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,    *
* either express or implied. See the License for the specific language governing permissions   *
* and limitations under the License.                                                           *
\**********************************************************************************************/
package rapture.io
import rapture.core._

object Finance {

  trait Usd
  trait Gbp
  trait Eur
  trait Chf
  trait Cad
  trait Cny
  trait Dkk
  trait Inr
  trait Jpy
  trait Nok
  trait Nzd
  trait Rub

  class Currency[T](val code: String, val name: String, val dp: Int, val prefix: String)
  implicit val GbpCurrency = new Currency[Gbp]("GBP", "Pounds Sterling", 2, "£")
  implicit val UsdCurrency = new Currency[Usd]("USD", "US Dollars", 2, "$")
  implicit val ChfCurrency = new Currency[Chf]("CHF", "Swiss Francs", 2, "Fr")
  implicit val EurCurrency = new Currency[Eur]("EUR", "Euros", 2, "€")
  implicit val CadCurrency = new Currency[Cad]("CAD", "Canadian Dollars", 2, "$")
  implicit val CnyCurrency = new Currency[Cny]("CNY", "Chinese Yuan", 2, "¥")
  implicit val DkkCurrency = new Currency[Dkk]("DKK", "Danish Krone", 2, "kr")
  implicit val InrCurrency = new Currency[Inr]("INR", "Indian Rupees", 2, "Rs")
  implicit val JpyCurrency = new Currency[Jpy]("JPY", "Japanese Yen", 2, "¥")
  implicit val NokCurrency = new Currency[Nok]("NOK", "Norwegian Krone", 2, "kr")
  implicit val NzdCurrency = new Currency[Nzd]("NZD", "New Zealand Dollars", 2, "$")
  implicit val RubCurrency = new Currency[Rub]("RUB", "Russian Rubles", 2, "р")

  trait MoneyFactory[T] { def apply(d: Double)(implicit currency: Currency[T]) = new Money[T](d) }

  object Usd extends MoneyFactory[Usd]
  object Gbp extends MoneyFactory[Gbp]
  object Eur extends MoneyFactory[Eur]
  object Chf extends MoneyFactory[Chf]
  object Cad extends MoneyFactory[Cad]
  object Cny extends MoneyFactory[Cny]
  object Dkk extends MoneyFactory[Dkk]
  object Inr extends MoneyFactory[Inr]
  object Jpy extends MoneyFactory[Jpy]
  object Nok extends MoneyFactory[Nok]
  object Nzd extends MoneyFactory[Nzd]
  object Rub extends MoneyFactory[Rub]

  case class Money[T: Currency](major: Int, minor: Int) {

    val div = math.pow(10, ?[Currency[T]].dp).toInt
    val half = 0.5/div

    def this(amount: Double) =
      this((amount*math.pow(10, ?[Currency[T]].dp).toInt + 0.5).toInt/math.pow(10,
          ?[Currency[T]].dp).toInt, (amount*math.pow(10,
          ?[Currency[T]].dp).toInt + 0.5).toInt%math.pow(10,
          ?[Currency[T]].dp).toInt)

    def pad(x: Int) = ("0"*(?[Currency[T]].dp - x.toString.length))+x

    override def toString = ?[Currency[T]].prefix+amountString
    def amountString =
      if(major < 0) "-"+(-major - 1)+"."+pad(div - minor) else major+"."+pad(minor)

    def +(m: Money[T]): Money[T] =
      Money[T](major + m.major + (minor + m.minor)/div, (minor + m.minor)%div)
    
    def unary_- : Money[T] = Money[T](-major - 1, div - minor)
    def -(m: Money[T]): Money[T] = this + -m

    def *(n: Int): Money[T] = this * n.toDouble
    def *(n: Double): Money[T] = {
      val x = ((major*div + minor)*n + 0.5).toInt
      Money[T](x/div, x%div)
    }
  
    def /(n: Int): Money[T] = this * (1.0/n)
    def /(n: Double): Money[T] = this * (1.0/n)
  }
}
