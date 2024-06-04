package org.groover.bar.export

import android.content.Context
import android.util.Log
import org.groover.bar.util.data.FileOpener

class SessionHandler(
    context: Context
) {
    private val fileOpener = FileOpener(context, "")

    var sessionName = ""

    fun open() {
        // Load data
        val dataStr = fileOpener.read("session.csv")

        // Deserialize
        sessionName = dataStr.getOrElse(0) { "__default" }
    }

    fun changeSession(newSessionName: String) {
        // Change session name
        sessionName = newSessionName

        // Serialize
        val dataStr = listOf(sessionName)

        // Save data
        fileOpener.write("session.csv", dataStr)
    }

    fun getAllSessions(): List<String> = fileOpener.listDirs()
}