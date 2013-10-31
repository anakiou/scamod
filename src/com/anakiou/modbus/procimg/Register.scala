package com.anakiou.modbus.procimg

trait Register extends InputRegister {

  def setValue(v: Int): Unit

  def setValue(s: Short): Unit

  def setValue(bytes: Array[Byte]): Unit
}
