package com.anakiou.modbus.msg

import java.io.DataInput
import java.io.DataOutput

import com.anakiou.modbus.Modbus
import com.anakiou.modbus.ModbusCoupler
import com.anakiou.modbus.io.NonWordDataHandler
import com.anakiou.modbus.procimg.IllegalAddressException
import com.anakiou.modbus.procimg.Register

class WriteMultipleRegistersRequest extends ModbusRequest() {

  private var m_Reference: Int = _

  private var m_Registers: Array[Register] = _

  private var m_NonWordDataHandler: NonWordDataHandler = null

  setFunctionCode(Modbus.WRITE_MULTIPLE_REGISTERS)

  def this(ref: Int, registers: Array[Register]) {
    this()
    setFunctionCode(Modbus.WRITE_MULTIPLE_REGISTERS)
    setReference(ref)
    setRegisters(registers)
  }

  def createResponse(): ModbusResponse = {
    var response: WriteMultipleRegistersResponse = null
    if (m_NonWordDataHandler == null) {
      var regs: Array[Register] = null
      val procimg = ModbusCoupler.getReference.getProcessImage
      try {
        regs = procimg.getRegisterRange(this.getReference, this.getWordCount)
        for (i <- 0 until regs.length) {
          regs(i).setValue(this.getRegister(i).toBytes())
        }
      } catch {
        case iaex: IllegalAddressException => return createExceptionResponse(Modbus.ILLEGAL_ADDRESS_EXCEPTION)
      }
      response = new WriteMultipleRegistersResponse(this.getReference, regs.length)
    } else {
      val result = m_NonWordDataHandler.commitUpdate()
      if (result > 0) {
        return createExceptionResponse(result)
      }
      response = new WriteMultipleRegistersResponse(this.getReference, m_NonWordDataHandler.getWordCount)
    }
    if (!isHeadless) {
      response.setTransactionID(this.getTransactionID)
      response.setProtocolID(this.getProtocolID)
    } else {
      response.setHeadless()
    }
    response.setUnitID(this.getUnitID)
    response.setFunctionCode(this.getFunctionCode)
    response
  }

  def setReference(ref: Int) {
    m_Reference = ref
  }

  def getReference(): Int = m_Reference

  def setRegisters(registers: Array[Register]) {
    m_Registers = registers
    setDataLength(5 + getByteCount)
  }

  def getRegisters(): Array[Register] = m_Registers

  def getRegister(index: Int): Register = {
    if (index >= getWordCount) {
      throw new IndexOutOfBoundsException()
    } else {
      m_Registers(index)
    }
  }

  def getRegisterValue(index: Int): Int = m_Registers(index).toUnsignedShort()

  def getByteCount(): Int = getWordCount * 2

  def getWordCount(): Int = m_Registers.length

  def setNonWordDataHandler(dhandler: NonWordDataHandler) {
    m_NonWordDataHandler = dhandler
    setDataLength(5 + (m_NonWordDataHandler.getWordCount * 2))
  }

  def getNonWordDataHandler(): NonWordDataHandler = m_NonWordDataHandler

  def writeData(dout: DataOutput) {
    dout.writeShort(m_Reference)
    dout.writeShort(getWordCount)
    dout.writeByte(getByteCount)
    if (m_NonWordDataHandler == null) {
      for (n <- 0 until m_Registers.length) {
        dout.write(m_Registers(n).toBytes())
      }
    } else {
      m_NonWordDataHandler.prepareData(getReference, getWordCount)
      dout.write(m_NonWordDataHandler.getData)
    }
  }

  def readData(din: DataInput) {
    m_Reference = din.readShort()
    val wc = din.readUnsignedShort()
    val bc = din.readUnsignedByte()
    if (m_NonWordDataHandler == null) {
      m_Registers = Array.ofDim[Register](wc)
      val pimf = ModbusCoupler.getReference.getProcessImageFactory
      for (i <- 0 until wc) {
        m_Registers(i) = pimf.createRegister(din.readByte(), din.readByte())
      }
    } else {
      m_NonWordDataHandler.readData(din, m_Reference, wc)
    }
  }
}
