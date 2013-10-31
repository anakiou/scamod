package com.anakiou.modbus.msg

import java.io.DataInput
import java.io.DataOutput

import com.anakiou.modbus.Modbus
import com.anakiou.modbus.ModbusCoupler
import com.anakiou.modbus.procimg.IllegalAddressException
import com.anakiou.modbus.procimg.Register

class WriteSingleRegisterRequest extends ModbusRequest() {

  private var m_Reference: Int = _

  private var m_Register: Register = _

  setFunctionCode(Modbus.WRITE_SINGLE_REGISTER)

  setDataLength(4)

  def this(ref: Int, reg: Register) {
    this()
    setFunctionCode(Modbus.WRITE_SINGLE_REGISTER)
    m_Reference = ref
    m_Register = reg
    setDataLength(4)
  }

  def createResponse(): ModbusResponse = {
    var response: WriteSingleRegisterResponse = null
    var reg: Register = null
    val procimg = ModbusCoupler.getReference.getProcessImage
    try {
      reg = procimg.getRegister(m_Reference)
      reg.setValue(m_Register.toBytes())
    } catch {
      case iaex: IllegalAddressException => return createExceptionResponse(Modbus.ILLEGAL_ADDRESS_EXCEPTION)
    }
    response = new WriteSingleRegisterResponse(this.getReference, reg.getValue)
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

  def setRegister(reg: Register) {
    m_Register = reg
  }

  def getRegister(): Register = m_Register

  def writeData(dout: DataOutput) {
    dout.writeShort(m_Reference)
    dout.write(m_Register.toBytes(), 0, 2)
  }

  def readData(din: DataInput) {
    m_Reference = din.readUnsignedShort()
    m_Register = ModbusCoupler.getReference.getProcessImageFactory.createRegister(din.readByte(), din.readByte())
  }
}
