package org.kobjects.hi5

class Parser(
    input: String,
    val platform: Platform,
) {
    val main = Function(emptyList(), listOf(Type.S16))
    val tokenizer = Tokenizer(input)
    val functions = mutableMapOf<String, Function>()

    fun parse(): Function {
        tokenizer.consume();
        parseBody(main)
        return main
    }

    fun parseDeclaration(): Function {
        val name = tokenizer.consume()

        tokenizer.consume("{")

        val parameters = mutableListOf<Parameter>()
        while (!tokenizer.tryConsume("--") && tokenizer.currentToken != "}") {
            val typeName = tokenizer.consume()
            val name = tokenizer.consume()
            val type = Type.resolve(typeName)
            parameters.add(Parameter(type, name))
        }

        val returnTypes = mutableListOf<Type>()
        while (!tokenizer.tryConsume("}")) {
            val typeName = tokenizer.consume()
            val type = Type.resolve(typeName)
            returnTypes.add(type)
        }

        val function = Function(parameters, returnTypes)
        functions[name] = function
        parseBody(function)
        return function
    }

    fun parseBody(function: Function) {
        val codeGenerator = platform.createCodeGenerator(function.code)
        val root = function == main

        codeGenerator.openStackFrame(function.parameters.size, 0, function.returnTypes.size)

        val stack = mutableListOf<Type>()
        while (true) {
            val token = tokenizer.consume()
            if (token == if (root) "" else ";") {
                break
            }
            if (token[0] >= '0' && token[0] <= '9') {
                codeGenerator.literalS16(token.toInt())
                stack.add(Type.S16)
            } else when (token) {
                ":" -> {
                    require(function == main) {
                        "Nested functions are not supported."
                    }
                    parseDeclaration()
                }
                "+" -> {
                    require(stack.size >= 2 && stack.removeLast() == Type.S16 && stack.last() == Type.S16) {
                        "Two S16 values required on the stack."
                    }
                    codeGenerator.addS16()
                }
                else -> {
                    val calling = functions[token]
                    require (calling != null) {
                        throw IllegalStateException("Can't resolve $token; declarations: $functions")
                    }
                    val paramCount = calling.parameters.size
                    require(stack.size >= paramCount) {
                        "Stack size must be at least $paramCount for calling $token."
                    }
                    for (i in 0 until paramCount) {
                        require(stack[stack.size - paramCount + i] == calling.parameters[i].type) {
                            "Expected parameter #$i: ${function.parameters[i]} buf got ${stack[stack.size - paramCount + i]}"
                        }
                    }
                    codeGenerator.call(calling)
                    stack.dropLast(paramCount)
                    for (parameter in calling.parameters) {
                        stack.add(parameter.type)
                    }
                }
            }
        }

        /*
        require(stack.size == function.returnTypes.size) {
            "${function.returnTypes.size} return values expected but got ${stack.size}."
        }*/

        codeGenerator.closeStackFrame(function.parameters.size, 0, function.returnTypes.size)
    }
}