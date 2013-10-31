package com.anakiou.modbus.io

import java.io.IOException
import java.io.InputStream

class FastByteArrayInputStream(protected var buf: Array[Byte]) extends InputStream {

  protected var count: Int = buf.length

  protected var pos: Int = 0

  protected var mark: Int = 0

  protected var readlimit: Int = -1

  def this(buffer: Array[Byte], offset: Int, length: Int) {
    this(buffer)
    buf = buffer
    pos = offset
    count = length
  }

  def read(): Int = {
    if ((pos < count)) {
      (buf(pos + 1) & 0xff)
    } else {
      (-1)
    }
  }

  override def read(toBuf: Array[Byte], offset: Int, length: Int): Int = {
    val avail = count - pos
    var length_x = length
    if (avail <= 0) {
      return -1
    }
    if (length_x > avail) {
      length_x = avail
    }
    System.arraycopy(buf, pos, toBuf, offset, length)
    pos += length
    length
  }

  override def read(toBuf: Array[Byte]): Int = read(toBuf, 0, toBuf.length)

  override def skip(n: Long): Long = {
    val skip = this.count - this.pos - n.toInt
    if (skip > 0) {
      pos += skip
    }
    skip
  }

  override def close() {
    return
  }

  override def available(): Int = count - pos

  override def mark(limit: Int) {
    mark = pos
    readlimit = limit
  }

  override def markSupported(): Boolean = true

  override def reset() {
    if (readlimit < 0 || pos > mark + readlimit) {
      pos = mark
      readlimit = -1
    } else {
      mark = pos
      readlimit = -1
      throw new IOException("Readlimit exceeded.")
    }
  }

  def getBuffer(): Array[Byte] = buf

  def getPosition(): Int = pos

  def size(): Int = count
}
