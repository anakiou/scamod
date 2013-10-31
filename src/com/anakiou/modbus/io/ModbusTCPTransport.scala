package com.anakiou.modbus.io

import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.DataInputStream
import java.io.DataOutput
import java.io.DataOutputStream
import java.io.EOFException
import java.io.IOException
import java.net.Socket
import java.net.SocketException
import com.anakiou.modbus.Modbus
import com.anakiou.modbus.ModbusIOException
import com.anakiou.modbus.msg.ModbusMessage
import com.anakiou.modbus.msg.ModbusRequest
import com.anakiou.modbus.msg.ModbusResponse
import com.anakiou.modbus.util.ModbusUtil

class ModbusTCPTransport(socket: Socket) extends ModbusTransport {

  private var m_Input: DataInputStream = _

  private var m_Output: DataOutputStream = _

  private var m_ByteIn: BytesInputStream = _

  try {
    setSocket(socket)
  } catch {
    case ex: IOException => {
      if (Modbus.debug) println("ModbusTCPTransport::Socket invalid.")
      throw new IllegalStateException("Socket invalid.")
    }
  }

  def setSocket(socket: Socket) {
    prepareStreams(socket)
  }

  def close() {
    m_Input.close()
    m_Output.close()
  }

  def writeMessage(msg: ModbusMessage) {
    msg.writeTo(m_Output.asInstanceOf[DataOutput])
    m_Output.flush()
  }

  def readRequest(): ModbusRequest = {
    try {
      var req: ModbusRequest = null
      synchronized {
        val buffer = m_ByteIn.getBuffer
        if (m_Input.read(buffer, 0, 6) == -1) {
          throw new EOFException("Premature end of stream (Header truncated).")
        }
        val bf = ModbusUtil.registerToShort(buffer, 4)
        if (m_Input.read(buffer, 6, bf) == -1) {
          throw new ModbusIOException("Premature end of stream (Message truncated).")
        }
        m_ByteIn.reset(buffer, (6 + bf))
        m_ByteIn.skip(7)
        val functionCode = m_ByteIn.readUnsignedByte()
        m_ByteIn.reset()
        req = ModbusRequest.createModbusRequest(functionCode)
        req.readFrom(m_ByteIn)
      }
      req
    } catch {
      case eoex: EOFException => throw new ModbusIOException(true)
      case sockex: SocketException => throw new ModbusIOException(true)
      case ex: Exception => {
        ex.printStackTrace()
        throw new ModbusIOException("I/O exception - failed to read.")
      }
    }
  }

  def readResponse(): ModbusResponse = {
    try {
      var res: ModbusResponse = null
      synchronized {
        val buffer = m_ByteIn.getBuffer
        if (m_Input.read(buffer, 0, 6) == -1) {
          throw new ModbusIOException("Premature end of stream (Header truncated).")
        }
        val bf = ModbusUtil.registerToShort(buffer, 4)
        if (m_Input.read(buffer, 6, bf) == -1) {
          throw new ModbusIOException("Premature end of stream (Message truncated).")
        }
        m_ByteIn.reset(buffer, (6 + bf))
        m_ByteIn.skip(7)
        val functionCode = m_ByteIn.readUnsignedByte()
        m_ByteIn.reset()
        res = ModbusResponse.createModbusResponse(functionCode)
        res.readFrom(m_ByteIn)
      }
      res
    } catch {
      case ex: Exception => {
        ex.printStackTrace()
        throw new ModbusIOException("I/O exception - failed to read.")
      }
    }
  }

  private def prepareStreams(socket: Socket) {
    m_Input = new DataInputStream(new BufferedInputStream(socket.getInputStream))
    m_Output = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream))
    m_ByteIn = new BytesInputStream(Modbus.MAX_IP_MESSAGE_LENGTH)
  }
}
