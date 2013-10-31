package com.anakiou.modbus.io

import java.io.DataInput
import java.io.DataInputStream
import java.io.IOException

class BytesInputStream(size: Int) extends FastByteArrayInputStream(Array.ofDim[Byte](size)) with DataInput {

  var m_Din: DataInputStream = new DataInputStream(this)
  var m_Data: Array[Byte] = new Array[Byte](size)

  def this(data: Array[Byte]) {
    this(data.length)
    m_Data = data
    m_Din = new DataInputStream(this)
  }

  def reset(data: Array[Byte]) {
    pos = 0
    mark = 0
    buf = data
    count = data.length
  }

  def reset(data: Array[Byte], length: Int) {
    pos = 0
    mark = 0
    count = length
    buf = data
    readlimit = -1
  }

  def reset(length: Int) {
    pos = 0
    count = length
  }

  def skip(n: Int): Int = {
    mark(pos)
    pos += n
    n
  }

  override def getBuffer(): Array[Byte] = buf

  def getBufferLength(): Int = buf.length

  def readFully(b: Array[Byte]) {
    m_Din.readFully(b)
  }

  def readFully(b: Array[Byte], off: Int, len: Int) {
    m_Din.readFully(b, off, len)
  }

  def skipBytes(n: Int): Int = m_Din.skipBytes(n)

  def readBoolean(): Boolean = m_Din.readBoolean()

  def readByte(): Byte = m_Din.readByte()

  def readUnsignedByte(): Int = m_Din.readUnsignedByte()

  def readShort(): Short = m_Din.readShort()

  def readUnsignedShort(): Int = m_Din.readUnsignedShort()

  def readChar(): Char = m_Din.readChar()

  def readInt(): Int = m_Din.readInt()

  def readLong(): Long = m_Din.readLong()

  def readFloat(): Float = m_Din.readFloat()

  def readDouble(): Double = m_Din.readDouble()

  def readLine(): String = throw new IOException("Not supported.")

  def readUTF(): String = m_Din.readUTF()
}
