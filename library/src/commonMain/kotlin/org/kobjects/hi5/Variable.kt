package org.kobjects.hi5

data class Variable(
    val type: Type,
    val name: String,
    val offset: Int
) {
}