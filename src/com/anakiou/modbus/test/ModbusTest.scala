package com.anakiou.modbus.test

import com.anakiou.modbus.msg.WriteCoilRequest
import com.anakiou.modbus.msg.WriteCoilResponse
import com.anakiou.modbus.io.ModbusTCPTransaction
import com.anakiou.modbus.msg.ReadInputDiscretesRequest
import com.anakiou.modbus.msg.ReadInputDiscretesResponse
import com.anakiou.modbus.net.TCPMasterConnection
import scala.util.Random
import java.net.InetAddress

object ModbusTest extends App {
  
  var con = new TCPMasterConnection(InetAddress.getByName("127.0.0.1"))
  var trans: ModbusTCPTransaction = null
  var reqIn: ReadInputDiscretesRequest = null
  var reqOut: WriteCoilRequest = null
  var resIn: ReadInputDiscretesResponse = null
  var resOut: WriteCoilResponse = null;

  con.setPort(502)
  con.connect()

  trans = new ModbusTCPTransaction(con)

  val rand: Random = new Random();

  do {
    reqIn = new ReadInputDiscretesRequest(rand.nextInt(32000), 32)
    reqOut = new WriteCoilRequest(rand.nextInt(32000), false)
    trans.setRequest(reqIn)
    trans.execute()
    resIn = trans.getResponse().asInstanceOf[ReadInputDiscretesResponse]
    trans.setRequest(reqOut)
    trans.execute()
    resOut = trans.getResponse().asInstanceOf[WriteCoilResponse]
    
    println("Digital Inputs Status=" + resIn.getDiscretes.toString)
    println("Coils=") + resOut.getHexMessage
    Thread.sleep(1000)

  } while (true)

}