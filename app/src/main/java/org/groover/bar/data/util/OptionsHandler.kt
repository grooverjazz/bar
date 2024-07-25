package org.groover.bar.data.util

import android.content.Context

/**
 * Class responsible for app-wide options.
 */
class OptionsHandler(
    context: Context
) {
    private val fileOpener = FileOpener(context, "")
    val fileName = "options.csv"

    var sessionName = ""
    var beheerPassword = ""

    init {
        open()
    }

    // (Loads the option data)
    private fun open() {
        // Load data
        val dataDict = fileOpener.readToMap(fileName)

        // Deserialize
        sessionName = dataDict.getOrDefault("sessionName", "__default")
        beheerPassword = dataDict.getOrDefault("beheerPassword", "")
    }

    // (Changes the session name)
    fun changeSession(newSessionName: String) {
        // Change session name
        sessionName = newSessionName

        save()
    }

    // (Saves the option data)
    fun save() {
        // Serialize
        val dataStr = mapOf(
            "sessionName" to sessionName,
            "beheerPassword" to beheerPassword,
        )

        // Save data
        fileOpener.writeFromMap(fileName, dataStr)
    }

    // (Lists all session directories)
    fun getAllSessions(): List<String> = fileOpener.getSessionDirs()
}