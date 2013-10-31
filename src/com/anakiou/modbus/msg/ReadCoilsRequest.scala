package com.anakiou.modbus.msg

import java.io.DataInput
import java.io.DataOutput

import com.anakiou.modbus.Modbus
import com.anakiou.modbus.ModbusCoupler
import com.anakiou.modbus.procimg.DigitalOut
import com.anakiou.modbus.procimg.IllegalAddressException

class ReadCoilsRequest extends ModbusRequest() {

  private var m_Reference: Int = _

  private var m_BitCount: Int = _

  setFunctionCode(Modbus.READ_COILS)

  setDataLength(4)

  def this(ref: Int, count: Int) {
    this()
    setFunctionCode(Modbus.READ_COILS)
    setDataLength(4)
    setReference(ref)
    setBitCount(count)
  }

  def createResponse(): ModbusResponse = {
    var response: ReadCoilsResponse = null
    var douts: Array[DigitalOut] = null
    val procimg = ModbusCoupler.getReference.getProcessImage
    try {
      douts = procimg.getDigitalOutRange(this.getReference, this.getBitCount)
    } catch {
      case iaex: IllegalAddressException => return createExceptionResponse(Modbus.ILLEGAL_ADDRESS_EXCEPTION)
    }
    response = new ReadCoilsResponse(douts.length)
    if (!isHeadless) {
      response.setTransactionID(this.getTransactionID)
      response.setProtocolID(this.getProtocolID)
    } else {
      response.setHeadless()
    }
    response.setUnitID(this.getUnitID)
    response.setFunctionCode(this.getFunctionCode)
    for (i <- 0 until douts.length) {
      response.setCoilStatus(i, douts(i).isSet)
    }
    response
  }

  def setReference(ref: Int) {
    m_Reference = ref
  }

  def getReference(): Int = m_Reference

  def setBitCount(count: Int) {
    if (count > Modbus.MAX_BITS) {
      throw new IllegalArgumentException("Maximum bitcount exceeded.")
    } else {
      m_BitCount = count
    }
  }

  def getBitCount(): Int = m_BitCount

  def writeData(dout: DataOutput) {
    dout.writeShort(m_Reference)
    dout.writeShort(m_BitCount)
  }

  def readData(din: DataInput) {
    m_Reference = din.readUnsignedShort()
    m_BitCount = din.readUnsignedShort()
  }
}
