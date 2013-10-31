package com.anakiou.modbus.util

class Mutex {

  protected var inuse: Boolean = false

  def acquire() {
    if (Thread.interrupted()) throw new InterruptedException()
    synchronized {
      try {
        while (inuse) wait()
        inuse = true
      } catch {
        case ex: InterruptedException => {
          notify()
          throw ex
        }
      }
    }
  }

  def release() {
    synchronized {
      inuse = false
      notify()
    }
  }

  def attempt(msecs: Long): Boolean = {
    if (Thread.interrupted()) throw new InterruptedException()
    synchronized {
      if (!inuse) {
        inuse = true
        true
      } else if (msecs <= 0) false else {
        var waitTime = msecs
        val start = System.currentTimeMillis()
        try {
         // while (true) {
            wait(waitTime)
            if (!inuse) {
              inuse = true
              true
            } else {
              waitTime = msecs - (System.currentTimeMillis() - start)
              if (waitTime <= 0) false
              true
            }
         // }
        } catch {
          case ex: InterruptedException => {
            notify()
            throw ex
          }
        }
      }
    }
  }
}
