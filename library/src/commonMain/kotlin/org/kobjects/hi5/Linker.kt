package org.kobjects.hi5

object Linker {
    fun writeCode(baseAddress: Int, main: Function): CodeBuilder {
        var code = CodeBuilder()
        val functionAddressMap = mutableMapOf<Function, Int>()
        val unresolvedAddressMap = mutableMapOf<Function, MutableList<Int>>()

        fun writeFunction(function: Function) {
            if (functionAddressMap.containsKey(function)) {
                // Already written
                return
            }
            functionAddressMap[function] =  code.size + baseAddress

            for (entry in function.code.calls.entries) {
                for (value in entry.value) {
                    unresolvedAddressMap.getOrPut(entry.key) { mutableListOf() }.add(value + code.size + baseAddress)
                }
            }

            code.appendBytes(function.code.data, 0, function.code.size)

            for (call in function.code.calls.keys) {
                writeFunction(call)
            }
        }

        writeFunction(main)

        for (entry in unresolvedAddressMap.entries) {
            val functionAddress = functionAddressMap[entry.key]!!
            for (callAddress in entry.value) {
                code.data[callAddress] = (functionAddress % 256).toByte()
                code.data[callAddress + 1] = (functionAddress / 256).toByte()
            }
        }

        return code
    }
}