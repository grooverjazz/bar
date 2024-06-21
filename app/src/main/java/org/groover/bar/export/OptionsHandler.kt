package org.groover.bar.export

import android.content.Context
import org.groover.bar.util.data.FileOpener

class OptionsHandler(
    context: Context
) {
    private val fileOpener = FileOpener(context, "")

    var sessionName = ""
    var beheerPassword = ""

    fun open() {
        // Load data
        val dataStr = fileOpener.read("session.csv")

        // Deserialize
        sessionName = dataStr.getOrElse(0) { "__default" }
        beheerPassword = dataStr.getOrElse(1) { "" }
    }

    fun changeSession(newSessionName: String) {
        // Change session name
        sessionName = newSessionName

        save()
    }

    fun save() {
        // Serialize
        val dataStr = listOf(
            sessionName,
            beheerPassword,
        )

        // Save data
        fileOpener.write("session.csv", dataStr)
    }

    fun getAllSessions(): List<String> = fileOpener.listDirs().sortedDescending()
}