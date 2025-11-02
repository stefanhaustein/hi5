package org.kobjects.hi5

object Linker {
    fun writeCode(baseAddress: Int, main: Function): Memory {
        var code = Memory()
        val unresolvedAddressMap = mutableMapOf<Any, MutableList<Int>>()

        fun writeFunction(function: Function) {
            if (code.resolvedKeys.containsKey(function)) {
                // Already written
                return
            }

            code.resolvedKeys[function] = code.size + baseAddress
            for (entry in function.code.resolvedKeys.entries) {
                code.resolvedKeys[entry.key] = code.size + baseAddress + entry.value
            }

            for (entry in function.code.references.entries) {
                for (value in entry.value) {
                    unresolvedAddressMap.getOrPut(entry.key) { mutableListOf() }.add(value + code.size + baseAddress)
                }
            }

            code.appendBytes(function.code.data, 0, function.code.size)

            for (call in function.code.references.keys) {
                if (call is Function) {
                    writeFunction(call)
                }
            }
        }

        writeFunction(main)

        for (entry in unresolvedAddressMap.entries) {
            val functionAddress = code.resolvedKeys[entry.key]!!
            for (callAddress in entry.value) {
                code.setShort(callAddress, functionAddress)
            }
        }

        return code
    }
}