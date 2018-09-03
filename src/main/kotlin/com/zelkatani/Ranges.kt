package com.zelkatani

class Ranges(private val ranges: List<List<Int>>) : Iterable<List<Int>> {
    override fun iterator(): Iterator<List<Int>> {
        return RangesIterator(ranges)
    }
}

class RangesIterator(private val ranges: List<List<Int>>) : Iterator<List<Int>> {

    private val rangeSizes = ranges.map { it.size }
    private val indices = Array(ranges.size) { 0 }
    private var hasNext = true

    override fun hasNext() = hasNext

    override fun next(): List<Int> {
        for (i in indices.size - 1 downTo 0) {
            if (indices[i] == rangeSizes[i] - 1) {
                indices[i] = 0
                if (i == 0) {
                    hasNext = false
                }

            } else {
                indices[i]++
                break
            }
        }

        return ranges.mapIndexed { index, list ->
            list[indices[index]]
        }
    }

}