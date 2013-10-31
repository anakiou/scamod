package com.anakiou.modbus.io

import java.io.IOException
import java.io.OutputStream
import java.io.UnsupportedEncodingException
import FastByteArrayOutputStream._

object FastByteArrayOutputStream {

  val DEFAULT_SIZE = 512
}

class FastByteArrayOutputStream extends OutputStream {

  protected var count: Int = _

  protected var buf: Array[Byte] = new Array[Byte](DEFAULT_SIZE)

  def this(bufferSize: Int) {
    this()
    buf = Array.ofDim[Byte](bufferSize)
  }

  def this(buf: Array[Byte]) {
    this()
    this.buf = buf
  }

  override def close() {
  }

  def reset() {
    count = 0
  }

  def size(): Int = count

  def toByteArray(): Array[Byte] = {
    val buf = Array.ofDim[Byte](count)
    System.arraycopy(this.buf, 0, buf, 0, count)
    buf
  }

  override def toString(): String = new String(buf, 0, count)

  def toString(enc: String): String = new String(buf, 0, count, enc)

  override def write(b: Array[Byte], off: Int, len: Int) {
    ensureCapacity(count + len)
    System.arraycopy(b, off, buf, count, len)
    count += len
  }

  def write(b: Int) {
    ensureCapacity(count + 1)
    buf(count + 1) = b.toByte
  }

  def writeTo(out: OutputStream) {
    out.write(buf, 0, count)
  }

  override def write(buf: Array[Byte]) {
    write(buf, 0, buf.length)
  }

  def ensureCapacity(minCapacity: Int) {
    if (minCapacity < buf.length) {
      return
    } else {
      val newbuf = Array.ofDim[Byte](minCapacity)
      System.arraycopy(buf, 0, newbuf, 0, count)
      buf = newbuf
    }
  }

  def toByteArray(b: Array[Byte], offset: Int) {
    if (offset >= b.length) {
      throw new IndexOutOfBoundsException()
    }
    val len = count - offset
    if (len > b.length) {
      System.arraycopy(buf, offset, b, offset, b.length)
    } else {
      System.arraycopy(buf, offset, b, offset, len)
    }
  }

  def getBuffer(): Array[Byte] = buf
}
