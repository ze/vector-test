package com.zelkatani

import com.zelkatani.EntryType.*
import org.junit.jupiter.api.Test

// TODO assertions
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
    }

    @Test
    fun `Test Function Generation`() {
        val add32Function = object : Function<Int> {
            override fun eval(fields: IntArray): Int {
                require(fields.size == 2) {
                    "32 bit adder requires exactly two parameters."
                }

                val a = fields[0]
                val b = fields[1]
                return a + b
            }
        }

        val table = buildTable {
            header {
                item("A", 32)
                item("B", 32)
                item("C", 32)
            }

            functionPermutations(add32Function, (-200..200).toList(), (-200..200).toList())
        }
    }

    @Test
    fun `Test File Exporting`() {
        val andFunction = object : Function<Int> {
            override fun eval(fields: IntArray): Int {
                require(fields.size == 2) {
                    "Function requires exactly two parameters."
                }

                val a = fields[0]
                val b = fields[1]
                return a and b
            }

        }

        val table = buildTable {
            header {
                item("A", 4)
                item("B", 4)
                item("C", 4)
            }

            val signedFourBitRange = (-8..7).toList()
            functionPermutations(andFunction, signedFourBitRange, signedFourBitRange)
        }

        val file = table.exportToFile("and-testvector")
        assert(file.exists())
        assert(file.readLines().size == 258)
        file.deleteOnExit()
    }
}