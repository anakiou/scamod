package com.anakiou.modbus

class ModbusSlaveException(private var m_Type: Int) extends ModbusException() {

  def getType(): Int = m_Type

  def isType(TYPE: Int): Boolean = (TYPE == m_Type)

  override def getMessage(): String = "Error Code = " + m_Type
}
