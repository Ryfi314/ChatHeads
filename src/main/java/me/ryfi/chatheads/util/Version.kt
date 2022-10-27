package me.ryfi.chatheads.util

import me.ryfi.chatheads.ChatHeads
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

object Version {

    private var VERSION = "1.4"

    private var url = URL("${ChatHeads.REPO_URL}/version.txt")


    var hasUpdates = false

    fun checkForUpdates() {
        val http: HttpURLConnection = url.openConnection() as HttpURLConnection
        http.disconnect()

        val responseVersion = BufferedReader(InputStreamReader(http.inputStream)).readText()

        hasUpdates = VERSION != responseVersion

    }

    @JvmStatic
    fun hasUpdates(): Boolean = hasUpdates

}