package org.kobjects.hi5

class Tokenizer(
    private val input: String
) {
    private var pos = 0;

    var currentToken = ""

    fun consume(): String {
        val result = currentToken
        while (pos < input.length && input[pos].isWhitespace()) {
            pos++
        }
        if (pos >= input.length) {
            currentToken = ""
        } else {
            var end = pos;
            while (end < input.length && !input[end].isWhitespace()) {
                end++
            }
            currentToken = input.substring(pos, end)
            pos = end
        }
        return result
    }

    fun consume(s: String) {
        require(currentToken == s) {
            "$s expcted; got $currentToken"
        }
        consume()
    }

    fun tryConsume(s: String): Boolean {
        if (currentToken == s) {
            consume()
            return true
        }
        return false
    }

}