package com.anakiou.modbus.procimg

trait ProcessImage {

  def getDigitalOutRange(offset: Int, count: Int): Array[DigitalOut]

  def getDigitalOut(ref: Int): DigitalOut

  def getDigitalOutCount(): Int

  def getDigitalInRange(offset: Int, count: Int): Array[DigitalIn]

  def getDigitalIn(ref: Int): DigitalIn

  def getDigitalInCount(): Int

  def getInputRegisterRange(offset: Int, count: Int): Array[InputRegister]

  def getInputRegister(ref: Int): InputRegister

  def getInputRegisterCount(): Int

  def getRegisterRange(offset: Int, count: Int): Array[Register]

  def getRegister(ref: Int): Register

  def getRegisterCount(): Int
}
