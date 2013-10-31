package com.anakiou.modbus.io

import com.anakiou.modbus.Modbus
import com.anakiou.modbus.ModbusException
import com.anakiou.modbus.ModbusIOException
import com.anakiou.modbus.ModbusSlaveException
import com.anakiou.modbus.msg.ExceptionResponse
import com.anakiou.modbus.msg.ModbusRequest
import com.anakiou.modbus.msg.ModbusResponse
import com.anakiou.modbus.net.TCPMasterConnection
import com.anakiou.modbus.util.AtomicCounter
import com.anakiou.modbus.util.Mutex
import ModbusTCPTransaction._

object ModbusTCPTransaction {

  private var c_TransactionID: AtomicCounter = new AtomicCounter(Modbus.DEFAULT_TRANSACTION_ID)
}

class ModbusTCPTransaction extends ModbusTransaction {

  private var m_Connection: TCPMasterConnection = _

  private var m_IO: ModbusTransport = _

  private var m_Request: ModbusRequest = _

  private var m_Response: ModbusResponse = _

  private var m_ValidityCheck: Boolean = Modbus.DEFAULT_VALIDITYCHECK

  private var m_Reconnecting: Boolean = Modbus.DEFAULT_RECONNECTING

  private var m_Retries: Int = Modbus.DEFAULT_RETRIES

  private var m_TransactionLock: Mutex = new Mutex()

  def this(request: ModbusRequest) {
    this()
    setRequest(request)
  }

  def this(con: TCPMasterConnection) {
    this()
    setConnection(con)
  }

  def setConnection(con: TCPMasterConnection) {
    m_Connection = con
    m_IO = con.getModbusTransport
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

  def setReconnecting(b: Boolean) {
    m_Reconnecting = b
  }

  def isReconnecting(): Boolean = m_Reconnecting

  def getRetries(): Int = m_Retries

  def setRetries(num: Int) {
    m_Retries = num
  }

  def execute() {
    assertExecutable()
    try {
      m_TransactionLock.acquire()
      if (!m_Connection.isConnected) {
        m_Connection.connect()
        m_IO = m_Connection.getModbusTransport
      }
      var retryCounter = 0
      while (retryCounter < m_Retries) {
        try {
          m_Request.setTransactionID(c_TransactionID.increment())
          m_IO.writeMessage(m_Request)
          m_Response = m_IO.readResponse()
          //break
        } catch {
          case ex: ModbusIOException => if (retryCounter == m_Retries) {
            throw new ModbusIOException("Executing transaction failed (tried " + m_Retries + " times)")
          } else {
            retryCounter += 1
            //continue
          }
        }
      }
      if (m_Response.isInstanceOf[ExceptionResponse]) {
        throw new ModbusSlaveException(m_Response.asInstanceOf[ExceptionResponse].getExceptionCode)
      }
      if (isReconnecting) {
        m_Connection.close()
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
    if (m_Request == null || m_Connection == null) {
      throw new ModbusException("Assertion failed, transaction not executable")
    }
  }

  protected def checkValidity() {
  }
}
