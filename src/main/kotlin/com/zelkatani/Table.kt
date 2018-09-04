package com.zelkatani

import java.io.File
import java.io.FileNotFoundException
import java.io.PrintWriter

/**
 * Header message for all tables.
 */
private const val TABLE_HEADER = "# These test vectors were programmatically generated."

/**
 * A table that follows the vector test model.
 */
data class Table(val header: Header, val rows: List<Row>) {

    /**
     * Export the [Table] as a [File].
     */
    fun exportToFile(path: String): File {
        val goodPath = if (!path.endsWith(".txt")) {
            "$path.txt"
        } else {
            path
        }

        val file = File(goodPath)
        if (!file.exists()) {
            if (!file.createNewFile()) {
                throw FileNotFoundException("File creation failed.")
            }
        }

        file.writeText(toString())

        return file
    }

    override fun toString() = buildString {
        appendln(TABLE_HEADER)
        appendln(header)
        rows.forEach {
            appendln(it)
        }
    }
}

/**
 * A [Builder] for [Table].
 */
class TableBuilder(exporting: Pair<String, Boolean>?) : Builder<Table> {
    // this is lateinit so that rows aren't placed into the table before the header.
    private lateinit var header: Header
    private val rows = mutableListOf<Row>()

    private val exportFile = exporting?.first?.let {
        val goodPath = if (!it.endsWith(".txt")) {
            "$it.txt"
        } else {
            it
        }

        File(goodPath)
    }
    private val exportFileWriter = exportFile?.let {
        PrintWriter(it.writer(), true)
    }
    private val writeAsWeGo = exporting?.second ?: false

    /**
     * Set a [Header] object as the table header.
     */
    fun header(header: Header) {
        this.header = header

        if (writeAsWeGo) {
            exportFileWriter?.let {
                it.println(TABLE_HEADER)
                it.println(header)
            }
        }
    }

    /**
     * Set a built header as the table header.
     */
    fun header(block: HeaderBuilder.() -> Unit) {
        val builder = HeaderBuilder()

        val header = builder.apply(block).build()

        header(header)
    }

    /**
     * Place a [Row] object into the table.
     */
    fun row(row: Row) {
        require(header.size == row.size) {
            "The amount of entries in a row must equal the number of items in the header."
        }

        rows.add(row)

        if (writeAsWeGo) {
            exportFileWriter?.println(row)
        }
    }

    /**
     * Place a built row into the table.
     */
    fun row(block: RowBuilder.() -> Unit) {
        val builder = RowBuilder()

        val row = builder.apply(block).build()
        row(row)
    }

    /**
     * Place a number of entries of type [entryType] into a new row in the table.
     */
    fun row(entryType: EntryType, vararg entryList: String) {
        val mappedEntries = entryList.map {
            Entry(it, entryType)
        }

        val row = Row(mappedEntries)
        row(row)
    }

    /**
     * Place [intEntries] into a new row in the table.
     */
    fun row(vararg intEntries: Int) = row {
        intEntries.forEach {
            entry(it)
        }
    }

    /**
     * Populate the table with the cartesian product of [spreads] mapped onto [function].
     */
    fun functionPermutations(function: Function<Int>, vararg spreads: List<Int>) {
        val ranges = Ranges(spreads.toList())
        ranges.forEach {
            val spreadIntArray = it.toIntArray()
            val evaluated = function.eval(spreadIntArray).toIntArray()
            row(*spreadIntArray, *evaluated)
        }
    }

    override fun build(): Table {
        val table = Table(header, rows)

        if (!writeAsWeGo && exportFile != null) {
            table.exportToFile(exportFile.path)
        }

        exportFileWriter?.close()

        return table
    }
}

/**
 * A [TableBuilder] function.
 */
@JvmOverloads
fun buildTable(exporting: Pair<String, Boolean>? = null, block: TableBuilder.() -> Unit) = TableBuilder(exporting)
        .apply(block)
        .build()