package me.ryfi.chatheads.util

fun String.chunkedMessage(
    size: Int
): List<String> {
    val lines = mutableListOf<String>()

    if (!this.contains(" ")) {
        return this.chunked(size)
    }
    var firstWord = true
    val builder = StringBuilder()

    for (word in this.split(" ")) {
        if (builder.length + word.length > size) {
            lines.add(builder.toString())
            builder.setLength(0)
            firstWord = true
        }
        if (firstWord) firstWord = false else builder.append(' ')
        builder.append(word)
    }
    if (builder.isNotEmpty()) {
        lines.add(builder.toString())
    }
    return lines
}
