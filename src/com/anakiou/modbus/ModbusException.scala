package com.anakiou.modbus

class ModbusException extends Exception() {

  var m_Msg = ""
  
  def this(message: String) {
    this()
    m_Msg = message
  }
}