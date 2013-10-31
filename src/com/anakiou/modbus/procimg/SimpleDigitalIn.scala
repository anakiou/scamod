package com.anakiou.modbus.procimg

class SimpleDigitalIn extends DigitalIn {

  protected var m_Set: Boolean = false

  def this(b: Boolean) {
    this()
    set(b)
  }

  def isSet(): Boolean = m_Set

  def set(b: Boolean) {
    synchronized {
      m_Set = b
    }
  }
}
