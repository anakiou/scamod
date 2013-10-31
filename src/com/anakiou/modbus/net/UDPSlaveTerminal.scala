package com.anakiou.modbus.net

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.util.Hashtable

import com.anakiou.modbus.Modbus
import com.anakiou.modbus.io.ModbusUDPTransport
import com.anakiou.modbus.util.LinkedQueue
import com.anakiou.modbus.util.ModbusUtil

class UDPSlaveTerminal protected () extends UDPTerminal {

  private var m_Socket: DatagramSocket = _

  private var m_Timeout: Int = Modbus.DEFAULT_TIMEOUT

  private var m_Active: Boolean = _

  protected var m_LocalAddress: InetAddress = _

  private var m_LocalPort: Int = Modbus.DEFAULT_PORT

  protected var m_ModbusTransport: ModbusUDPTransport = _

  private var m_Retries: Int = Modbus.DEFAULT_RETRIES

  private var m_SendQueue: LinkedQueue = new LinkedQueue()

  private var m_ReceiveQueue: LinkedQueue = new LinkedQueue()

  private var m_PacketSender: PacketSender = _

  private var m_PacketReceiver: PacketReceiver = _

  private var m_Receiver: Thread = _

  private var m_Sender: Thread = _

  protected var m_Requests: Hashtable[java.lang.Integer, DatagramPacket] = new Hashtable(342)

  def this(localaddress: InetAddress) {
    this()
    m_LocalAddress = localaddress
    m_SendQueue = new LinkedQueue()
    m_ReceiveQueue = new LinkedQueue()
    m_Requests = new Hashtable(342)
  }

  def getLocalAddress(): InetAddress = m_LocalAddress

  def getLocalPort(): Int = m_LocalPort

  def setLocalPort(port: Int) {
    m_LocalPort = port
  }

  def isActive(): Boolean = m_Active

  def activate() {
    synchronized {
      if (!isActive) {
        if (Modbus.debug) println("UDPSlaveTerminal::activate()")
        if (m_Socket == null) {
          if (m_LocalAddress != null && m_LocalPort != -1) {
            m_Socket = new DatagramSocket(m_LocalPort, m_LocalAddress)
          } else {
            m_Socket = new DatagramSocket()
            m_LocalPort = m_Socket.getLocalPort
            m_LocalAddress = m_Socket.getLocalAddress
          }
        }
        if (Modbus.debug) println("UDPSlaveTerminal::haveSocket():" + m_Socket.toString)
        if (Modbus.debug) println("UDPSlaveTerminal::addr=:" + m_LocalAddress.toString +
          ":port=" +
          m_LocalPort)
        m_Socket.setReceiveBufferSize(1024)
        m_Socket.setSendBufferSize(1024)
        m_PacketReceiver = new PacketReceiver()
        m_Receiver = new Thread(m_PacketReceiver)
        m_Receiver.start()
        if (Modbus.debug) println("UDPSlaveTerminal::receiver started()")
        m_PacketSender = new PacketSender()
        m_Sender = new Thread(m_PacketSender)
        m_Sender.start()
        if (Modbus.debug) println("UDPSlaveTerminal::sender started()")
        m_ModbusTransport = new ModbusUDPTransport(this)
        if (Modbus.debug) println("UDPSlaveTerminal::transport created")
        m_Active = true
      }
      if (Modbus.debug) println("UDPSlaveTerminal::activated")
    }
  }

  def deactivate() {
    try {
      if (m_Active) {
        m_PacketReceiver.stop()
        m_Receiver.join()
        m_PacketSender.stop()
        m_Sender.join()
        m_Socket.close()
        m_ModbusTransport = null
        m_Active = false
      }
    } catch {
      case ex: Exception => ex.printStackTrace()
    }
  }

  def getModbusTransport(): ModbusUDPTransport = m_ModbusTransport

  protected def hasResponse(): Boolean = !m_ReceiveQueue.isEmpty

  def getSocket(): DatagramSocket = m_Socket

  protected def setSocket(sock: DatagramSocket) {
    m_Socket = sock
  }

  def sendMessage(msg: Array[Byte]) {
    m_SendQueue.put(msg)
  }

  def receiveMessage(): Array[Byte] = {
    m_ReceiveQueue.take().asInstanceOf[Array[Byte]]
  }

  class PacketSender extends Runnable {

    private var m_Continue: Boolean = true

    def run() {
      do {
        try {
          val message = m_SendQueue.take().asInstanceOf[Array[Byte]]
          val req = m_Requests.remove(new java.lang.Integer(ModbusUtil.registersToInt(message))).asInstanceOf[DatagramPacket]
          val res = new DatagramPacket(message, message.length, req.getAddress, req.getPort)
          m_Socket.send(res)
          if (Modbus.debug) println("Sent package from queue.")
        } catch {
          case ex: Exception => println(ex.getMessage())
        }
      } while (m_Continue || !m_SendQueue.isEmpty);
    }

    def stop() {
      m_Continue = false
    }
  }

  class PacketReceiver extends Runnable {

    private var m_Continue: Boolean = true

    def run() {
      do {
        try {
          val buffer = Array.ofDim[Byte](256)
          val packet = new DatagramPacket(buffer, buffer.length)
          m_Socket.receive(packet)
          val tid = new java.lang.Integer(ModbusUtil.registersToInt(buffer))
          m_Requests.put(tid, packet)
          m_ReceiveQueue.put(buffer)
          if (Modbus.debug) println("Received package to queue.")
        } catch {
          case ex: Exception => println(ex.getMessage())
        }
      } while (m_Continue);
    }

    def stop() {
      m_Continue = false
    }
  }
}
