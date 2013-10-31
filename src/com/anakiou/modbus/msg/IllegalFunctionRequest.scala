package com.anakiou.modbus.msg

import java.io.DataInput
import java.io.DataOutput
import com.anakiou.modbus.Modbus

class IllegalFunctionRequest(fc: Int) extends ModbusRequest {

  setFunctionCode(fc)

  def createResponse(): ModbusResponse = {
    this.createExceptionResponse(Modbus.ILLEGAL_FUNCTION_EXCEPTION)
  }

  def writeData(dout: DataOutput) {
    throw new RuntimeException()
  }

  def readData(din: DataInput) {
    val length = getDataLength
    for (i <- 0 until length) {
      din.readByte()
    }
  }
}
