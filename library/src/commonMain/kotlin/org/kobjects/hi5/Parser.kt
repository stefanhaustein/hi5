package org.kobjects.hi5

class Parser(
    input: String,
    val platform: Platform,
) {
    val main = Function(emptyList(), emptyList(), listOf(Type.S16))
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

        val parameters = mutableListOf<Variable>()
        val locals = mutableListOf<Variable>()
        var vars = parameters
        var localOffset = 0
        while (tokenizer.currentToken != "--" && tokenizer.currentToken != "}") {
            if (tokenizer.tryConsume("|")) {
                require(vars == parameters) {
                    "Duplicated '|'"
                }
                vars = locals
                localOffset+=2
            }
            val typeName = tokenizer.consume()
            val name = tokenizer.consume()
            val type = Type.resolve(typeName)
            vars.add(Variable(type, name, localOffset))
            localOffset += 2
        }

        val returnTypes = mutableListOf<Type>()
        if (tokenizer.tryConsume("--")) {
            while (tokenizer.currentToken != "}") {
                val typeName = tokenizer.consume()
                val type = Type.resolve(typeName)
                returnTypes.add(type)
            }
        }
        tokenizer.consume("}")

        val function = Function(parameters, locals, returnTypes)
        functions[name] = function
        parseBody(function)
        return function
    }

    fun parseBody(function: Function) {
        val codeGenerator = platform.createCodeGenerator(function.code)
        val root = function == main
        val locals = (function.parameters + function.locals).associate { it.name to it }

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
            } else if (locals[token] != null) {
                val local = locals[token]!!
                codeGenerator.getLocal(local, stack.size)
                stack.add(local.type)
            } else if (functions[token] != null) {
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
                    val local = locals[token]


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