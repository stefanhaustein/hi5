package org.kobjects.hi5.z80

class Z80Emulator(val code: ByteArray) {

    val ram = ByteArray(65536)
    var bc: UShort = 0u
    var de: UShort = 0u
    var hl: UShort = 0u
    var pc: UShort = 0u
    var sp: UShort = 0u

    fun get16(address: UShort): UShort {
        return ram[address.toInt()].toUShort()
    }

    fun set16(address: UShort, value: UShort) {
        ram[address.toInt()] = (value % 256u).toByte()
        ram[address.toInt() + 1] = (value / 256u).toByte()
    }

    fun pop(): UShort {
        val result = get16(sp)
        sp = (sp + 2u).toUShort()
        return result
    }

    fun push(value: UShort) {
        sp = (sp - 2u).toUShort()
        set16(sp, value)
    }

    fun run() {
        code.copyInto(ram)

        push(0xffff.toUShort())
        while (pc != 0xffffu.toUShort()) {
            val opCode = ram[(pc++).toInt()].toUByte().toInt()
            when (opCode) {
                Op.ADD_HL_DE -> hl = (hl + de).toUShort()
                Op.ADD_HL_SP -> hl = (hl + sp).toUShort()
                Op.CALL -> {
                    push((pc + 2u).toUShort())
                    pc = get16(pc)
                }
                Op.DEC_HL -> hl--
                Op.INC_HL -> hl++
                Op.JP_HL -> pc = hl
                Op.NOP -> {}
                Op.LD_H_B -> {
                    hl = (hl and 0x00ffu) or (bc and 0xff00u)
                }
                Op.LD_L_C -> {
                    hl = (hl and 0xff00u) or (bc and 0x00ffu)
                }
                Op.LD_C__HL -> {
                    bc = ((bc and 0xff00u) or ram[hl.toInt()].toUByte().toUShort())
                }
                Op.LD_B__HL -> {
                    bc = ((bc and 0x00ffu) or (ram[hl.toInt()].toUByte() * 256u).toUShort())
                }
                Op.LD_HL_NN -> {
                    hl = get16(pc)
                    pc = (pc + 2u).toUShort()
                }
                Op.LD__HL_D -> {
                    ram[hl.toInt()] = (de / 256u).toByte()
                }
                Op.LD__HL_E -> {
                    ram[hl.toInt()] = (de % 256u).toByte()
                }
                Op.POP_DE -> de = pop()
                Op.POP_HL -> hl = pop()
                Op.RET -> pc = pop()
                Op.PUSH_HL -> push(hl)
                Op.PUSH_BC -> push(bc)
                Op.PUSH_DE -> push(de)
                else -> throw UnsupportedOperationException("Unrecognized op: $opCode")
            }
        }
    }

}