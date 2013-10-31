package com.anakiou.modbus.net

import java.net.InetAddress

import com.anakiou.modbus.Modbus
import com.anakiou.modbus.io.ModbusTransport

class UDPMasterConnection(private var m_Address: InetAddress) {

  private var m_Terminal: UDPMasterTerminal = _

  private var m_Timeout: Int = Modbus.DEFAULT_TIMEOUT

  private var m_Connected: Boolean = _

  private var m_Port: Int = Modbus.DEFAULT_PORT

  def connect() {
    synchronized {
      if (!m_Connected) {
        m_Terminal = new UDPMasterTerminal()
        m_Terminal.setLocalAddress(InetAddress.getLocalHost)
        m_Terminal.setLocalPort(5000)
        m_Terminal.setRemoteAddress(m_Address)
        m_Terminal.setRemotePort(m_Port)
        m_Terminal.setTimeout(m_Timeout)
        m_Terminal.activate()
        m_Connected = true
      }
    }
  }

  def close() {
    if (m_Connected) {
      try {
        m_Terminal.deactivate()
      } catch {
        case ex: Exception => if (Modbus.debug) ex.printStackTrace()
      }
      m_Connected = false
    }
  }

  def getModbusTransport(): ModbusTransport = m_Terminal.getModbusTransport

  def getTerminal(): UDPTerminal = m_Terminal

  def getTimeout(): Int = m_Timeout

  def setTimeout(timeout: Int) {
    m_Timeout = timeout
    m_Terminal.setTimeout(timeout)
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
