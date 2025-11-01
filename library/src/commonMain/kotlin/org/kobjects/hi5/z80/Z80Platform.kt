package org.kobjects.hi5.z80

import org.kobjects.hi5.CodeBuilder
import org.kobjects.hi5.CodeGenerator
import org.kobjects.hi5.Platform

object Z80Platform : Platform {
    override fun createCodeGenerator(builder: CodeBuilder): CodeGenerator = Z80CodeGenerator(builder)
}