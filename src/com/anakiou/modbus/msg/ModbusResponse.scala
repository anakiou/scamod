package com.anakiou.modbus.msg

import java.io.ByteArrayInputStream
import java.io.DataInputStream
import java.io.IOException
import com.anakiou.modbus.Modbus
import ModbusResponse._

object ModbusResponse {

  def createModbusResponse(functionCode: Int): ModbusResponse = {
    var response: ModbusResponse = null
    functionCode match {
      case Modbus.READ_MULTIPLE_REGISTERS => response = new ReadMultipleRegistersResponse()
      case Modbus.READ_INPUT_DISCRETES => response = new ReadInputDiscretesResponse()
      case Modbus.READ_INPUT_REGISTERS => response = new ReadInputRegistersResponse()
      case Modbus.READ_COILS => response = new ReadCoilsResponse()
      case Modbus.WRITE_MULTIPLE_REGISTERS => response = new WriteMultipleRegistersResponse()
      case Modbus.WRITE_SINGLE_REGISTER => response = new WriteSingleRegisterResponse()
      case Modbus.WRITE_COIL => response = new WriteCoilResponse()
      case Modbus.WRITE_MULTIPLE_COILS => response = new WriteMultipleCoilsResponse()
      case _ => response = new ExceptionResponse()
    }
    response
  }
}

abstract class ModbusResponse extends ModbusMessageImpl {

  protected def setMessage(msg: Array[Byte]) {
    try {
      readData(new DataInputStream(new ByteArrayInputStream(msg)))
    } catch {
      case ex: IOException => 
    }
  }
}
