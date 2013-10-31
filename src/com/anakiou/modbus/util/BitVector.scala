package com.anakiou.modbus.util

import BitVector._

object BitVector {

  def createBitVector(data: Array[Byte], size: Int): BitVector = {
    val bv = new BitVector(data.length * 8)
    bv.setBytes(data)
    bv.m_Size = size
    bv
  }

  def createBitVector(data: Array[Byte]): BitVector = {
    val bv = new BitVector(data.length * 8)
    bv.setBytes(data)
    bv
  }

  private val ODD_OFFSETS = Array(-1, -3, -5, -7)

  private val STRAIGHT_OFFSETS = Array(7, 5, 3, 1)
}

class BitVector(var size_x: Int) {

  private var m_Size = size_x

  private var m_Data: Array[Byte] = new Array[Byte](size)

  private var m_MSBAccess: Boolean = false

  size_x = if ((size_x % 8) > 0) (size_x / 8) + 1 else (size_x / 8)

  def toggleAccess() {
    m_MSBAccess = !m_MSBAccess
  }

  def toggleAccess(b: Boolean) {
    m_MSBAccess = b
  }

  def isLSBAccess(): Boolean = !m_MSBAccess

  def isMSBAccess(): Boolean = m_MSBAccess

  def getBytes(): Array[Byte] = m_Data

  def setBytes(data: Array[Byte]) {
    System.arraycopy(data, 0, m_Data, 0, data.length)
  }

  def setBytes(data: Array[Byte], size: Int) {
    System.arraycopy(data, 0, m_Data, 0, data.length)
    m_Size = size
  }

  def getBit(index_m: Int): Boolean = {
    var index = index_m
    index = translateIndex(index)
    if (((m_Data(byteIndex(index)) & (0x01 << bitIndex(index))) !=
      0)) true else false
  }

  def setBit(index_m: Int, b: Boolean) {
    var index = index_m
    index = translateIndex(index)
    val value = (if ((b)) 1 else 0)
    val byteNum = byteIndex(index)
    val bitNum = bitIndex(index)
    m_Data(byteNum) = ((m_Data(byteNum) & ~(0x01 << bitNum)) | ((value & 0x01) << bitNum)).toByte
  }

  def size(): Int = m_Size

  def forceSize(size: Int) {
    if (size > m_Data.length * 8) {
      throw new IllegalArgumentException("Size exceeds byte[] store.")
    } else {
      m_Size = size
    }
  }

  def byteSize(): Int = m_Data.length

  override def toString(): String = {
    val sbuf = new StringBuffer()
    for (i <- 0 until size) {
      sbuf.append((if ((if (((m_Data(byteIndex(i)) & (0x01 << bitIndex(i))) != 0)) true else false)) '1' else '0'))
      if (((i + 1) % 8) == 0) {
        sbuf.append(" ")
      }
    }
    sbuf.toString
  }

  private def byteIndex(index: Int): Int = {
    if (index < 0 || index >= m_Data.length * 8) {
      throw new IndexOutOfBoundsException()
    } else {
      index / 8
    }
  }

  private def bitIndex(index: Int): Int = {
    if (index < 0 || index >= m_Data.length * 8) {
      throw new IndexOutOfBoundsException()
    } else {
      index % 8
    }
  }

  private def translateIndex(idx: Int): Int = {
    if (m_MSBAccess) {
      val mod4 = idx % 4
      val div4 = idx / 4
      if ((div4 % 2) != 0) {
        (idx + ODD_OFFSETS(mod4))
      } else {
        (idx + STRAIGHT_OFFSETS(mod4))
      }
    } else {
      idx
    }
  }
}
