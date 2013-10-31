package com.anakiou.modbus.procimg

class SimpleDigitalOut extends DigitalOut {

  protected var m_Set: Boolean = _

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
