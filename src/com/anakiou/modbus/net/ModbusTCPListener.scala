package com.anakiou.modbus.net

import java.io.IOException
import java.net.InetAddress
import java.net.ServerSocket
import java.net.SocketException
import java.net.UnknownHostException

import com.anakiou.modbus.Modbus
import com.anakiou.modbus.util.ThreadPool

object ModbusTCPListener {

  private var c_RequestCounter: Int = 0

  private val REQUESTS_TOGC = 10
  
  private def count() {
    c_RequestCounter += 1
    if (c_RequestCounter == REQUESTS_TOGC) {
      System.gc()
      c_RequestCounter = 0
    }
  }
}

class ModbusTCPListener(poolsize: Int) extends Runnable {

  private var m_ServerSocket: ServerSocket = null

  private var m_ThreadPool: ThreadPool = new ThreadPool(poolsize)

  private var m_Listener: Thread = _

  private var m_Port: Int = Modbus.DEFAULT_PORT

  private var m_FloodProtection: Int = 5

  private var m_Listening: Boolean = _

  private var m_Address: InetAddress = _

  try {
    m_Address = InetAddress.getLocalHost
  } catch {
    case ex: UnknownHostException =>
  }

  def this(poolsize: Int, addr: InetAddress) {
    this(poolsize)
    m_ThreadPool = new ThreadPool(poolsize)
    m_Address = addr
  }

  def setPort(port: Int) {
    m_Port = port
  }

  def setAddress(addr: InetAddress) {
    m_Address = addr
  }

  def start() {
    m_Listener = new Thread(this)
    m_Listener.start()
    m_Listening = true
  }

  def stop() {
    m_Listening = false
    try {
      m_ServerSocket.close()
      m_Listener.join()
    } catch {
      case ex: Exception =>
    }
  }

  def run() {
    try {
      m_ServerSocket = new ServerSocket(m_Port, m_FloodProtection, m_Address)
      if (Modbus.debug) println("Listenening to " + m_ServerSocket.toString + "(Port " +
        m_Port +
        ")")
      do {
        val incoming = m_ServerSocket.accept()
        if (Modbus.debug) println("Making new connection " + incoming.toString)
        if (m_Listening) {
          m_ThreadPool.execute(new TCPConnectionHandler(new TCPSlaveConnection(incoming)))
          ModbusTCPListener.count()
        } else {
          incoming.close()
        }
      } while (m_Listening);
    } catch {
      case iex: SocketException => if (!m_Listening) {
        return
      } else {
        iex.printStackTrace()
      }
      case e: IOException =>
    }
  }

  def isListening(): Boolean = m_Listening
}
