package org.kobjects.hi5

interface CodeGenerator {

    val builder: CodeBuilder

    fun literalS16(value: Int)

    fun addS16()

    fun call(other: Function)

    fun getLocal(variable: Variable, stackSize: Int)

    fun ret();

    fun openStackFrame(parameterCount: Int, localCount: Int, returnValueCount: Int)

    fun closeStackFrame(parameterCount: Int, localCount: Int, returnValueCount: Int)
}