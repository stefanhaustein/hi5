package org.kobjects.hi5

import org.kobjects.hi5.z80.Z80CodeGenerator
import org.kobjects.hi5.z80.Z80Emulator
import org.kobjects.hi5.z80.Z80Platform

object Hi5 {

    fun parse(code: String): Memory {
        val parser = Parser(code, Z80Platform)

        val main = parser.parse()

        val code = Linker.writeCode(0, main)

        return code
    }

    fun run(code: Memory): Int {
        val emulator = Z80Emulator(code)
        emulator.run()
        return emulator.pop().toInt()
    }

    fun run(source: String): Int {
        val parser = Parser(source, Z80Platform)

        val main = parser.parse()

        val code = Linker.writeCode(0, main)

        val emulator = Z80Emulator(code)
        emulator.run()
        return emulator.pop().toInt()
    }


}