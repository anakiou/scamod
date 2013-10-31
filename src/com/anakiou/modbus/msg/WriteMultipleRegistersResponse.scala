package com.anakiou.modbus.msg

import java.io.DataInput
import java.io.DataOutput

class WriteMultipleRegistersResponse extends ModbusResponse() {

  private var m_WordCount: Int = _

  private var m_Reference: Int = _

  def this(reference: Int, wordcount: Int) {
    this()
    m_Reference = reference
    m_WordCount = wordcount
    setDataLength(4)
  }

  private def setReference(ref: Int) {
    m_Reference = ref
  }

  def getReference(): Int = m_Reference

  def getByteCount(): Int = m_WordCount * 2

  def getWordCount(): Int = m_WordCount

  private def setWordCount(count: Int) {
    m_WordCount = count
  }

  def writeData(dout: DataOutput) {
    dout.writeShort(m_Reference)
    dout.writeShort(getWordCount)
  }

  def readData(din: DataInput) {
    setReference(din.readUnsignedShort())
    setWordCount(din.readUnsignedShort())
    setDataLength(4)
  }
}
