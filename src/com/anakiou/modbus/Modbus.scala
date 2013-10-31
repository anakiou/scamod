package com.anakiou.modbus

object Modbus {

  val debug = "true" == System.getProperty("com.anakiou.modbus.debug")

  val READ_MULTIPLE_REGISTERS = 3

  val WRITE_MULTIPLE_REGISTERS = 16

  val READ_COILS = 1

  val READ_INPUT_DISCRETES = 2

  val READ_INPUT_REGISTERS = 4

  val WRITE_COIL = 5

  val WRITE_MULTIPLE_COILS = 15

  val WRITE_SINGLE_REGISTER = 6

  val COIL_ON = 255.toByte

  val COIL_OFF = 0

  val COIL_ON_BYTES = Array(COIL_ON.toByte, COIL_OFF.toByte)

  val COIL_OFF_BYTES = Array(COIL_OFF.toByte, COIL_OFF.toByte)

  val MAX_BITS = 2000

  val EXCEPTION_OFFSET = 128

  val ILLEGAL_FUNCTION_EXCEPTION = 1

  val ILLEGAL_ADDRESS_EXCEPTION = 2

  val ILLEGAL_VALUE_EXCEPTION = 3

  val DEFAULT_PORT = 502

  val MAX_MESSAGE_LENGTH = 256

  val MAX_IP_MESSAGE_LENGTH = 260

  val DEFAULT_TRANSACTION_ID = 0

  val DEFAULT_PROTOCOL_ID = 0

  val DEFAULT_UNIT_ID = 0

  val DEFAULT_VALIDITYCHECK = true

  val DEFAULT_TIMEOUT = 3000

  val DEFAULT_RECONNECTING = false

  val DEFAULT_RETRIES = 3

  val DEFAULT_TRANSMIT_DELAY = 0

  val MAX_TRANSACTION_ID = (java.lang.Short.MAX_VALUE * 2) - 1

  val SERIAL_ENCODING_ASCII = "ascii"

  val SERIAL_ENCODING_RTU = "rtu"

  val SERIAL_ENCODING_BIN = "bin"

  val DEFAULT_SERIAL_ENCODING = SERIAL_ENCODING_ASCII
}
