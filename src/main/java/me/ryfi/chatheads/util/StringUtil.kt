package me.ryfi.chatheads.util

fun String.chunkedMessage(size: Int) : List<String> {

    if (!intern().contains(" ")) {
        return intern().chunked(size)
    }

    val buffer = StringBuilder()
    val chunked = mutableListOf<String>()

    for (word in intern().split(" ")){
        if(buffer.length + word.length > size){
            chunked.add(buffer.toString())
            buffer.clear()
        } else {
            buffer.append(word)
        }
    }
    return chunked
}
