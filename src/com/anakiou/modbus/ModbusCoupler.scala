package com.anakiou.modbus

import ModbusCoupler._
import scala.collection.JavaConversions._
import com.anakiou.modbus.procimg.DefaultProcessImageFactory
import com.anakiou.modbus.procimg.ProcessImageFactory
import com.anakiou.modbus.procimg.ProcessImage

object ModbusCoupler {

  private var c_Self: ModbusCoupler = new ModbusCoupler()

  def getReference(): ModbusCoupler = c_Self
}

class ModbusCoupler private () {

  private var m_ProcessImage: ProcessImage = _

  private var m_UnitID: Int = Modbus.DEFAULT_UNIT_ID

  private var m_Master: Boolean = true

  private var m_PIFactory: ProcessImageFactory = new DefaultProcessImageFactory()

  private def this(procimg: ProcessImage) {
    this()
    setProcessImage(procimg)
    c_Self = this
  }

  def getProcessImageFactory(): ProcessImageFactory = m_PIFactory

  def setProcessImageFactory(factory: ProcessImageFactory) {
    m_PIFactory = factory
  }

  def getProcessImage(): ProcessImage = synchronized {
    m_ProcessImage
  }

  def setProcessImage(procimg: ProcessImage) {
    synchronized {
      m_ProcessImage = procimg
    }
  }

  def getUnitID(): Int = m_UnitID

  def setUnitID(id: Int) {
    m_UnitID = id
  }

  def isMaster(): Boolean = m_Master

  def isSlave(): Boolean = !m_Master

  def setMaster(master: Boolean) {
    m_Master = master
  }
}
