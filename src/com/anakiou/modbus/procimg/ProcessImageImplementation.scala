package com.anakiou.modbus.procimg

import ProcessImageImplementation._

object ProcessImageImplementation {

  val DIG_TRUE = 1

  val DIG_FALSE = 0

  val DIG_INVALID = -1
}

trait ProcessImageImplementation extends ProcessImage {

  def setDigitalOut(ref: Int, _do: DigitalOut): Unit

  def addDigitalOut(_do: DigitalOut): Unit

  def removeDigitalOut(_do: DigitalOut): Unit

  def setDigitalIn(ref: Int, di: DigitalIn): Unit

  def addDigitalIn(di: DigitalIn): Unit

  def removeDigitalIn(di: DigitalIn): Unit

  def setInputRegister(ref: Int, reg: InputRegister): Unit

  def addInputRegister(reg: InputRegister): Unit

  def removeInputRegister(reg: InputRegister): Unit

  def setRegister(ref: Int, reg: Register): Unit

  def addRegister(reg: Register): Unit

  def removeRegister(reg: Register): Unit
}
