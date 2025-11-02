package org.kobjects.hi5

import io.github.kotlin.fibonacci.org.kobjects.hi5.Opcode
import kotlin.math.max

class Memory(initialSize: Int = 0) {
    var size: Int = initialSize
    var data: ByteArray = ByteArray(max(initialSize, 256))

    val references = mutableMapOf<Any, MutableList<Int>>()
    val resolvedKeys = mutableMapOf<Any, Int>()

    fun appendOp(opcode: Opcode) {
        val op = opcode.opcode
        if (op > 255) {
            appendByte(op / 256)
        }
        appendByte(op % 256)
    }

    fun appendShort(value: Int) {
        appendByte(value)
        appendByte(value shr 8)
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

    fun markReference(key: Any) {
        references.getOrPut(key) { mutableListOf<Int>() }.add(size)
    }

    fun getByte(index: Int): Int = data[index].toInt() and 0xFF

    fun getShort(index: Int): Int = getByte(index) or (getByte(index + 1) shl 8)

    fun setByte(index: Int, value: Int) {
        data[index] = value.toByte()
    }

    fun setShort(index: Int, value: Int) {
        data[index] = (value and 0xff).toByte()
        data[index + 1] = (value ushr 8).toByte()
    }
}