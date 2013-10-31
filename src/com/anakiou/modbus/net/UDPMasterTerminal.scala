package com.anakiou.modbus.net

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

import com.anakiou.modbus.Modbus
import com.anakiou.modbus.io.ModbusUDPTransport

class UDPMasterTerminal extends UDPTerminal {

  private var m_Socket: DatagramSocket = _

  private var m_Timeout: Int = Modbus.DEFAULT_TIMEOUT

  private var m_Active: Boolean = _

  protected var m_LocalAddress: InetAddress = _

  protected var m_RemoteAddress: InetAddress = _

  private var m_RemotePort: Int = Modbus.DEFAULT_PORT

  private var m_LocalPort: Int = Modbus.DEFAULT_PORT

  protected var m_ModbusTransport: ModbusUDPTransport = _

  protected def this(addr: InetAddress) {
    this()
    m_RemoteAddress = addr
  }

  def getLocalAddress(): InetAddress = m_LocalAddress

  def setLocalAddress(addr: InetAddress) {
    m_LocalAddress = addr
  }

  def getLocalPort(): Int = m_LocalPort

  def setLocalPort(port: Int) {
    m_LocalPort = port
  }

  def getRemotePort(): Int = m_RemotePort

  def setRemotePort(port: Int) {
    m_RemotePort = port
  }

  def getRemoteAddress(): InetAddress = m_RemoteAddress

  def setRemoteAddress(adr: InetAddress) {
    m_RemoteAddress = adr
  }

  def isActive(): Boolean = m_Active

  def activate() {
    synchronized {
      if (!isActive) {
        if (Modbus.debug) println("UDPMasterTerminal::activate()::laddr=:" + m_LocalAddress +
          ":lport=" +
          m_LocalPort)
        if (m_Socket == null) {
          if (m_LocalAddress != null && m_LocalPort != -1) {
            m_Socket = new DatagramSocket(m_LocalPort, m_LocalAddress)
          } else {
            m_Socket = new DatagramSocket()
            m_LocalPort = m_Socket.getLocalPort
            m_LocalAddress = m_Socket.getLocalAddress
          }
        }
        if (Modbus.debug) println("UDPMasterTerminal::haveSocket():" + m_Socket.toString)
        if (Modbus.debug) println("UDPMasterTerminal::laddr=:" + m_LocalAddress.toString +
          ":lport=" +
          m_LocalPort)
        if (Modbus.debug) println("UDPMasterTerminal::raddr=:" + m_RemoteAddress.toString +
          ":rport=" +
          m_RemotePort)
        m_Socket.setReceiveBufferSize(1024)
        m_Socket.setSendBufferSize(1024)
        m_ModbusTransport = new ModbusUDPTransport(this)
        m_Active = true
      }
      if (Modbus.debug) println("UDPMasterTerminal::activated")
    }
  }

  def deactivate() {
    try {
      if (Modbus.debug) println("UDPMasterTerminal::deactivate()")
      m_Socket.close()
      m_ModbusTransport = null
      m_Active = false
    } catch {
      case ex: Exception => ex.printStackTrace()
    }
  }

  def getModbusTransport(): ModbusUDPTransport = m_ModbusTransport

  def getTimeout(): Int = m_Timeout

  def setTimeout(timeout: Int) {
    m_Timeout = timeout
  }

  def sendMessage(msg: Array[Byte]) {
    val req = new DatagramPacket(msg, msg.length, m_RemoteAddress, m_RemotePort)
    synchronized {
      m_Socket.send(req)
    }
  }

  def receiveMessage(): Array[Byte] = {
    val buffer = Array.ofDim[Byte](256)
    val packet = new DatagramPacket(buffer, buffer.length)
    synchronized {
      m_Socket.setSoTimeout(m_Timeout)
      m_Socket.receive(packet)
    }
    buffer
  }

  def receiveMessage(buffer: Array[Byte]) {
    val packet = new DatagramPacket(buffer, buffer.length)
    m_Socket.setSoTimeout(m_Timeout)
    m_Socket.receive(packet)
  }
}
