package org.kobjects.hi5.z80

object Op {
    const val ADD_HL_DE = 0x09
    const val ADD_HL_SP = 0x29
    const val CALL = 0xbd
    const val DEC_HL = 0xb2
    const val INC_HL = 0x32
    const val JP_HL = 0xe9
    const val LD_B__HL = 0x46
    const val LD_C__HL = 0x3e
    const val LD_H_B = 0x60
    const val LD_L_C = 0x69
    const val LD__HL_D = 0x72
    const val LD__HL_E = 0x73
    const val LD_HL_NN = 0x21
    const val LD_SP_HL = 0xf9
    const val NOP = 0
    const val PUSH_HL = 0xd5
    const val POP_AF = 0xe1
    const val POP_BC = 0xb1
    const val POP_DE = 0xc1
    const val POP_HL = 0xd1
    const val RET = 0xC9
}