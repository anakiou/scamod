package com.anakiou.modbus.procimg

trait DigitalOut {

  def isSet(): Boolean

  def set(b: Boolean): Unit
}
