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
        assertEquals(42, Hi5.run(Hi5.parse(": double { w: input -- w: } input ; 42 double")))
    }


}