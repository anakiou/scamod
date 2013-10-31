package com.anakiou.modbus.net

import java.io.IOException
import java.net.InetAddress
import java.net.Socket

import com.anakiou.modbus.Modbus
import com.anakiou.modbus.io.ModbusTCPTransport
import com.anakiou.modbus.io.ModbusTransport

class TCPMasterConnection(private var m_Address: InetAddress) {

  private var m_Socket: Socket = _

  private var m_Timeout: Int = Modbus.DEFAULT_TIMEOUT

  private var m_Connected: Boolean = _

  private var m_Port: Int = Modbus.DEFAULT_PORT

  private var m_ModbusTransport: ModbusTCPTransport = _

  def connect() {
    synchronized {
      if (!m_Connected) {
        if (Modbus.debug) println("connect()")
        m_Socket = new Socket(m_Address, m_Port)
        setTimeout(m_Timeout)
        prepareTransport()
        m_Connected = true
      }
    }
  }

  def close() {
    if (m_Connected) {
      try {
        m_ModbusTransport.close()
      } catch {
        case ex: IOException => if (Modbus.debug) println("close()")
      }
      m_Connected = false
    }
  }

  def getModbusTransport(): ModbusTransport = m_ModbusTransport

  private def prepareTransport() {
    if (m_ModbusTransport == null) {
      m_ModbusTransport = new ModbusTCPTransport(m_Socket)
    } else {
      m_ModbusTransport.setSocket(m_Socket)
    }
  }

  def getTimeout(): Int = m_Timeout

  def setTimeout(timeout: Int) {
    m_Timeout = timeout
    if (m_Socket != null) {
      try {
        m_Socket.setSoTimeout(m_Timeout)
      } catch {
        case ex: IOException =>
      }
    }
  }

  def getPort(): Int = m_Port

  def setPort(port: Int) {
    m_Port = port
  }

  def getAddress(): InetAddress = m_Address

  def setAddress(adr: InetAddress) {
    m_Address = adr
  }

  def isConnected(): Boolean = m_Connected
}
