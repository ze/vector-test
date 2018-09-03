package com.zelkatani

import org.junit.jupiter.api.Test

internal class RangesTest {
    @Test
    fun `Test Ranges Iterator`() {

        val intRanges = listOf((1..5).toList(), listOf(3), (7..8).toList())
        // no way to have EITHER a range or an int, so just make it a range that only goes onto itself

        var ranges = Ranges(intRanges)

        var total = 0
        ranges.forEach {
            total++
        }
        assert(total == 10)

        val secondIntRanges = listOf((1..100).toList(), listOf(1, 5, 7), (1 until 6 step 2).toList())
        ranges = Ranges(secondIntRanges)
        ranges.forEach {
            println(it)
        }
    }
}