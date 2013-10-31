package com.anakiou.modbus.io

import com.anakiou.modbus.ModbusException
import com.anakiou.modbus.msg.ModbusRequest
import com.anakiou.modbus.msg.ModbusResponse

trait ModbusTransaction {

  def setRequest(req: ModbusRequest): Unit

  def getRequest(): ModbusRequest

  def getResponse(): ModbusResponse

  def getTransactionID(): Int

  def setRetries(retries: Int): Unit

  def getRetries(): Int

  def setCheckingValidity(b: Boolean): Unit

  def isCheckingValidity(): Boolean

  def execute(): Unit
}
