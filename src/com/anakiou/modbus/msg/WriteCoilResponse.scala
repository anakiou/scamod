package com.anakiou.modbus.msg

import java.io.DataInput
import java.io.DataOutput

import com.anakiou.modbus.Modbus

class WriteCoilResponse extends ModbusResponse() {

  private var m_Coil: Boolean = false

  private var m_Reference: Int = _

  setFunctionCode(Modbus.WRITE_COIL)

  setDataLength(4)

  def this(reference: Int, b: Boolean) {
    this()
    setFunctionCode(Modbus.WRITE_COIL)
    setDataLength(4)
    setReference(reference)
    setCoil(b)
  }

  private def setCoil(b: Boolean) {
    m_Coil = b
  }

  def getCoil(): Boolean = m_Coil

  def getReference(): Int = m_Reference

  private def setReference(ref: Int) {
    m_Reference = ref
  }

  def writeData(dout: DataOutput) {
    dout.writeShort(getReference)
    if (getCoil) {
      dout.write(Modbus.COIL_ON_BYTES, 0, 2)
    } else {
      dout.write(Modbus.COIL_OFF_BYTES, 0, 2)
    }
  }

  def readData(din: DataInput) {
    setReference(din.readUnsignedShort())
    val data = Array.ofDim[Byte](2)
    for (k <- 0 until 2) {
      data(k) = din.readByte()
    }
    if (data(0) == Modbus.COIL_ON) {
      setCoil(true)
    } else {
      setCoil(false)
    }
    setDataLength(4)
  }
}
