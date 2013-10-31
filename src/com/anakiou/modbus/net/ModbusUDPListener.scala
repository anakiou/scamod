package com.anakiou.modbus.net

import java.net.InetAddress

import com.anakiou.modbus.Modbus
import com.anakiou.modbus.ModbusCoupler
import com.anakiou.modbus.ModbusIOException
import com.anakiou.modbus.io.ModbusUDPTransport
import com.anakiou.modbus.msg.ModbusResponse

class ModbusUDPListener {

  private var m_Terminal: UDPSlaveTerminal = _

  private var m_Handler: ModbusUDPHandler = _

  private var m_HandlerThread: Thread = _

  private var m_Port: Int = Modbus.DEFAULT_PORT

  private var m_Listening: Boolean = _

  private var m_Interface: InetAddress = _

  def this(ifc: InetAddress) {
    this()
    m_Interface = ifc
  }

  def getPort(): Int = m_Port

  def setPort(port: Int) {
    m_Port = (if ((port > 0)) port else Modbus.DEFAULT_PORT)
  }

  def start() {
    try {
      m_Terminal = if (m_Interface == null) new UDPSlaveTerminal(InetAddress.getLocalHost) else new UDPSlaveTerminal(m_Interface)
      m_Terminal.setLocalPort(m_Port)
      m_Terminal.activate()
      m_Handler = new ModbusUDPHandler(m_Terminal.getModbusTransport)
      m_HandlerThread = new Thread(m_Handler)
      m_HandlerThread.start()
    } catch {
      case e: Exception =>
    }
    m_Listening = true
  }

  def stop() {
    m_Terminal.deactivate()
    m_Handler.stop()
    m_Listening = false
  }

  def isListening(): Boolean = m_Listening

  class ModbusUDPHandler(private var m_Transport: ModbusUDPTransport) extends Runnable {

    private var m_Continue: Boolean = true

    def run() {
      try {
        do {
          val request = m_Transport.readRequest()
          var response: ModbusResponse = null
          response = if (ModbusCoupler.getReference.getProcessImage == null) request.createExceptionResponse(Modbus.ILLEGAL_FUNCTION_EXCEPTION) else request.createResponse()
          if (Modbus.debug) println("Request:" + request.getHexMessage)
          if (Modbus.debug) println("Response:" + response.getHexMessage)
          m_Transport.writeMessage(response)
        } while (m_Continue);
      } catch {
        case ex: ModbusIOException => if (!ex.isEOF) {
          ex.printStackTrace()
        }
      } finally {
        try {
          m_Terminal.deactivate()
        } catch {
          case ex: Exception =>
        }
      }
    }

    def stop() {
      m_Continue = false
    }
  }
}
