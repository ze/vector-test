package com.zelkatani.vectortest

/**
 * A table header. This describes the columns.
 */
data class Header(private val headers: List<HeaderItem>) {
    /**
     * The number of columns in the table.
     */
    val size = headers.size

    /**
     * All of the headers together but space separated.
     */
    override fun toString() = headers.joinToString(" ") {
        it.toString()
    }
}

/**
 * A [Builder] for [Header].
 */
class HeaderBuilder : Builder<Header> {
    private val headers = mutableListOf<HeaderItem>()

    /**
     * Add a new column with [name] and bit width of [dataBits] to the header.
     */
    fun item(name: String, dataBits: Int = 1) {
        val headerItem = HeaderItem(name, dataBits)
        headers.add(headerItem)
    }

    override fun build() = Header(headers)
}

/**
 * A header entry, or a column with a specific bit length.
 */
data class HeaderItem @JvmOverloads constructor(private val name: String, private val dataBits: Int = 1) {
    init {
        require(dataBits >= 1) {
            "Data bits must be at least one."
        }
    }

    /**
     * The representation of the header. If data bits are > 1, it will be put next to the header name surrounded by brackets.
     */
    override fun toString(): String {
        return if (dataBits > 1) {
            "$name[$dataBits]"
        } else {
            name
        }
    }
}