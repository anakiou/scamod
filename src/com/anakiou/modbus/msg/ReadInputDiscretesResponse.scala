package com.anakiou.modbus.msg

import java.io.DataInput
import java.io.DataOutput

import com.anakiou.modbus.Modbus
import com.anakiou.modbus.util.BitVector

class ReadInputDiscretesResponse extends ModbusResponse() {

  private var m_BitCount: Int = _

  private var m_Discretes: BitVector = _

  setFunctionCode(Modbus.READ_INPUT_DISCRETES)

  def this(count: Int) {
    this()
    setBitCount(count)
  }

  def getBitCount(): Int = m_BitCount

  def setBitCount(count: Int) {
    m_BitCount = count
    m_Discretes = new BitVector(count)
    setDataLength(m_Discretes.byteSize() + 1)
  }

  def getDiscretes(): BitVector = m_Discretes

  def getDiscreteStatus(index: Int): Boolean = m_Discretes.getBit(index)

  def setDiscreteStatus(index: Int, b: Boolean) {
    m_Discretes.setBit(index, b)
  }

  def writeData(dout: DataOutput) {
    dout.writeByte(m_Discretes.byteSize())
    dout.write(m_Discretes.getBytes, 0, m_Discretes.byteSize())
  }

  def readData(din: DataInput) {
    val count = din.readUnsignedByte()
    val data = Array.ofDim[Byte](count)
    for (k <- 0 until count) {
      data(k) = din.readByte()
    }
    m_Discretes = BitVector.createBitVector(data)
    setDataLength(count + 1)
  }
}
