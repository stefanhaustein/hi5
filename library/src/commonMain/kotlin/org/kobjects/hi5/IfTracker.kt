package org.kobjects.hi5

class IfTracker(stack: List<Type>) {
    val count = globalCount++
    val stack = stack.toList()
    val elseKey = "else$count"
    val endKey = "end$count"
    var elseStack: List<Type>? = null

    companion object {
        var globalCount = 0
    }
}