package com.anakiou.modbus.msg

import java.io.DataInput
import java.io.DataOutput

class WriteSingleRegisterResponse extends ModbusResponse() {

  private var m_Reference: Int = _

  private var m_RegisterValue: Int = _

  setDataLength(4)

  def this(reference: Int, value: Int) {
    this()
    setReference(reference)
    setRegisterValue(value)
    setDataLength(4)
  }

  def getRegisterValue(): Int = m_RegisterValue

  private def setRegisterValue(value: Int) {
    m_RegisterValue = value
  }

  def getReference(): Int = m_Reference

  private def setReference(ref: Int) {
    m_Reference = ref
  }

  def writeData(dout: DataOutput) {
    dout.writeShort(getReference)
    dout.writeShort(getRegisterValue)
  }

  def readData(din: DataInput) {
    setReference(din.readUnsignedShort())
    setRegisterValue(din.readUnsignedShort())
    setDataLength(4)
  }
}
