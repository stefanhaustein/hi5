package org.kobjects.hi5

class Function(
    val parameters: List<Variable>,
    val locals: List<Variable>,
    val returnTypes: List<Type>
) {
    var code = CodeBuilder()

}