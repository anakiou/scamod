package com.anakiou.modbus.msg

import java.io.DataInput
import java.io.DataOutput

import com.anakiou.modbus.Modbus

class WriteMultipleCoilsResponse extends ModbusResponse() {

  private var m_Reference: Int = _

  private var m_BitCount: Int = _

  setFunctionCode(Modbus.WRITE_MULTIPLE_COILS)

  setDataLength(4)

  def this(ref: Int, count: Int) {
    this()
    setFunctionCode(Modbus.WRITE_MULTIPLE_COILS)
    setDataLength(4)
    m_Reference = ref
    m_BitCount = count
  }

  def getReference(): Int = m_Reference

  def getBitCount(): Int = m_BitCount

  def setBitCount(count: Int) {
    m_BitCount = count
  }

  def writeData(dout: DataOutput) {
    dout.writeShort(m_Reference)
    dout.writeShort(m_BitCount)
  }

  def readData(din: DataInput) {
    m_Reference = din.readUnsignedShort()
    m_BitCount = din.readUnsignedShort()
  }
}
