package com.anakiou.modbus.procimg

import java.util.Vector

class SimpleProcessImage extends ProcessImageImplementation {

  protected var m_DigitalInputs: Vector[DigitalIn] = new Vector()

  protected var m_DigitalOutputs: Vector[DigitalOut] = new Vector()

  protected var m_InputRegisters: Vector[InputRegister] = new Vector()

  protected var m_Registers: Vector[Register] = new Vector()

  protected var m_Locked: Boolean = false

  def isLocked(): Boolean = m_Locked

  def setLocked(locked: Boolean) {
    m_Locked = locked
  }

  def addDigitalIn(di: DigitalIn) {
    if (!isLocked) {
      m_DigitalInputs.addElement(di)
    }
  }

  def removeDigitalIn(di: DigitalIn) {
    if (!isLocked) {
      m_DigitalInputs.removeElement(di)
    }
  }

  def setDigitalIn(ref: Int, di: DigitalIn) {
    if (!isLocked) {
      m_DigitalInputs.setElementAt(di, ref)
    }
  }

  def getDigitalIn(ref: Int): DigitalIn = {
    m_DigitalInputs.elementAt(ref).asInstanceOf[DigitalIn]
  }

  def getDigitalInCount(): Int = m_DigitalInputs.size

  def getDigitalInRange(ref: Int, count: Int): Array[DigitalIn] = {
    if (ref < 0 || ref + count > m_DigitalInputs.size) {
      throw new IllegalAddressException("")
    } else {
      val dins = Array.ofDim[DigitalIn](count)
      for (i <- 0 until dins.length) {
        dins(i) = getDigitalIn(ref + i)
      }
      dins
    }
  }

  def addDigitalOut(dout: DigitalOut) {
    if (!isLocked) {
      m_DigitalOutputs.addElement(dout)
    }
  }

  def removeDigitalOut(dout: DigitalOut) {
    if (!isLocked) {
      m_DigitalOutputs.removeElement(dout)
    }
  }

  def setDigitalOut(ref: Int, dout: DigitalOut) {
    if (!isLocked) {
      m_DigitalOutputs.setElementAt(dout, ref)
    }
  }

  def getDigitalOut(ref: Int): DigitalOut = {
    m_DigitalOutputs.elementAt(ref).asInstanceOf[DigitalOut]
  }

  def getDigitalOutCount(): Int = m_DigitalOutputs.size

  def getDigitalOutRange(ref: Int, count: Int): Array[DigitalOut] = {
    if (ref < 0 || ref + count > m_DigitalOutputs.size) {
      throw new IllegalAddressException("")
    } else {
      val douts = Array.ofDim[DigitalOut](count)
      for (i <- 0 until douts.length) {
        douts(i) = getDigitalOut(ref + i)
      }
      douts
    }
  }

  def addInputRegister(reg: InputRegister) {
    if (!isLocked) {
      m_InputRegisters.addElement(reg)
    }
  }

  def removeInputRegister(reg: InputRegister) {
    if (!isLocked) {
      m_InputRegisters.removeElement(reg)
    }
  }

  def setInputRegister(ref: Int, reg: InputRegister) {
    if (!isLocked) {
      m_InputRegisters.setElementAt(reg, ref)
    }
  }

  def getInputRegister(ref: Int): InputRegister = {
    m_InputRegisters.elementAt(ref).asInstanceOf[InputRegister]
  }

  def getInputRegisterCount(): Int = m_InputRegisters.size

  def getInputRegisterRange(ref: Int, count: Int): Array[InputRegister] = {
    if (ref < 0 || ref + count > m_InputRegisters.size) {
      throw new IllegalAddressException("")
    } else {
      val iregs = Array.ofDim[InputRegister](count)
      for (i <- 0 until iregs.length) {
        iregs(i) = getInputRegister(ref + i)
      }
      iregs
    }
  }

  def addRegister(reg: Register) {
    if (!isLocked) {
      m_Registers.addElement(reg)
    }
  }

  def removeRegister(reg: Register) {
    if (!isLocked) {
      m_Registers.removeElement(reg)
    }
  }

  def setRegister(ref: Int, reg: Register) {
    if (!isLocked) {
      m_Registers.setElementAt(reg, ref)
    }
  }

  def getRegister(ref: Int): Register = {
    m_Registers.elementAt(ref).asInstanceOf[Register]
  }

  def getRegisterCount(): Int = m_Registers.size

  def getRegisterRange(ref: Int, count: Int): Array[Register] = {
    if (ref < 0 || ref + count > m_Registers.size) {
      throw new IllegalAddressException("")
    } else {
      val iregs = Array.ofDim[Register](count)
      for (i <- 0 until iregs.length) {
        iregs(i) = getRegister(ref + i)
      }
      iregs
    }
  }
}
