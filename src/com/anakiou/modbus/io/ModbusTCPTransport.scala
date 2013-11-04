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

  private var input: DataInputStream = _

  private var output: DataOutputStream = _

  private var byteIn: BytesInputStream = _

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
    input.close()
    output.close()
  }

  def writeMessage(msg: ModbusMessage) {
    msg.writeTo(output.asInstanceOf[DataOutput])
    output.flush()
  }

  def readRequest(): ModbusRequest = {
    try {
      var req: ModbusRequest = null
      synchronized {
        val buffer = byteIn.getBuffer
        if (input.read(buffer, 0, 6) == -1) {
          throw new EOFException("Premature end of stream (Header truncated).")
        }
        val bf = ModbusUtil.registerToShort(buffer, 4)
        if (input.read(buffer, 6, bf) == -1) {
          throw new ModbusIOException("Premature end of stream (Message truncated).")
        }
        byteIn.reset(buffer, (6 + bf))
        byteIn.skip(7)
        val functionCode = byteIn.readUnsignedByte()
        byteIn.reset()
        req = ModbusRequest.createModbusRequest(functionCode)
        req.readFrom(byteIn)
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
        val buffer = byteIn.getBuffer
        if (input.read(buffer, 0, 6) == -1) {
          throw new ModbusIOException("Premature end of stream (Header truncated).")
        }
        val bf = ModbusUtil.registerToShort(buffer, 4)
        if (input.read(buffer, 6, bf) == -1) {
          throw new ModbusIOException("Premature end of stream (Message truncated).")
        }
        byteIn.reset(buffer, (6 + bf))
        byteIn.skip(7)
        val functionCode = byteIn.readUnsignedByte()
        byteIn.reset()
        res = ModbusResponse.createModbusResponse(functionCode)
        res.readFrom(byteIn)
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
    input = new DataInputStream(new BufferedInputStream(socket.getInputStream))
    output = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream))
    byteIn = new BytesInputStream(Modbus.MAX_IP_MESSAGE_LENGTH)
  }
}
