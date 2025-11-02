package org.kobjects.hi5

import org.kobjects.hi5.Type
import org.kobjects.hi5.Variable

class NamedFunction(
    val name: String,
    parameters: List<Variable>,
    locals: List<Variable>,
    returnTypes: List<Type>
) : Function(parameters, locals, returnTypes) {

    override fun toString() = name
}
