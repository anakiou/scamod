package com.anakiou.modbus.io

import java.io.DataOutput
import java.io.InterruptedIOException

import com.anakiou.modbus.Modbus
import com.anakiou.modbus.ModbusIOException
import com.anakiou.modbus.msg.ModbusMessage
import com.anakiou.modbus.msg.ModbusRequest
import com.anakiou.modbus.msg.ModbusResponse
import com.anakiou.modbus.net.UDPTerminal

class ModbusUDPTransport(private var m_Terminal: UDPTerminal) extends ModbusTransport {

  private var m_ByteOut: BytesOutputStream = new BytesOutputStream(Modbus.MAX_IP_MESSAGE_LENGTH)

  private var m_ByteIn: BytesInputStream = new BytesInputStream(Modbus.MAX_IP_MESSAGE_LENGTH)

  def close() {
  }

  def writeMessage(msg: ModbusMessage) {
    synchronized {
      m_ByteOut.reset()
      msg.writeTo(m_ByteOut.asInstanceOf[DataOutput])
      m_Terminal.sendMessage(m_ByteOut.toByteArray())
    }
  }

  def readRequest(): ModbusRequest = {
    var req: ModbusRequest = null
    synchronized {
      m_ByteIn.reset(m_Terminal.receiveMessage())
      m_ByteIn.skip(7)
      val functionCode = m_ByteIn.readUnsignedByte()
      m_ByteIn.reset()
      req = ModbusRequest.createModbusRequest(functionCode)
      req.readFrom(m_ByteIn)
    }
    req
  }

  def readResponse(): ModbusResponse = {
    try {
      var res: ModbusResponse = null
      synchronized {
        m_ByteIn.reset(m_Terminal.receiveMessage())
        m_ByteIn.skip(7)
        val functionCode = m_ByteIn.readUnsignedByte()
        m_ByteIn.reset()
        res = ModbusResponse.createModbusResponse(functionCode)
        res.readFrom(m_ByteIn)
      }
      res
    } catch {
      case ioex: InterruptedIOException => throw new ModbusIOException("Socket timed out.")
      case ex: Exception => {
        ex.printStackTrace()
        throw new ModbusIOException("I/O exception - failed to read.")
      }
    }
  }
}
