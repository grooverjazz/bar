package org.groover.bar.export

import org.groover.bar.util.data.CSV
import org.groover.bar.util.data.Cents

data class ExportRow (
    val id: Int,
    val voornaam: String,
    val tussenvoegsel: String,
    val achternaam: String,
    val amount: Cents
) {
    fun serialize(): List<String> {
        return listOf(
            id.toString(),
            voornaam,
            tussenvoegsel,
            achternaam,
        )
    }

    companion object {
        fun serialize(exportRow: ExportRow): String {
            return CSV.serialize(
                exportRow.id.toString(),
                exportRow.voornaam,
                exportRow.tussenvoegsel,
                exportRow.achternaam,
                exportRow.amount.stringWithoutEuro
            )
        }
    }
}