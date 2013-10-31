package com.anakiou.modbus.msg

import java.io.DataInput
import java.io.DataOutput

import com.anakiou.modbus.Modbus
import com.anakiou.modbus.ModbusCoupler
import com.anakiou.modbus.procimg.Register

class ReadMultipleRegistersResponse extends ModbusResponse() {

  private var m_ByteCount: Int = _

  private var m_Registers: Array[Register] = _

  setFunctionCode(Modbus.READ_MULTIPLE_REGISTERS)

  def this(registers: Array[Register]) {
    this()
    m_Registers = registers
    m_ByteCount = registers.length * 2
    setFunctionCode(Modbus.READ_MULTIPLE_REGISTERS)
    setDataLength(m_ByteCount + 1)
  }

  def getByteCount(): Int = m_ByteCount

  def getWordCount(): Int = m_ByteCount / 2

  private def setByteCount(count: Int) {
    m_ByteCount = count
  }

  def getRegisterValue(index: Int): Int = m_Registers(index).toUnsignedShort()

  def getRegister(index: Int): Register = {
    if (index >= getWordCount) {
      throw new IndexOutOfBoundsException()
    } else {
      m_Registers(index)
    }
  }

  def getRegisters(): Array[Register] = m_Registers

  def writeData(dout: DataOutput) {
    dout.writeByte(m_ByteCount)
    for (k <- 0 until getWordCount) {
      dout.write(m_Registers(k).toBytes())
    }
  }

  def readData(din: DataInput) {
    setByteCount(din.readUnsignedByte())
    m_Registers = Array.ofDim[Register](getWordCount)
    val pimf = ModbusCoupler.getReference.getProcessImageFactory
    for (k <- 0 until getWordCount) {
      m_Registers(k) = pimf.createRegister(din.readByte(), din.readByte())
    }
    setDataLength(getByteCount + 1)
  }
}
