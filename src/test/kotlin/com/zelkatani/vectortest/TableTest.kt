package com.zelkatani.vectortest

import com.zelkatani.vectortest.EntryType.*
import org.junit.jupiter.api.Test
import java.io.File

internal class TableTest {
    @Test
    fun `Test buildTable`() {
        val table = buildTable {
            header {
                item("S", 32)
                item("Sa", 5)
                item("Cin")
                item("Cout", 32)
            }

            row(34, 2, 0, 136)
            row(325948595, 15, 0, -900104192)
            row(BINARY, "00000000000000000000000000000001", "00100", "0", "00000000000000000000000000011111")
            row(HEXADECIMAL, "0x00000005", "0x01", "0x0", "0x0000000A")
            row(OCTAL, "0o17777777777", "0o1", "0o0", "0o37777777776")
            row {
                entry("00011111001010100010110111110001", BINARY)
                entry("0o10", OCTAL)
                entry("0x0", HEXADECIMAL)
                entry(707653888)
            }
        }

        assert(table.header.size == 4)
        assert(table.rows.size == 6)
    }

    @Test
    fun `Test Function Generation`() {
        val add32Function = object : Function<Int> {
            override fun eval(fields: IntArray): Array<Int> {
                require(fields.size == 2) {
                    "32 bit adder requires exactly two parameters."
                }

                val a = fields[0]
                val b = fields[1]
                return arrayOf(a + b)
            }
        }

        val table = buildTable {
            header {
                item("A", 32)
                item("B", 32)
                item("C", 32)
            }

            // this list has 401 items in it. -200 to -1, 0, 1 to 200.
            val spread = (-200..200).toList()
            functionPermutations(add32Function, spread, spread)
        }

        assert(table.header.size == 3)
        assert(table.rows.size == 401 * 401)
    }

    @Test
    fun `Test Function with Multi Returns`() {
        val fileName = "multiple_returns.txt"

        val add32OverflowFunction = object : Function<Int> {
            override fun eval(fields: IntArray): Array<Int> {
                require(fields.size == 2) {
                    "There must be exactly two parameters in a one bit adder."
                }
                val a = fields[0]
                val b = fields[1]

                val sum = a + b
                val overflow = if (a xor sum and (b xor sum) < 0) 1 else 0 // signs are different

                return arrayOf(sum, overflow)
            }
        }

        val table = buildTable(fileName to true) {
            header {
                item("A", 32)
                item("B", 32)
                item("C", 32)
                item("V")
            }

            // 43 entries 2,147,483,647 / 100_000_000 -> 21. Double to get 42. Since bounds are inclusive, +1 = 43.
            val intRange = (Integer.MIN_VALUE..Integer.MAX_VALUE step 100_000_000).toList()

            functionPermutations(add32OverflowFunction, intRange, intRange)
        }

        assert(table.rows.size == 43 * 43)
        File(fileName).deleteOnExit()
    }

    @Test
    fun `Test File Exporting`() {
        val andFunction = object : Function<Int> {
            override fun eval(fields: IntArray): Array<Int> {
                require(fields.size == 2) {
                    "Function requires exactly two parameters."
                }

                val a = fields[0]
                val b = fields[1]
                return arrayOf(a and b)
            }
        }

        val table = buildTable {
            header {
                item("A", 4)
                item("B", 4)
                item("C", 4)
            }

            // 16 items in this range.
            val signedFourBitRange = (-8..7).toList()
            functionPermutations(andFunction, signedFourBitRange, signedFourBitRange)
        }

        val file = table.exportToFile("and-testvector")
        assert(file.exists())
        assert(file.readLines().size == 258) // 256 rows, one header, and one descriptor.
        file.deleteOnExit()
    }

    @Test
    fun `Test File Writing As We Go`() {
        val fileName = "write_as_we_go.txt"

        val doublingFunction = object : Function<Int> {
            override fun eval(fields: IntArray): Array<Int> {
                return arrayOf(fields[0] * 2)
            }
        }

        buildTable(fileName to true) {
            header {
                item("A", 32)
                item("B", 32)
            }

            functionPermutations(doublingFunction, (1..10_000 step 2).toList())
        }

        File(fileName).deleteOnExit()
    }
}