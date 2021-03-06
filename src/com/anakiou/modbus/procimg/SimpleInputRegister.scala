package com.anakiou.modbus.procimg

class SimpleInputRegister extends SynchronizedAbstractRegister with InputRegister {

  def this(b1: Byte, b2: Byte) {
    this()
    m_Register(0) = b1
    m_Register(1) = b2
  }

  def this(value: Int) {
    this()
    setValue(value)
  }
}
