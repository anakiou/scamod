package com.anakiou.modbus.msg

import java.io.DataInput
import java.io.DataOutput
import java.io.IOException
import com.anakiou.modbus.Modbus

class ExceptionResponse extends ModbusResponse {

  private var m_ExceptionCode: Int = -1

  setDataLength(1)

  def this(fc: Int) {
    this()
    setDataLength(1)
    setFunctionCode(fc + Modbus.EXCEPTION_OFFSET)
  }

  def this(fc: Int, exc: Int) {
    this()
    setDataLength(1)
    setFunctionCode(fc + Modbus.EXCEPTION_OFFSET)
    m_ExceptionCode = exc
  }

  def getExceptionCode(): Int = m_ExceptionCode

  def writeData(dout: DataOutput) {
    dout.writeByte(getExceptionCode)
  }

  def readData(din: DataInput) {
    m_ExceptionCode = din.readUnsignedByte()
  }
}
