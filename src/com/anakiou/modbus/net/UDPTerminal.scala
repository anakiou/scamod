package com.anakiou.modbus.net

import java.net.InetAddress

import com.anakiou.modbus.io.ModbusUDPTransport

trait UDPTerminal {

  def getLocalAddress(): InetAddress

  def getLocalPort(): Int

  def isActive(): Boolean

  def activate(): Unit

  def deactivate(): Unit

  def getModbusTransport(): ModbusUDPTransport

  def sendMessage(msg: Array[Byte]): Unit

  def receiveMessage(): Array[Byte]
}
