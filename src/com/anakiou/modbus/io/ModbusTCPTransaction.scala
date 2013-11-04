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

  private var transactionID: AtomicCounter = new AtomicCounter(Modbus.DEFAULT_TRANSACTION_ID)
}

class ModbusTCPTransaction extends ModbusTransaction {

  private var connection: TCPMasterConnection = _

  private var io: ModbusTransport = _

  private var request: ModbusRequest = _

  private var response: ModbusResponse = _

  private var validityCheck: Boolean = Modbus.DEFAULT_VALIDITYCHECK

  private var reconnecting: Boolean = Modbus.DEFAULT_RECONNECTING

  private var retries: Int = Modbus.DEFAULT_RETRIES

  private var transactionLock: Mutex = new Mutex()

  def this(request: ModbusRequest) {
    this()
    setRequest(request)
  }

  def this(con: TCPMasterConnection) {
    this()
    setConnection(con)
  }

  def setConnection(con: TCPMasterConnection) {
    connection = con
    io = con.getModbusTransport
  }

  def setRequest(req: ModbusRequest) {
    request = req
  }

  def getRequest(): ModbusRequest = request

  def getResponse(): ModbusResponse = response

  def getTransactionID(): Int = transactionID.get

  def setCheckingValidity(b: Boolean) {
    validityCheck = b
  }

  def isCheckingValidity(): Boolean = validityCheck

  def setReconnecting(b: Boolean) {
    reconnecting = b
  }

  def isReconnecting(): Boolean = reconnecting

  def getRetries(): Int = retries

  def setRetries(num: Int) {
    retries = num
  }

  def execute() {
    assertExecutable()
    try {
      transactionLock.acquire()
      if (!connection.isConnected) {
        connection.connect()
        io = connection.getModbusTransport
      }
      var retryCounter = 0
      while (retryCounter < retries) {
        try {
          request.setTransactionID(transactionID.increment())
          io.writeMessage(request)
          response = io.readResponse()
          //break
        } catch {
          case ex: ModbusIOException => if (retryCounter == retries) {
            throw new ModbusIOException("Executing transaction failed (tried " + retries + " times)")
          } else {
            retryCounter += 1
            //continue
          }
        }
      }
      if (response.isInstanceOf[ExceptionResponse]) {
        throw new ModbusSlaveException(response.asInstanceOf[ExceptionResponse].getExceptionCode)
      }
      if (isReconnecting) {
        connection.close()
      }
      if (isCheckingValidity) {
        checkValidity()
      }
    } catch {
      case ex: InterruptedException => throw new ModbusIOException("Thread acquiring lock was interrupted.")
    } finally {
      transactionLock.release()
    }
  }

  private def assertExecutable() {
    if (request == null || connection == null) {
      throw new ModbusException("Assertion failed, transaction not executable")
    }
  }

  protected def checkValidity() {
  }
}
