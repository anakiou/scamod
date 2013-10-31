package com.anakiou.modbus.io

import java.io.DataInputStream
import java.io.EOFException
import java.io.IOException
import java.io.DataInput

trait NonWordDataHandler {

  def getData(): Array[Byte]

  def readData(in: DataInput, reference: Int, count: Int): Unit

  def getWordCount(): Int

  def commitUpdate(): Int

  def prepareData(reference: Int, count: Int): Unit
}
