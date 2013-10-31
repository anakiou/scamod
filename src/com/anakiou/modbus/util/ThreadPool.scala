package com.anakiou.modbus.util

class ThreadPool(private var m_Size: Int) {

  private var m_TaskPool: LinkedQueue = new LinkedQueue()

  initPool()

  def execute(task: Runnable) {
    synchronized {
      try {
        m_TaskPool.put(task)
      } catch {
        case ex: InterruptedException =>
      }
    }
  }

  protected def initPool() {
    var i = m_Size
    while (i >= 0) {
      new PoolThread().start()
    }
  }

  private class PoolThread extends Thread {

    override def run() {
      do {
        try {
          m_TaskPool.take().asInstanceOf[Runnable].run()
        } catch {
          case ex: Exception => ex.printStackTrace()
        }
      } while (true);
    }
  }
}
