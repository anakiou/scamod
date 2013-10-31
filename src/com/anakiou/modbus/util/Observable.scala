package com.anakiou.modbus.util

import java.util.Vector

class Observable {

  var m_Observers: Vector[Observer] = new Vector[Observer](10)

  def getObserverCount(): Int = {
    synchronized {
      m_Observers.size
    }
  }

  def addObserver(o: Observer) {
    synchronized {
      if (!m_Observers.contains(o)) {
        m_Observers.addElement(o)
      }
    }
  }

  def removeObserver(o: Observer) {
    synchronized {
      m_Observers.removeElement(o)
    }
  }

  def removeObservers() {
    synchronized {
      m_Observers.removeAllElements()
    }
  }

  def notifyObservers(arg: AnyRef) {
    synchronized {
      for (i <- 0 until m_Observers.size) {
        m_Observers.elementAt(i).asInstanceOf[Observer].update(this, arg)
      }
    }
  }
}
