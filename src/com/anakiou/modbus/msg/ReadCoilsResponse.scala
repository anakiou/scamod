package com.anakiou.modbus.msg

import java.io.DataInput
import java.io.DataOutput

import com.anakiou.modbus.Modbus
import com.anakiou.modbus.util.BitVector

class ReadCoilsResponse extends ModbusResponse() {

  private var m_Coils: BitVector = _

  setFunctionCode(Modbus.READ_COILS)

  def this(count: Int) {
    this()
    m_Coils = new BitVector(count)
    setFunctionCode(Modbus.READ_COILS)
    setDataLength(m_Coils.byteSize() + 1)
  }

  def getBitCount(): Int = {
    if (m_Coils == null) {
      0
    } else {
      m_Coils.size
    }
  }

  def getCoils(): BitVector = m_Coils

  def getCoilStatus(index: Int): Boolean = m_Coils.getBit(index)

  def setCoilStatus(index: Int, b: Boolean) {
    m_Coils.setBit(index, b)
  }

  def writeData(dout: DataOutput) {
    dout.writeByte(m_Coils.byteSize())
    dout.write(m_Coils.getBytes, 0, m_Coils.byteSize())
  }

  def readData(din: DataInput) {
    val count = din.readUnsignedByte()
    val data = Array.ofDim[Byte](count)
    for (k <- 0 until count) {
      data(k) = din.readByte()
    }
    m_Coils = BitVector.createBitVector(data)
    setDataLength(count + 1)
  }
}
