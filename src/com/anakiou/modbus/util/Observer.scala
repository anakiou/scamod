package com.anakiou.modbus.util

trait Observer {

  def update(o: Observable, arg: AnyRef): Unit
}
