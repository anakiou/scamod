package com.anakiou.modbus.io

import java.io.DataOutput
import java.io.DataOutputStream

class BytesOutputStream(size: Int) extends FastByteArrayOutputStream(size) with DataOutput {

  private var m_Dout: DataOutputStream = new DataOutputStream(this)

  def this(buffer: Array[Byte]) {
    this(buffer.length)
    buf = buffer
    count = 0
    m_Dout = new DataOutputStream(this)
  }

  override def getBuffer(): Array[Byte] = buf

  override def reset() {
    count = 0
  }

  def writeBoolean(v: Boolean) {
    m_Dout.writeBoolean(v)
  }

  def writeByte(v: Int) {
    m_Dout.writeByte(v)
  }

  def writeShort(v: Int) {
    m_Dout.writeShort(v)
  }

  def writeChar(v: Int) {
    m_Dout.writeChar(v)
  }

  def writeInt(v: Int) {
    m_Dout.writeInt(v)
  }

  def writeLong(v: Long) {
    m_Dout.writeLong(v)
  }

  def writeFloat(v: Float) {
    m_Dout.writeFloat(v)
  }

  def writeDouble(v: Double) {
    m_Dout.writeDouble(v)
  }

  def writeBytes(s: String) {
    val len = s.length
    for (i <- 0 until len) {
      this.write(s.charAt(i).toByte)
    }
  }

  def writeChars(s: String) {
    m_Dout.writeChars(s)
  }

  def writeUTF(str: String) {
    m_Dout.writeUTF(str)
  }
}
