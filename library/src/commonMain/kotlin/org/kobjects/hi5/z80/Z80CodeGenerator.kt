package org.kobjects.hi5.z80

import org.kobjects.hi5.Memory
import org.kobjects.hi5.CodeGenerator
import org.kobjects.hi5.Function
import org.kobjects.hi5.Variable

class Z80CodeGenerator(
    override val builder: Memory,
) : CodeGenerator {

    var baseOffset = 0

    override fun literalS16(value: Int) {
        builder.appendOp(Op.LD_HL_NN)
        builder.appendShort(value)
        builder.appendOp(Op.PUSH_HL)
    }

    override fun addS16() {
        builder.appendOp(Op.POP_HL)
        builder.appendOp(Op.POP_DE)
        builder.appendOp(Op.ADD_HL_DE)
        builder.appendOp(Op.PUSH_HL)
    }

    override fun subS16() {
        builder.appendOp(Op.POP_HL)
        builder.appendOp(Op.POP_DE)
        builder.appendOp(Op.OR_A)
        builder.appendOp(Op.SBC_HL_DE)
        builder.appendOp(Op.PUSH_HL)
    }

    override fun call(other: Function) {
        builder.appendOp(Op.CALL)
        builder.markReference(other)
        builder.appendShort(0)
    }

    override fun eqS16() {
        builder.appendOp(Op.POP_HL)
        builder.appendOp(Op.POP_BC)
        builder.appendOp(Op.OR_A)
        builder.appendOp(Op.SBC_HL_BC)
        builder.appendOp(Op.JR_Z)
        builder.appendByte(5)
        builder.appendOp(Op.LD_HL_NN)
        builder.appendShort(1)
    }

    override fun getLocal(variable: Variable, stackOffset: Int) {
        builder.appendOp(Op.LD_HL_NN)
        builder.appendShort(baseOffset + stackOffset - variable.offset - 2)
        builder.appendOp(Op.ADD_HL_SP)
        builder.appendOp(Op.LD_C__HL)
        builder.appendOp(Op.INC_HL)
        builder.appendOp(Op.LD_B__HL)
        builder.appendOp(Op.PUSH_BC)
    }

    override fun ret() {
        builder.appendOp(Op.RET)
    }

    override fun ifCode(elseKey: Any) {
        builder.appendOp(Op.POP_HL)
        builder.appendOp(Op.LD_A_H)
        builder.appendOp(Op.OR_L)
        builder.appendOp(Op.JP_Z)
        builder.markReference(elseKey)
        builder.appendShort(0)
    }

    override fun elseCode(endKey: Any) {
        builder.appendOp(Op.JP)
        builder.markReference(endKey)
        builder.appendShort(0)
    }

    override fun openStackFrame(parameterCount: Int, localCount: Int, returnValueCount: Int) {
        if (localCount != 0) {
            builder.appendOp(Op.LD_HL_NN)
            builder.appendShort(-localCount * 2)
            builder.appendOp(Op.ADD_HL_SP)
            builder.appendOp(Op.LD_SP_HL)
        }
        baseOffset = (parameterCount + localCount + 1) * 2
    }

    override fun closeStackFrame(parameterCount: Int, localCount: Int, returnValueCount: Int) {
        // Skip return values and locals on the stack to get to the return address
        builder.appendOp(Op.LD_HL_NN)
        builder.appendShort((returnValueCount + localCount) * 2)
        builder.appendOp(Op.ADD_HL_SP)
        // load BC from ret address
        builder.appendOp(Op.LD_C__HL)
        builder.appendOp(Op.INC_HL)
        builder.appendOp(Op.LD_B__HL)

        // Move the return value(s) to the right address
        builder.appendOp(Op.LD_HL_NN)
        builder.appendShort((returnValueCount + localCount + parameterCount + 1) * 2 - 1)
        builder.appendOp(Op.ADD_HL_SP)
        builder.appendOp(Op.EX_DE_HL)

        builder.appendOp(Op.LD_HL_NN)
        builder.appendShort((returnValueCount) * 2 - 1)
        builder.appendOp(Op.ADD_HL_SP)

        for (i in 0 until returnValueCount) {
            builder.appendOp(Op.LDD)
            builder.appendOp(Op.LDD)
        }
        builder.appendOp(Op.EX_DE_HL)
        builder.appendOp(Op.INC_HL)
        builder.appendOp(Op.LD_SP_HL)

        builder.appendOp(Op.LD_HL_NN)
        builder.appendShort(returnValueCount * 2)
        builder.appendOp(Op.ADD_HL_BC)
        builder.appendOp(Op.JP_HL)
    }
}