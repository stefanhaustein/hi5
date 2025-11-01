package org.kobjects.hi5

interface Platform {
    fun createCodeGenerator(builder: CodeBuilder): CodeGenerator
}