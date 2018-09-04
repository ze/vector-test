package com.zelkatani.vectortest

import org.junit.jupiter.api.Test

internal class RangesTest {
    @Test
    fun `Test Ranges Iterator`() {

        var intRanges = listOf((1..5).toList(), listOf(3), (7..8).toList())
        // no way to have EITHER a range or an int, so just make it a range that only goes onto itself

        var ranges = Ranges(intRanges)

        var total = 0
        ranges.forEach {
            total++
        }
        assert(total == 10)

        intRanges = listOf((1..100).toList(), listOf(1, 5, 7), (1 until 6 step 2).toList())
        ranges = Ranges(intRanges)

        total = 0
        ranges.forEach {
            total++
        }

        // the size of a cartesian product is just the product of all of the list sizes. 100 * 3 * 3.
        assert(900 == total)
    }
}