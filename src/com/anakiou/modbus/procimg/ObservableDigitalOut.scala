package com.anakiou.modbus.procimg

import com.anakiou.modbus.util.Observable

class ObservableDigitalOut extends Observable with DigitalOut {

  protected var m_Set: Boolean = _

  def isSet(): Boolean = m_Set

  def set(b: Boolean) {
    m_Set = b
    notifyObservers("value")
  }
}
