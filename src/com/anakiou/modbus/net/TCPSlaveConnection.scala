package com.anakiou.modbus.net

import java.io.IOException
import java.net.InetAddress
import java.net.Socket

import com.anakiou.modbus.Modbus
import com.anakiou.modbus.io.ModbusTCPTransport
import com.anakiou.modbus.io.ModbusTransport

class TCPSlaveConnection(socket: Socket) {

  private var m_Socket: Socket = _

  private var m_Timeout: Int = Modbus.DEFAULT_TIMEOUT

  private var m_Connected: Boolean = _

  private var m_ModbusTransport: ModbusTCPTransport = _

  try {
    setSocket(socket)
  } catch {
    case ex: IOException => {
      if (Modbus.debug) println("TCPSlaveConnection::Socket invalid.")
      throw new IllegalStateException("Socket invalid.")
    }
  }

  def close() {
    if (m_Connected) {
      try {
        m_ModbusTransport.close()
        m_Socket.close()
      } catch {
        case ex: IOException => if (Modbus.debug) ex.printStackTrace()
      }
      m_Connected = false
    }
  }

  def getModbusTransport(): ModbusTransport = m_ModbusTransport

  private def setSocket(socket: Socket) {
    m_Socket = socket
    if (m_ModbusTransport == null) {
      m_ModbusTransport = new ModbusTCPTransport(m_Socket)
    } else {
      m_ModbusTransport.setSocket(m_Socket)
    }
    m_Connected = true
  }

  def getTimeout(): Int = m_Timeout

  def setTimeout(timeout: Int) {
    m_Timeout = timeout
    try {
      m_Socket.setSoTimeout(m_Timeout)
    } catch {
      case ex: IOException =>
    }
  }

  def getPort(): Int = m_Socket.getLocalPort

  def getAddress(): InetAddress = m_Socket.getLocalAddress

  def isConnected(): Boolean = m_Connected
}
