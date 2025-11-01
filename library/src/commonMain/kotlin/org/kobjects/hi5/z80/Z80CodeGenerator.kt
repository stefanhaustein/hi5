package org.kobjects.hi5.z80

import org.kobjects.hi5.CodeBuilder
import org.kobjects.hi5.CodeGenerator
import org.kobjects.hi5.Function

class Z80CodeGenerator(
    override val builder: CodeBuilder,
) : CodeGenerator {

    override fun literalS16(value: Int) {
        builder.appendOp(Op.LD_HL_NN)
        builder.appendWord(value)
        builder.appendOp(Op.PUSH_HL)
    }

    override fun addS16() {
        builder.appendOp(Op.POP_HL)
        builder.appendOp(Op.POP_DE)
        builder.appendOp(Op.ADD_HL_DE)
        builder.appendOp(Op.PUSH_HL)
    }

    override fun call(other: Function) {
        builder.appendByte(Op.CALL)
        builder.markReference(other)
        builder.appendWord(0)
    }

    override fun ret() {
        builder.appendOp(Op.RET)
    }

    override fun openStackFrame(parameterCount: Int, localCount: Int, returnValueCount: Int) {
        if (localCount != 0) {
            builder.appendOp(Op.LD_HL_NN)
            builder.appendWord(-localCount * 2)
            builder.appendOp(Op.ADD_HL_SP)
            builder.appendOp(Op.LD_SP_HL)
        }
    }

    override fun closeStackFrame(parameterCount: Int, localCount: Int, returnValueCount: Int) {
        // load BC from ret address
        builder.appendOp(Op.LD_HL_NN)
        // Skip return values and locals on the stack to get to the return address
        builder.appendWord((returnValueCount + localCount) * 2)
        builder.appendOp(Op.ADD_HL_SP)
        builder.appendOp(Op.LD_C__HL)
        builder.appendOp(Op.INC_HL)
        builder.appendOp(Op.LD_B__HL)

        // Move the return value(s) to the right address
        builder.appendOp(Op.LD_HL_NN)
        builder.appendWord((parameterCount + localCount + 1) * 2)
        builder.appendOp(Op.ADD_HL_SP)

        for (i in 0 until returnValueCount) {
            builder.appendOp(Op.POP_DE)
            builder.appendOp(Op.LD__HL_E)
            builder.appendWord(Op.INC_HL)
            builder.appendOp(Op.LD__HL_D)
            builder.appendWord(Op.INC_HL)
        }

        builder.appendOp(Op.LD_H_B)
        builder.appendOp(Op.LD_L_C)
        builder.appendOp(Op.JP_HL)

    }
}