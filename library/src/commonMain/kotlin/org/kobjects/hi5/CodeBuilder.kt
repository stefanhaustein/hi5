package org.kobjects.hi5

class CodeBuilder {
    var size: Int = 0
    var data: ByteArray = ByteArray(1024)
    val calls = mutableMapOf<Function, MutableList<Int>>()

    fun appendOp(op: Int) {
        if (op > 255) {
            appendByte(op / 256)
        }
        appendByte(op % 256)
    }

    fun appendWord(value: Int) {
        appendByte(value % 256)
        appendByte(value / 256)
    }

    fun appendByte(value: Int) {
        if (size >= data.size) {
            data = data.copyOf(data.size * 3 / 2)
        }
        data[size++] = value.toByte()
    }

    fun appendBytes(value: ByteArray, offset: Int = 0, length: Int = value.size) {
        if (data.size < size + length) {
            data = data.copyOf(size + length)
        }
        value.copyInto(data, size, startIndex = offset, endIndex = offset + length)
        size += length
    }


    fun markReference(function: Function) {
        calls.getOrPut(function) { mutableListOf<Int>() }.add(size)
    }



}