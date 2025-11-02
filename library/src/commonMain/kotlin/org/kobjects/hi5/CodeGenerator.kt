package org.kobjects.hi5

interface CodeGenerator {

    val builder: Memory

    fun literalS16(value: Int)

    fun addS16()

    fun call(other: Function)

    fun eqS16()

    fun getLocal(variable: Variable, stackOffset: Int)

    fun ret();

    fun ifCode(elseKey: Any)

    fun subS16()

    fun openStackFrame(parameterCount: Int, localCount: Int, returnValueCount: Int)

    fun closeStackFrame(parameterCount: Int, localCount: Int, returnValueCount: Int)

    fun elseCode(endKey: Any)
}