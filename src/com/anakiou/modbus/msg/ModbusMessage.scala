package com.anakiou.modbus.msg

import com.anakiou.modbus.io.Transportable

trait ModbusMessage extends Transportable {

  def setHeadless(): Unit

  def getTransactionID(): Int

  def getProtocolID(): Int

  def getDataLength(): Int

  def getUnitID(): Int

  def getFunctionCode(): Int

  def getHexMessage(): String
}
