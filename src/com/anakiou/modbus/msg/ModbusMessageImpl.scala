package com.anakiou.modbus.msg

import java.io.DataInput
import java.io.DataOutput

import com.anakiou.modbus.Modbus
import com.anakiou.modbus.util.ModbusUtil

abstract class ModbusMessageImpl extends ModbusMessage {

  private var m_TransactionID: Int = Modbus.DEFAULT_TRANSACTION_ID

  private var m_ProtocolID: Int = Modbus.DEFAULT_PROTOCOL_ID

  private var m_DataLength: Int = _

  private var m_UnitID: Int = Modbus.DEFAULT_UNIT_ID

  private var m_FunctionCode: Int = _

  private var m_Headless: Boolean = false

  def isHeadless(): Boolean = m_Headless

  def setHeadless() {
    m_Headless = true
  }

  def setHeadless(b: Boolean) {
    m_Headless = b
  }

  def getTransactionID(): Int = m_TransactionID

  def setTransactionID(tid: Int) {
    m_TransactionID = tid
  }

  def getProtocolID(): Int = m_ProtocolID

  def setProtocolID(pid: Int) {
    m_ProtocolID = pid
  }

  def getDataLength(): Int = m_DataLength

  def setDataLength(length: Int) {
    m_DataLength = length + 2
  }

  def getUnitID(): Int = m_UnitID

  def setUnitID(num: Int) {
    m_UnitID = num
  }

  def getFunctionCode(): Int = m_FunctionCode

  def setFunctionCode(code: Int) {
    m_FunctionCode = code
  }

  def writeTo(dout: DataOutput) {
    if (!isHeadless) {
      dout.writeShort(getTransactionID)
      dout.writeShort(getProtocolID)
      dout.writeShort(getDataLength)
    }
    dout.writeByte(getUnitID)
    dout.writeByte(getFunctionCode)
    writeData(dout)
  }

  def writeData(dout: DataOutput): Unit

  def readFrom(din: DataInput) {
    if (!isHeadless) {
      setTransactionID(din.readUnsignedShort())
      setProtocolID(din.readUnsignedShort())
      m_DataLength = din.readUnsignedShort()
    }
    setUnitID(din.readUnsignedByte())
    setFunctionCode(din.readUnsignedByte())
    readData(din)
  }

  def readData(din: DataInput): Unit

  def getOutputLength(): Int = {
    var l = 2 + getDataLength
    if (!isHeadless) {
      l = l + 6
    }
    l
  }

  def getHexMessage(): String = ModbusUtil.toHex(this)
}
