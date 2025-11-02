package org.kobjects.hi5

class IfTracker(stack: List<Type>) {
    val stack = stack.toList()
    val elseKey = Any()
    val endKey = Any()
    var elseStack: List<Type>? = null
}