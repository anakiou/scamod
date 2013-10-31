package com.anakiou.modbus.procimg

class DefaultProcessImageFactory extends ProcessImageFactory {

  def createProcessImageImplementation(): ProcessImageImplementation = new SimpleProcessImage()

  def createDigitalIn(): DigitalIn = new SimpleDigitalIn()

  def createDigitalIn(state: Boolean): DigitalIn = new SimpleDigitalIn(state)

  def createDigitalOut(): DigitalOut = new SimpleDigitalOut()

  def createDigitalOut(b: Boolean): DigitalOut = new SimpleDigitalOut(b)

  def createInputRegister(): InputRegister = new SimpleInputRegister()

  def createInputRegister(b1: Byte, b2: Byte): InputRegister = new SimpleInputRegister(b1, b2)

  def createRegister(): Register = new SimpleRegister()

  def createRegister(b1: Byte, b2: Byte): Register = new SimpleRegister(b1, b2)
}
