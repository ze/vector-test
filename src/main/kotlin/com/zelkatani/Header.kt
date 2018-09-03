package com.zelkatani

data class Header(private val headers: List<HeaderItem>) {
    val size = headers.size

    override fun toString() = headers.joinToString(" ") {
        it.toString()
    }
}

class HeaderBuilder : Builder<Header> {
    private val headers = mutableListOf<HeaderItem>()

    fun item(name: String, dataBits: Int = 1) {
        val headerItem = HeaderItem(name, dataBits)
        headers.add(headerItem)
    }

    override fun build() = Header(headers)
}

data class HeaderItem(private val name: String, private val dataBits: Int = 1) {
    override fun toString() = "$name[$dataBits]"
}