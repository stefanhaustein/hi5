package org.kobjects.hi5

enum class Type {
    BOOL, S16;

    companion object {
        fun resolve(name: String): Type {
            when (name) {
                "w:" -> return S16
                else -> throw IllegalArgumentException("Unrecognized type: $name")
            }
        }
    }
}