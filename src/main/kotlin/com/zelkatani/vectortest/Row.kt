package com.zelkatani.vectortest

import com.zelkatani.vectortest.EntryType.*

/**
 * A table row entry. This describes one set of values for the table.
 *
 * @property entries the entries of the row.
 */
data class Row(val entries: List<Entry>) {
    /**
     * The number of items in the row.
     */
    val size = entries.size

    /**
     * The row with all entries being space separated.
     */
    override fun toString() = entries.joinToString(" ") {
        it.toString() + " "
    }
}

/**
 * A [Builder] for [Row].
 */
class RowBuilder : Builder<Row> {
    private val entries = mutableListOf<Entry>()

    /**
     * Add a single entry with [value] to the row.
     */
    fun entry(value: Int) {
        if (value >= 0) {
            entry(value.toString())
        } else {
            entry(value.toString(), NEG_INT)
        }
    }

    /**
     * Add a [value] of type [entryType] to the row.
     */
    @JvmOverloads
    fun entry(value: String, entryType: EntryType = UINT) {
        val entry = Entry(value, entryType)
        entries.add(entry)
    }

    /**
     * Add entries of type [entryType] to the row.
     */
    fun entries(entryType: EntryType, vararg entryList: String) {
        val mappedEntries = entryList.map {
            Entry(it, entryType)
        }

        entries.addAll(mappedEntries)
    }

    /**
     * Add [intEntries] to the row.
     */
    fun entries(vararg intEntries: Int) {
        intEntries.forEach {
            entry(it)
        }
    }

    override fun build() = Row(entries)
}

/**
 * A row entry. Has a [value] and an [entryType] that defines what the entry is.
 *
 * @property value the contents of the entry.
 * @property entryType the data type of the entry.
 */
data class Entry @JvmOverloads constructor(val value: String, val entryType: EntryType = UINT) {
    init {
        val isValid = when (entryType) {
            UINT -> value.toIntOrNull()?.let {
                it >= 0
            } ?: false

            NEG_INT -> value.toIntOrNull() != null

            BINARY -> try {
                value.toLong(2)
                true
            } catch (e: NumberFormatException) {
                false
            }

            HEXADECIMAL -> if (value.startsWith("0x")) {
                try {
                    value.substring(2).toLong(16)
                    true
                } catch (e: NumberFormatException) {
                    false
                }
            } else {
                false
            }

            OCTAL -> if (value.startsWith("0o")) {
                try {
                    value.substring(2).toLong(8)
                    true
                } catch (e: NumberFormatException) {
                    false
                }
            } else {
                false
            }
        }

        require(isValid) {
            "Entry '$value' was given an incorrect type and/or value of '$entryType'"
        }
    }

    /**
     * The [value] of the entry.
     */
    override fun toString() = value
}

/**
 * All possible entry types that can be represented in the table.
 */
enum class EntryType {
    /**
     * An unsigned integer.
     */
    UINT,
    /**
     * A negative integer.
     */
    NEG_INT,
    /**
     * A binary number.
     */
    BINARY,
    /**
     * A hexadecimal number.
     */
    HEXADECIMAL,
    /**
     * An octal number.
     */
    OCTAL
}