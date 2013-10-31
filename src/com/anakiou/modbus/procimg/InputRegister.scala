package com.anakiou.modbus.procimg

trait InputRegister {

  def getValue(): Int

  def toUnsignedShort(): Int

  def toShort(): Short

  def toBytes(): Array[Byte]
}
