package com.zelkatani

data class Table(val header: Header, val rows: List<Row>) {
    override fun toString() = buildString {
        appendln("# These test vectors were programmatically generated.")
        appendln(header.toString())
        rows.forEach {
            appendln(it.toString())
        }
    }
}

class TableBuilder : Builder<Table> {
    private lateinit var header: Header
    private val rows = mutableListOf<Row>()

    fun header(header: Header) {
        this.header = header
    }

    fun header(block: HeaderBuilder.() -> Unit) {
        val builder = HeaderBuilder()

        val header = builder.let {
            it.block()
            it.build()
        }

        header(header)
    }

    fun row(block: RowBuilder.() -> Unit) {
        val builder = RowBuilder()

        val row = builder.let {
            it.block()
            it.build()
        }

        require(header.size == row.size) {
            "The amount of entries in a row must equal the number of items in the header."
        }

        rows.add(row)
    }

    fun row(entryType: EntryType, vararg entryList: String) {
        val mappedEntries = entryList.map {
            Entry(it, entryType)
        }

        val row = Row(mappedEntries)
        rows.add(row)
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
            row(*spreaded, evaluated)
        }
    }

    override fun build() = Table(header, rows)
}

fun buildTable(block: TableBuilder.() -> Unit) = TableBuilder().let {
    it.block()
    it.build()
}