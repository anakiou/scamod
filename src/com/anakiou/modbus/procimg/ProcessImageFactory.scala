package com.anakiou.modbus.procimg

trait ProcessImageFactory {

  def createProcessImageImplementation(): ProcessImageImplementation

  def createDigitalIn(): DigitalIn

  def createDigitalIn(state: Boolean): DigitalIn

  def createDigitalOut(): DigitalOut

  def createDigitalOut(b: Boolean): DigitalOut

  def createInputRegister(): InputRegister

  def createInputRegister(b1: Byte, b2: Byte): InputRegister

  def createRegister(): Register

  def createRegister(b1: Byte, b2: Byte): Register
}
