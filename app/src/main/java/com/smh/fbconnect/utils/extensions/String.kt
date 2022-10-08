package com.smh.fbconnect.utils.extensions

fun String.toWords(): ArrayList<String> {
    val words = ArrayList<String>()
    for (w in this.replace("\'", "").replace(",", "").split(" ")) {
        if (w.isNotEmpty()) {
            words.add(w)
        }
    }
    return words
}