package com.anakiou.modbus.util

import com.anakiou.modbus.Modbus

class AtomicCounter {

  private var m_Value: Int = 0

  def this(value: Int) {
    this()
    m_Value = value
  }

  def increment(): Int = {
    synchronized {
      if (m_Value == Modbus.MAX_TRANSACTION_ID) {
        m_Value = 0
      }
      m_Value
    }
  }

  def get(): Int = synchronized {
    m_Value
  }
}
