package util

class Csv(private vararg val parameterNames: String) {

    private val rows: MutableList<Array<out Object>> = mutableListOf()

    fun getHeaderLine(): String {
        return concatenateRow(parameterNames)
    }

    private fun concatenateRow(elements: Array<out Any>): String {
        val stringBuilder = StringBuilder()

        for (i in 0..(elements.size - 2)) {
            stringBuilder.append(elements[i].toString())
            stringBuilder.append(",")
        }

        stringBuilder.append(elements[elements.size - 1].toString())

        return stringBuilder.toString()
    }

    private fun concatenateRow(elements: Array<out Number>): String {
        val stringBuilder = StringBuilder()

        for (i in 0..(elements.size - 2)) {
            stringBuilder.append(elements[i].toString())
            stringBuilder.append(",")
        }

        stringBuilder.append(elements[elements.size - 1].toString())

        return stringBuilder.toString()
    }

    fun addRow(vararg values: Object) {
        rows.add(values)
    }

    fun addRowAndPrint(vararg values: Object) {
        addRow(*values)

        println(concatenateRow(values))
    }

    override fun toString(): String {
        val stringBuilder = StringBuilder()

        stringBuilder.append(getHeaderLine())

        for (row in rows) {
            stringBuilder.append("\n")
            stringBuilder.append(concatenateRow(row))
        }

        return stringBuilder.toString()
    }
}