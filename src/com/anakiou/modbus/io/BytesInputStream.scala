package com.anakiou.modbus.io

import java.io.DataInput
import java.io.DataInputStream
import java.io.IOException

class BytesInputStream(size: Int) extends FastByteArrayInputStream(Array.ofDim[Byte](size)) with DataInput {

  var din: DataInputStream = new DataInputStream(this)
  var datam: Array[Byte] = new Array[Byte](size)

  def this(data: Array[Byte]) {
    this(data.length)
    datam = data
    din = new DataInputStream(this)
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
    din.readFully(b)
  }

  def readFully(b: Array[Byte], off: Int, len: Int) {
    din.readFully(b, off, len)
  }

  def skipBytes(n: Int): Int = din.skipBytes(n)

  def readBoolean(): Boolean = din.readBoolean()

  def readByte(): Byte = din.readByte()

  def readUnsignedByte(): Int = din.readUnsignedByte()

  def readShort(): Short = din.readShort()

  def readUnsignedShort(): Int = din.readUnsignedShort()

  def readChar(): Char = din.readChar()

  def readInt(): Int = din.readInt()

  def readLong(): Long = din.readLong()

  def readFloat(): Float = din.readFloat()

  def readDouble(): Double = din.readDouble()

  def readLine(): String = throw new IOException("Not supported.")

  def readUTF(): String = din.readUTF()
}
