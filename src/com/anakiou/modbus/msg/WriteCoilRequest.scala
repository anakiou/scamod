package com.anakiou.modbus.msg

import java.io.DataInput
import java.io.DataOutput

import com.anakiou.modbus.Modbus
import com.anakiou.modbus.ModbusCoupler
import com.anakiou.modbus.procimg.DigitalOut
import com.anakiou.modbus.procimg.IllegalAddressException

class WriteCoilRequest extends ModbusRequest() {

  private var m_Reference: Int = _

  private var m_Coil: Boolean = _

  setFunctionCode(Modbus.WRITE_COIL)

  setDataLength(4)

  def this(ref: Int, b: Boolean) {
    this()
    setFunctionCode(Modbus.WRITE_COIL)
    setDataLength(4)
    setReference(ref)
    setCoil(b)
  }

  def createResponse(): ModbusResponse = {
    var response: WriteCoilResponse = null
    var dout: DigitalOut = null
    val procimg = ModbusCoupler.getReference.getProcessImage
    try {
      dout = procimg.getDigitalOut(this.getReference)
      dout.set(this.getCoil)
    } catch {
      case iaex: IllegalAddressException => return createExceptionResponse(Modbus.ILLEGAL_ADDRESS_EXCEPTION)
    }
    response = new WriteCoilResponse(this.getReference, dout.isSet)
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

  def setCoil(b: Boolean) {
    m_Coil = b
  }

  def getCoil(): Boolean = m_Coil

  def writeData(dout: DataOutput) {
    dout.writeShort(m_Reference)
    if (m_Coil) {
      dout.write(Modbus.COIL_ON_BYTES, 0, 2)
    } else {
      dout.write(Modbus.COIL_OFF_BYTES, 0, 2)
    }
  }

  def readData(din: DataInput) {
    m_Reference = din.readUnsignedShort()
    m_Coil = if (din.readByte() == Modbus.COIL_ON) true else false
    din.readByte()
  }
}
