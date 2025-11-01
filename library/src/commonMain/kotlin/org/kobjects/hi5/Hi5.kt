package org.kobjects.hi5

import org.kobjects.hi5.z80.Z80CodeGenerator
import org.kobjects.hi5.z80.Z80Emulator
import org.kobjects.hi5.z80.Z80Platform

object Hi5 {

    fun parse(code: String): ByteArray {
        val parser = Parser(code, Z80Platform)

        val main = parser.parse()

        val code = Linker.writeCode(0, main)

        return code.data.copyOf(code.data.size)
    }

    fun run(code: ByteArray): Int {
        val emulator = Z80Emulator(code)
        emulator.run()
        return emulator.pop().toInt()
    }


}