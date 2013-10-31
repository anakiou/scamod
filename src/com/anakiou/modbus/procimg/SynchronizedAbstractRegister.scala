package com.anakiou.modbus.procimg

abstract class SynchronizedAbstractRegister extends Register {

  protected var m_Register: Array[Byte] = new Array[Byte](2)

  def getValue(): Int = {
    ((m_Register(0) & 0xff) << 8 | (m_Register(1) & 0xff))
  }

  def toUnsignedShort(): Int = {
    ((m_Register(0) & 0xff) << 8 | (m_Register(1) & 0xff))
  }

  def setValue(v: Int) {
    synchronized {
      setValue(v.toShort)
    }
  }

  def toShort(): Short = {
    ((m_Register(0) << 8) | (m_Register(1) & 0xff)).toShort
  }

  def setValue(s: Short) {
    synchronized {
      if (m_Register == null) {
        m_Register = Array.ofDim[Byte](2)
      }
      m_Register(0) = (0xff & (s >> 8)).toByte
      m_Register(1) = (0xff & s).toByte
    }
  }

  def setValue(bytes: Array[Byte]) {
    synchronized {
      if (bytes.length < 2) {
        throw new IllegalArgumentException()
      } else {
        m_Register(0) = bytes(0)
        m_Register(1) = bytes(1)
      }
    }
  }

  def toBytes(): Array[Byte] = m_Register
}
