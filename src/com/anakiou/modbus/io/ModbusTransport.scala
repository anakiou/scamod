package com.anakiou.modbus.io

import java.io.IOException
import scala.collection.JavaConversions._
import com.anakiou.modbus.msg.ModbusMessage
import com.anakiou.modbus.msg.ModbusResponse
import com.anakiou.modbus.msg.ModbusRequest

trait ModbusTransport {

  def close(): Unit

  def writeMessage(msg: ModbusMessage): Unit

  def readRequest(): ModbusRequest

  def readResponse(): ModbusResponse
}
