package com.anakiou.modbus.io

import java.io.DataInput
import java.io.DataOutput
import java.io.IOException

trait Transportable {

  def getOutputLength(): Int

  def writeTo(dout: DataOutput): Unit

  def readFrom(din: DataInput): Unit
}
