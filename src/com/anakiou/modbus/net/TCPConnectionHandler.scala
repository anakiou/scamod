package com.anakiou.modbus.net

import com.anakiou.modbus.Modbus
import com.anakiou.modbus.ModbusCoupler
import com.anakiou.modbus.ModbusIOException
import com.anakiou.modbus.io.ModbusTransport
import com.anakiou.modbus.msg.ModbusResponse

class TCPConnectionHandler(con: TCPSlaveConnection) extends Runnable {

  private var m_Connection: TCPSlaveConnection = _

  private var m_Transport: ModbusTransport = _

  setConnection(con)

  def setConnection(con: TCPSlaveConnection) {
    m_Connection = con
    m_Transport = m_Connection.getModbusTransport
  }

  def run() {
    try {
      do {
        val request = m_Transport.readRequest()
        var response: ModbusResponse = null
        response = if (ModbusCoupler.getReference.getProcessImage == null) request.createExceptionResponse(Modbus.ILLEGAL_FUNCTION_EXCEPTION) else request.createResponse()
        if (Modbus.debug) println("Request:" + request.getHexMessage)
        if (Modbus.debug) println("Response:" + response.getHexMessage)
        m_Transport.writeMessage(response)
      } while (true);
    } catch {
      case ex: ModbusIOException => if (!ex.isEOF) {
        ex.printStackTrace()
      }
    } finally {
      try {
        m_Connection.close()
      } catch {
        case ex: Exception =>
      }
    }
  }
}
