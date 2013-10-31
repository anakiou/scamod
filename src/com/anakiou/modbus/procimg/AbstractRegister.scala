package com.anakiou.modbus.procimg

abstract class AbstractRegister extends Register {

  protected var m_Register: Array[Byte] = new Array[Byte](2)

  def getValue(): Int = {
    ((m_Register(0) & 0xff) << 8 | (m_Register(1) & 0xff))
  }

  def toUnsignedShort(): Int = {
    ((m_Register(0) & 0xff) << 8 | (m_Register(1) & 0xff))
  }

  def setValue(v: Int) {
    m_Register(0) = (0xff & (v >> 8)).toByte
    m_Register(1) = (0xff & v).toByte
  }

  def toShort(): Short = {
    ((m_Register(0) << 8) | (m_Register(1) & 0xff)).toShort
  }

  def setValue(s: Short) {
    m_Register(0) = (0xff & (s >> 8)).toByte
    m_Register(1) = (0xff & s).toByte
  }

  def toBytes(): Array[Byte] = m_Register

  def setValue(bytes: Array[Byte]) {
    if (bytes.length < 2) {
      throw new IllegalArgumentException()
    } else {
      m_Register(0) = bytes(0)
      m_Register(1) = bytes(1)
    }
  }
}
