package com.anakiou.modbus.msg

import java.io.DataInput
import java.io.DataOutput

import com.anakiou.modbus.Modbus
import com.anakiou.modbus.ModbusCoupler
import com.anakiou.modbus.procimg.IllegalAddressException
import com.anakiou.modbus.procimg.InputRegister

class ReadInputRegistersRequest extends ModbusRequest() {

  private var m_Reference: Int = _

  private var m_WordCount: Int = _

  setFunctionCode(Modbus.READ_INPUT_REGISTERS)

  setDataLength(4)

  def this(ref: Int, count: Int) {
    this()
    setFunctionCode(Modbus.READ_INPUT_REGISTERS)
    setDataLength(4)
    setReference(ref)
    setWordCount(count)
  }

  def createResponse(): ModbusResponse = {
    var response: ReadInputRegistersResponse = null
    var inpregs: Array[InputRegister] = null
    val procimg = ModbusCoupler.getReference.getProcessImage
    try {
      inpregs = procimg.getInputRegisterRange(this.getReference, this.getWordCount)
    } catch {
      case iaex: IllegalAddressException => return createExceptionResponse(Modbus.ILLEGAL_ADDRESS_EXCEPTION)
    }
    response = new ReadInputRegistersResponse(inpregs)
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

  def setWordCount(count: Int) {
    m_WordCount = count
  }

  def getWordCount(): Int = m_WordCount

  def writeData(dout: DataOutput) {
    dout.writeShort(m_Reference)
    dout.writeShort(m_WordCount)
  }

  def readData(din: DataInput) {
    m_Reference = din.readUnsignedShort()
    m_WordCount = din.readUnsignedShort()
  }
}
