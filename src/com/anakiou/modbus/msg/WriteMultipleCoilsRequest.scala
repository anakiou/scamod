package com.anakiou.modbus.msg

import java.io.DataInput
import java.io.DataOutput

import com.anakiou.modbus.Modbus
import com.anakiou.modbus.ModbusCoupler
import com.anakiou.modbus.procimg.DigitalOut
import com.anakiou.modbus.procimg.IllegalAddressException
import com.anakiou.modbus.util.BitVector

class WriteMultipleCoilsRequest extends ModbusRequest() {

  private var m_Reference: Int = _

  private var m_Coils: BitVector = _

  setFunctionCode(Modbus.WRITE_MULTIPLE_COILS)

  setDataLength(5)

  def this(ref: Int, count: Int) {
    this()
    setFunctionCode(Modbus.WRITE_MULTIPLE_COILS)
    setReference(ref)
    setCoils(new BitVector(count))
  }

  def this(ref: Int, bv: BitVector) {
    this()
    setFunctionCode(Modbus.WRITE_MULTIPLE_COILS)
    setReference(ref)
    setCoils(bv)
  }

  def createResponse(): ModbusResponse = {
    var response: WriteMultipleCoilsResponse = null
    var douts: Array[DigitalOut] = null
    val procimg = ModbusCoupler.getReference.getProcessImage
    try {
      douts = procimg.getDigitalOutRange(m_Reference, m_Coils.size)
      for (i <- 0 until douts.length) {
        douts(i).set(m_Coils.getBit(i))
      }
    } catch {
      case iaex: IllegalAddressException => return createExceptionResponse(Modbus.ILLEGAL_ADDRESS_EXCEPTION)
    }
    response = new WriteMultipleCoilsResponse(m_Reference, m_Coils.size)
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

  def getBitCount(): Int = {
    if (m_Coils == null) {
      0
    } else {
      m_Coils.size
    }
  }

  def getByteCount(): Int = m_Coils.byteSize()

  def getCoilStatus(index: Int): Boolean = m_Coils.getBit(index)

  def setCoilStatus(index: Int, b: Boolean) {
    m_Coils.setBit(index, b)
  }

  def getCoils(): BitVector = m_Coils

  def setCoils(bv: BitVector) {
    m_Coils = bv
    setDataLength(m_Coils.byteSize() + 5)
  }

  def writeData(dout: DataOutput) {
    dout.writeShort(m_Reference)
    dout.writeShort(m_Coils.size)
    dout.writeByte(m_Coils.byteSize())
    dout.write(m_Coils.getBytes)
  }

  def readData(din: DataInput) {
    m_Reference = din.readUnsignedShort()
    val bitcount = din.readUnsignedShort()
    val count = din.readUnsignedByte()
    val data = Array.ofDim[Byte](count)
    for (k <- 0 until count) {
      data(k) = din.readByte()
    }
    m_Coils = BitVector.createBitVector(data, bitcount)
    setDataLength(count + 5)
  }
}
