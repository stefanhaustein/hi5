package org.kobjects.hi5.z80

import org.kobjects.hi5.Memory

class Z80Emulator(code: Memory) {

    val ram = Memory(65536)
    var a: Int = 0
    var bc: Int = 0
    var de: Int = 0
    var hl: Int = 0
    var pc: Int = 0
    var sp: Int = 0

    var carryFlag: Boolean = false
    var zeroFlag: Boolean = false

    val addressMap = code.resolvedKeys.entries.associateBy({ it.value }, { it.key })

    init {
        code.data.copyInto(ram.data, 0, 0, code.size)
    }

    fun add16F(a: Int, b: Int): Int {
        val result = a + b
        carryFlag = (result and 0x10000) != 0
        zeroFlag = (result and 0xFFFF) == 0
        return result and 0xffff
    }

    fun pop(): Int {
        val result = ram.getShort(sp)
        sp = add16(sp, 2)
        return result
    }

    fun push(value: Int) {
        sp = sub16(sp, 2)
        ram.setShort(sp, value)
    }

    fun or(value: Int) {
        a = (a or value) and 0xff
        zeroFlag = a == 0
        carryFlag = false
    }

    fun sbc16(a: Int, b: Int): Int {
        val result = a - b - if (carryFlag) 1 else 0
        carryFlag = (result and 0x10000) != 0
        zeroFlag = (result and 0xFFFF) == 0
        return result and 0xffff
    }

    fun run() {
        push(0xffff)

        while (pc != 0xffff) {
            val resolved = addressMap[pc]
            if (resolved != null) {
                println("$resolved:")
            }
            print("  PC:${pc.toUShort().toHexString()} ")

            var code = ram.getByte(pc)
            pc = add16(pc, 1)
            if (code == 0xed) {
                code = code * 256 + ram.getByte(pc)
                pc = add16(pc, 1)
            }
            val opCode = Op.values().find { it.opcode == code }
            require (opCode != null) { "Unknown opcode: ${code.toHexString()}" }

            print("${opCode.toString().padEnd(20)} A:${a.toUByte().toHexString()} BC:${bc.toUShort().toHexString()} DE:${de.toUShort().toHexString()} HL:${hl.toUShort().toHexString()} SP:${sp.toUShort().toHexString()} ")
            var spi = 0
            while (spi != sp) {
                spi = add16(spi, -2)
                val value = ram.getShort(spi)
                print(value.toUShort().toHexString())
                print(' ')
            }
            println()


            when (opCode) {
                Op.ADD_HL_BC -> hl = add16F(hl, bc)
                Op.ADD_HL_DE -> hl = add16F(hl, de)
                Op.ADD_HL_HL -> hl = add16F(hl, hl)
                Op.ADD_HL_SP -> hl = add16F(hl, sp)
                Op.CALL -> {
                    push(add16(pc,2))
                    pc = ram.getShort(pc)
                    println()
                }
                Op.DEC_HL -> hl = sub16(hl, 1)
                Op.EX_DE_HL -> {
                    val tmp = hl
                    hl = de
                    de = tmp
                }
                Op.INC_BC -> bc = add16(bc, 1)
                Op.INC_DE -> de = add16(de, 1)
                Op.INC_HL -> hl = add16(hl, 1)
                Op.INC_SP -> sp = add16(sp, 1)
                Op.JP -> {
                    pc = ram.getShort(pc)
                    println()
                }
                Op.JP_HL -> {
                    pc = hl
                    println()
                }
                Op.JP_Z -> {
                    if (zeroFlag) {
                        pc = ram.getShort(pc)
                        println()
                    } else {
                        pc = add16(pc, 2)
                    }
                }
                Op.JR_Z -> {
                    if (zeroFlag) {
                        pc = add16(pc, ram.getByte(pc).toByte() - 1)
                        println()
                    } else {
                        pc = add16(pc, 1)
                    }
                }
                Op.NOP -> {}
                Op.LD_A_H -> a = (hl shr 8) and 0xff
                Op.LD_H_B -> hl = (hl and 0x00ff) or (bc and 0xff00)
                Op.LD_L_C -> hl = (hl and 0xff00) or (bc and 0x00ff)
                Op.LD_C__HL -> bc = (bc and 0xff00) or ram.getByte(hl)
                Op.LD_B__HL -> bc = (bc and 0x00ff) or (ram.getByte(hl) shl 8)
                Op.LD_DE_NN -> {
                    de = ram.getShort(pc)
                    pc = add16(pc, 2)
                }
                Op.LD_HL_NN -> {
                    hl = ram.getShort(pc)
                    pc = add16(pc, 2)
                }
                Op.LD_SP_HL -> sp = hl
                Op.LD__HL_D -> ram.setByte(hl, de ushr 8)
                Op.LD__HL_E -> ram.setByte(hl, de and 0xff)
                Op.LDD -> {
                    ram.setByte(de, ram.getByte(hl))
                    de = sub16(de, 1)
                    hl = sub16(hl, 1)
                    bc = sub16(bc, 1)
                }
                Op.OR_A -> or(a)
                Op.OR_L -> or(hl and 0xff)
                Op.POP_BC -> bc = pop()
                Op.POP_DE -> de = pop()
                Op.POP_HL -> hl = pop()
                Op.RET -> pc = pop()
                Op.PUSH_HL -> push(hl)
                Op.PUSH_BC -> push(bc)
                Op.PUSH_DE -> push(de)
                Op.SBC_HL_BC -> hl = sbc16(hl, bc)
                Op.SBC_HL_DE -> hl = sbc16(hl, de)
                Op.SBC_HL_HL -> hl = sbc16(hl, hl)
                Op.SBC_HL_SP -> hl = sbc16(hl, sp)
                else -> throw UnsupportedOperationException("Unrecognized op: $opCode")
            }
        }
    }

    companion object {
        fun add16(a: Int, b: Int) = (a + b) and 0xffff

        fun sub16(a: Int, b: Int) = (a - b) and 0xffff
    }

}