package org.kobjects.hi5

open class Function(
    val parameters: List<Variable>,
    val locals: List<Variable>,
    val returnTypes: List<Type>
) {
    var code = Memory()
}