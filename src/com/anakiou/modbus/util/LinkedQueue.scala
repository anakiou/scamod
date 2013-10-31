package com.anakiou.modbus.util

class LinkedQueue {

  protected var m_Head: LinkedNode = new LinkedNode(null)

  protected val m_PutLock = new AnyRef()

  protected var m_Tail: LinkedNode = _

  protected var m_WaitingForTake: Int = 0

  protected def insert(x: AnyRef) {
    synchronized {
      val p = new LinkedNode(x)
      synchronized {
        m_Tail.m_NextNode = p
        m_Tail = p
      }
      if (m_WaitingForTake > 0) m_PutLock.notify()
    }
  }

  protected def extract(): AnyRef = {
    synchronized {
      synchronized {
        var x: AnyRef = null
        val first = m_Head.m_NextNode
        if (first != null) {
          x = first.m_Node
          first.m_Node = null
          m_Head = first
        }
        x
      }
    }
  }

  def put(x: AnyRef) {
    if (x == null) throw new IllegalArgumentException()
    insert(x)
  }

  def offer(x: AnyRef, msecs: Long): Boolean = {
    if (x == null) {
      throw new IllegalArgumentException()
    }
    if (Thread.interrupted()) {
      throw new InterruptedException()
    }
    insert(x)
    true
  }

  def take(): AnyRef = {
    var x = extract()
    if (x != null) x else {
      synchronized {
        try {
          m_WaitingForTake
         // while (true) {
            x = extract()
            if (x != null) {
              m_WaitingForTake
              x
            } else {
              //m_PutLock.wait()
              x //added to eliminate errorAK
            }
        //  }
        } catch {
          case ex: InterruptedException => {
            m_WaitingForTake
            m_PutLock.notify()
            throw ex
          }
        }
      }
    }
  }

  def peek(): AnyRef = {
    synchronized {
      val first = m_Head.m_NextNode
      if (first != null) {
        first.m_Node
      } else {
        null
      }
    }
  }

  def isEmpty(): Boolean = {
    synchronized {
      m_Head.m_NextNode == null
    }
  }

  def poll(msecs: Long): AnyRef = {
    var x = extract()
    if (x != null) {
      x
    } else {
      synchronized {
        try {
          var waitTime = msecs
          val start = if ((msecs <= 0)) 0 else System.currentTimeMillis()
          m_WaitingForTake
         // while (true) {
            x = extract()
            if (x != null || waitTime <= 0) {
              m_WaitingForTake
              x
            } else {
              m_PutLock.wait(waitTime)
              waitTime = msecs - (System.currentTimeMillis() - start)
              x // added
            }
         // }
        } catch {
          case ex: InterruptedException => {
            m_WaitingForTake
            m_PutLock.notify()
            throw ex
          }
        }
      }
    }
  }
}
