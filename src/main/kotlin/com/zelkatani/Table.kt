package com.zelkatani

import java.io.File
import java.io.FileNotFoundException
import java.io.PrintWriter

private const val TABLE_HEADER = "# These test vectors were programmatically generated."

data class Table(val header: Header, val rows: List<Row>) {

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

class TableBuilder(exporting: Pair<String, Boolean>?) : Builder<Table> {
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

    fun header(header: Header) {
        this.header = header

        if (writeAsWeGo) {
            exportFileWriter?.let {
                it.println(TABLE_HEADER)
                it.println(header)
            }
        }
    }

    fun header(block: HeaderBuilder.() -> Unit) {
        val builder = HeaderBuilder()

        val header = builder.let {
            it.block()
            it.build()
        }

        header(header)
    }

    fun row(row: Row) {
        require(header.size == row.size) {
            "The amount of entries in a row must equal the number of items in the header."
        }

        rows.add(row)

        if (writeAsWeGo) {
            exportFileWriter?.println(row)
        }
    }

    fun row(block: RowBuilder.() -> Unit) {
        val builder = RowBuilder()

        val row = builder.let {
            it.block()
            it.build()
        }

        row(row)
    }

    fun row(entryType: EntryType, vararg entryList: String) {
        val mappedEntries = entryList.map {
            Entry(it, entryType)
        }

        val row = Row(mappedEntries)
        row(row)
    }

    fun row(vararg intEntries: Int) = row {
        intEntries.forEach {
            entry(it)
        }
    }

    fun functionPermutations(function: Function<Int>, vararg spreads: List<Int>) {
        val ranges = Ranges(spreads.toList())
        ranges.forEach {
            val spreaded = it.toIntArray()
            val evaluated = function.eval(spreaded)
            row(*spreaded, *evaluated.toIntArray())
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

fun buildTable(exporting: Pair<String, Boolean>? = null, block: TableBuilder.() -> Unit) = TableBuilder(exporting).let {
    it.block()
    it.build()
}