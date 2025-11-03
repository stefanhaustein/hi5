package org.kobjects.hi5

import kotlin.test.Test
import kotlin.test.assertEquals

class Hi5Test {

    @Test
    fun constantTest() {
        assertEquals(42, Hi5.run(Hi5.parse("42")))
    }

    @Test
    fun functionTest() {
        assertEquals(42, Hi5.run(Hi5.parse(": fourtytwo { -- w: } 42 ; fourtytwo")))
    }


    @Test
    fun localTest() {
        assertEquals(42, Hi5.run("""
            : double { w: input -- w: } 
               input input + 
            ;
            21 double
            """))
    }

    @Test
    fun recursionTest() {
        assertEquals(21, Hi5.run("""
            : fib { w: input -- w: } 
               input 0 = if
                 0 
               else 
                 input 1 = if 
                   1 
                 else 
                   input 1 - fib input 2 - fib + 
                 then 
               then
            ;
            8 fib
            """))
    }
}