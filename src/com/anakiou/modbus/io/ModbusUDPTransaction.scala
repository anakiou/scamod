package com.anakiou.modbus.io

import com.anakiou.modbus.Modbus
import com.anakiou.modbus.ModbusException
import com.anakiou.modbus.ModbusIOException
import com.anakiou.modbus.ModbusSlaveException
import com.anakiou.modbus.msg.ExceptionResponse
import com.anakiou.modbus.msg.ModbusRequest
import com.anakiou.modbus.msg.ModbusResponse
import com.anakiou.modbus.net.UDPMasterConnection
import com.anakiou.modbus.net.UDPTerminal
import com.anakiou.modbus.util.AtomicCounter
import com.anakiou.modbus.util.Mutex
import ModbusUDPTransaction._

object ModbusUDPTransaction {

  private var c_TransactionID: AtomicCounter = new AtomicCounter(Modbus.DEFAULT_TRANSACTION_ID)
}

class ModbusUDPTransaction extends ModbusTransaction {

  private var m_Terminal: UDPTerminal = _

  private var m_IO: ModbusTransport = _

  private var m_Request: ModbusRequest = _

  private var m_Response: ModbusResponse = _

  private var m_ValidityCheck: Boolean = Modbus.DEFAULT_VALIDITYCHECK

  private var m_Retries: Int = Modbus.DEFAULT_RETRIES

  private var m_RetryCounter: Int = 0

  private var m_TransactionLock: Mutex = new Mutex()

  def this(request: ModbusRequest) {
    this()
    setRequest(request)
  }

  def this(terminal: UDPTerminal) {
    this()
    setTerminal(terminal)
  }

  def this(con: UDPMasterConnection) {
    this()
    setTerminal(con.getTerminal)
  }

  def setTerminal(terminal: UDPTerminal) {
    m_Terminal = terminal
    if (terminal.isActive) {
      m_IO = terminal.getModbusTransport
    }
  }

  def setRequest(req: ModbusRequest) {
    m_Request = req
  }

  def getRequest(): ModbusRequest = m_Request

  def getResponse(): ModbusResponse = m_Response

  def getTransactionID(): Int = c_TransactionID.get

  def setCheckingValidity(b: Boolean) {
    m_ValidityCheck = b
  }

  def isCheckingValidity(): Boolean = m_ValidityCheck

  def getRetries(): Int = m_Retries

  def setRetries(num: Int) {
    m_Retries = num
  }

  def execute() {
    assertExecutable()
    try {
      m_TransactionLock.acquire()
      if (!m_Terminal.isActive) {
        m_Terminal.activate()
        m_IO = m_Terminal.getModbusTransport
      }
      m_RetryCounter = 0
      while (m_RetryCounter <= m_Retries) {
        try {
          m_Request.setTransactionID(c_TransactionID.increment())
          synchronized {
            m_IO.writeMessage(m_Request)
            m_Response = m_IO.readResponse()
            //break
          }
        } catch {
          case ex: ModbusIOException => {
            m_RetryCounter += 1
            //continue
          }
        }
      }
      if (m_Response.isInstanceOf[ExceptionResponse]) {
        throw new ModbusSlaveException(m_Response.asInstanceOf[ExceptionResponse].getExceptionCode)
      }
      if (isCheckingValidity) {
        checkValidity()
      }
    } catch {
      case ex: InterruptedException => throw new ModbusIOException("Thread acquiring lock was interrupted.")
    } finally {
      m_TransactionLock.release()
    }
  }

  private def assertExecutable() {
    if (m_Request == null || m_Terminal == null) {
      throw new ModbusException("Assertion failed, transaction not executable")
    }
  }

  protected def checkValidity() {
  }
}
