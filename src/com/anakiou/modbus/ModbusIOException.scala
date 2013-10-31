package com.anakiou.modbus

class ModbusIOException extends ModbusException {

  private var m_EOF: Boolean = false

  def this(message: String) {
    this()
    m_Msg = message
  }

  def this(b: Boolean) {
    this()
    m_EOF = b
  }

  def this(message: String, b: Boolean) {
    this()
    m_Msg = message
    m_EOF = b
  }

  def isEOF(): Boolean = m_EOF

  def setEOF(b: Boolean) {
    m_EOF = b
  }
}