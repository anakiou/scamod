package com.anakiou.modbus.msg

import com.anakiou.modbus.Modbus
import ModbusRequest._

object ModbusRequest {

  def createModbusRequest(functionCode: Int): ModbusRequest = {
    var request: ModbusRequest = null
    functionCode match {
      case Modbus.READ_MULTIPLE_REGISTERS => request = new ReadMultipleRegistersRequest()
      case Modbus.READ_INPUT_DISCRETES => request = new ReadInputDiscretesRequest()
      case Modbus.READ_INPUT_REGISTERS => request = new ReadInputRegistersRequest()
      case Modbus.READ_COILS => request = new ReadCoilsRequest()
      case Modbus.WRITE_MULTIPLE_REGISTERS => request = new WriteMultipleRegistersRequest()
      case Modbus.WRITE_SINGLE_REGISTER => request = new WriteSingleRegisterRequest()
      case Modbus.WRITE_COIL => request = new WriteCoilRequest()
      case Modbus.WRITE_MULTIPLE_COILS => request = new WriteMultipleCoilsRequest()
      case _ => request = new IllegalFunctionRequest(functionCode)
    }
    request
  }
}

abstract class ModbusRequest extends ModbusMessageImpl {

  def createResponse(): ModbusResponse

  def createExceptionResponse(EXCEPTION_CODE: Int): ModbusResponse = {
    val response = new ExceptionResponse(this.getFunctionCode, EXCEPTION_CODE)
    if (!isHeadless) {
      response.setTransactionID(this.getTransactionID)
      response.setProtocolID(this.getProtocolID)
      response.setUnitID(this.getUnitID)
    } else {
      response.setHeadless()
    }
    response
  }
}
