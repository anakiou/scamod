package com.anakiou.modbus.util

class LinkedNode(var m_Node: AnyRef) {

   var m_NextNode: LinkedNode = null

  def this(node: AnyRef, linkednode: LinkedNode) {
    this(node)
    m_Node = node
    m_NextNode = linkednode
  }
}
