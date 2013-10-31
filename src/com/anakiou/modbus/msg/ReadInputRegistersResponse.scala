package com.anakiou.modbus.msg

import java.io.DataInput
import java.io.DataOutput

import com.anakiou.modbus.Modbus
import com.anakiou.modbus.ModbusCoupler
import com.anakiou.modbus.procimg.InputRegister

class ReadInputRegistersResponse extends ModbusResponse() {

  private var m_ByteCount: Int = _

  private var m_Registers: Array[InputRegister] = _

  setFunctionCode(Modbus.READ_INPUT_REGISTERS)

  def this(registers: Array[InputRegister]) {
    this()
    setFunctionCode(Modbus.READ_INPUT_REGISTERS)
    m_ByteCount = registers.length * 2
    m_Registers = registers
    setDataLength(m_ByteCount + 1)
  }

  def getByteCount(): Int = m_ByteCount

  def getWordCount(): Int = m_ByteCount / 2

  private def setByteCount(count: Int) {
    m_ByteCount = count
  }

  def getRegister(index: Int): InputRegister = {
    if (index >= getWordCount) {
      throw new IndexOutOfBoundsException()
    } else {
      m_Registers(index)
    }
  }

  def getRegisterValue(index: Int): Int = {
    if (index >= getWordCount) {
      throw new IndexOutOfBoundsException()
    } else {
      m_Registers(index).toUnsignedShort()
    }
  }

  def getRegisters(): Array[InputRegister] = m_Registers

  def writeData(dout: DataOutput) {
    dout.writeByte(m_ByteCount)
    for (k <- 0 until getWordCount) {
      dout.write(m_Registers(k).toBytes())
    }
  }

  def readData(din: DataInput) {
    setByteCount(din.readUnsignedByte())
    val registers = Array.ofDim[InputRegister](getWordCount)
    val pimf = ModbusCoupler.getReference.getProcessImageFactory
    for (k <- 0 until getWordCount) {
      registers(k) = pimf.createInputRegister(din.readByte(), din.readByte())
    }
    m_Registers = registers
    setDataLength(getByteCount + 1)
  }
}
