package com.anakiou.modbus.facade

import java.net.InetAddress

import com.anakiou.modbus.io.ModbusUDPTransaction
import com.anakiou.modbus.msg.ReadCoilsRequest
import com.anakiou.modbus.msg.ReadCoilsResponse
import com.anakiou.modbus.msg.ReadInputDiscretesRequest
import com.anakiou.modbus.msg.ReadInputDiscretesResponse
import com.anakiou.modbus.msg.ReadInputRegistersRequest
import com.anakiou.modbus.msg.ReadInputRegistersResponse
import com.anakiou.modbus.msg.ReadMultipleRegistersRequest
import com.anakiou.modbus.msg.ReadMultipleRegistersResponse
import com.anakiou.modbus.msg.WriteCoilRequest
import com.anakiou.modbus.msg.WriteCoilResponse
import com.anakiou.modbus.msg.WriteMultipleCoilsRequest
import com.anakiou.modbus.msg.WriteMultipleRegistersRequest
import com.anakiou.modbus.msg.WriteSingleRegisterRequest
import com.anakiou.modbus.net.UDPMasterConnection
import com.anakiou.modbus.procimg.InputRegister
import com.anakiou.modbus.procimg.Register
import com.anakiou.modbus.util.BitVector

class ModbusUDPMaster(addr: String) {

  private var m_Connection: UDPMasterConnection = _

  private var m_SlaveAddress: InetAddress = _

  private var m_Transaction: ModbusUDPTransaction = _

  private var m_ReadCoilsRequest: ReadCoilsRequest = _

  private var m_ReadInputDiscretesRequest: ReadInputDiscretesRequest = _

  private var m_WriteCoilRequest: WriteCoilRequest = _

  private var m_WriteMultipleCoilsRequest: WriteMultipleCoilsRequest = _

  private var m_ReadInputRegistersRequest: ReadInputRegistersRequest = _

  private var m_ReadMultipleRegistersRequest: ReadMultipleRegistersRequest = _

  private var m_WriteSingleRegisterRequest: WriteSingleRegisterRequest = _

  private var m_WriteMultipleRegistersRequest: WriteMultipleRegistersRequest = _

  {
    m_SlaveAddress = InetAddress.getByName(addr)
    m_Connection = new UDPMasterConnection(m_SlaveAddress)
    m_ReadCoilsRequest = new ReadCoilsRequest()
    m_ReadInputDiscretesRequest = new ReadInputDiscretesRequest()
    m_WriteCoilRequest = new WriteCoilRequest()
    m_WriteMultipleCoilsRequest = new WriteMultipleCoilsRequest()
    m_ReadInputRegistersRequest = new ReadInputRegistersRequest()
    m_ReadMultipleRegistersRequest = new ReadMultipleRegistersRequest()
    m_WriteSingleRegisterRequest = new WriteSingleRegisterRequest()
    m_WriteMultipleRegistersRequest = new WriteMultipleRegistersRequest()
  }

  def this(addr: String, port: Int) {
    this(addr)
    m_Connection.setPort(port)
  }

  def connect() {
    if (m_Connection != null && !m_Connection.isConnected) {
      m_Connection.connect()
      m_Transaction = new ModbusUDPTransaction(m_Connection)
    }
  }

  def disconnect() {
    if (m_Connection != null && m_Connection.isConnected) {
      m_Connection.close()
      m_Transaction = null
    }
  }

  def readCoils(ref: Int, count: Int): BitVector = {
    synchronized {
      m_ReadCoilsRequest.setReference(ref)
      m_ReadCoilsRequest.setBitCount(count)
      m_Transaction.setRequest(m_ReadCoilsRequest)
      m_Transaction.execute()
      val bv = m_Transaction.getResponse.asInstanceOf[ReadCoilsResponse]
        .getCoils
      bv.forceSize(count)
      bv
    }
  }

  def writeCoil(unitid: Int, ref: Int, state: Boolean): Boolean = {
    synchronized {
      m_WriteCoilRequest.setUnitID(unitid)
      m_WriteCoilRequest.setReference(ref)
      m_WriteCoilRequest.setCoil(state)
      m_Transaction.setRequest(m_WriteCoilRequest)
      m_Transaction.execute()
      m_Transaction.getResponse.asInstanceOf[WriteCoilResponse]
        .getCoil
    }
  }

  def writeMultipleCoils(ref: Int, coils: BitVector) {
    synchronized {
      m_WriteMultipleCoilsRequest.setReference(ref)
      m_WriteMultipleCoilsRequest.setCoils(coils)
      m_Transaction.setRequest(m_WriteMultipleCoilsRequest)
      m_Transaction.execute()
    }
  }

  def readInputDiscretes(ref: Int, count: Int): BitVector = {
    synchronized {
      m_ReadInputDiscretesRequest.setReference(ref)
      m_ReadInputDiscretesRequest.setBitCount(count)
      m_Transaction.setRequest(m_ReadInputDiscretesRequest)
      m_Transaction.execute()
      val bv = m_Transaction.getResponse.asInstanceOf[ReadInputDiscretesResponse]
        .getDiscretes
      bv.forceSize(count)
      bv
    }
  }

  def readInputRegisters(ref: Int, count: Int): Array[InputRegister] = {
    synchronized {
      m_ReadInputRegistersRequest.setReference(ref)
      m_ReadInputRegistersRequest.setWordCount(count)
      m_Transaction.setRequest(m_ReadInputRegistersRequest)
      m_Transaction.execute()
      m_Transaction.getResponse.asInstanceOf[ReadInputRegistersResponse]
        .getRegisters
    }
  }

  def readMultipleRegisters(ref: Int, count: Int): Array[Register] = {
    synchronized {
      m_ReadMultipleRegistersRequest.setReference(ref)
      m_ReadMultipleRegistersRequest.setWordCount(count)
      m_Transaction.setRequest(m_ReadMultipleRegistersRequest)
      m_Transaction.execute()
      m_Transaction.getResponse.asInstanceOf[ReadMultipleRegistersResponse]
        .getRegisters
    }
  }

  def writeSingleRegister(ref: Int, register: Register) {
    synchronized {
      m_WriteSingleRegisterRequest.setReference(ref)
      m_WriteSingleRegisterRequest.setRegister(register)
      m_Transaction.setRequest(m_WriteSingleRegisterRequest)
      m_Transaction.execute()
    }
  }

  def writeMultipleRegisters(ref: Int, registers: Array[Register]) {
    synchronized {
      m_WriteMultipleRegistersRequest.setReference(ref)
      m_WriteMultipleRegistersRequest.setRegisters(registers)
      m_Transaction.setRequest(m_WriteMultipleRegistersRequest)
      m_Transaction.execute()
    }
  }
}
