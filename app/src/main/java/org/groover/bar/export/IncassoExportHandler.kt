package org.groover.bar.export

import org.apache.poi.xssf.usermodel.XSSFSheet
import org.groover.bar.data.customer.CustomerRepository
import org.groover.bar.data.item.ItemRepository

class IncassoExportHandler(
    private val customerRepository: CustomerRepository,
    private val itemRepository: ItemRepository,
    private val sessionName: String
)  {
    fun export(sheet: XSSFSheet) {
        var currentRowIndex = 0

        val headerRow = sheet.createRow(currentRowIndex)
        ExcelHandler.writeRow(
            headerRow,
            listOf(
                "id",
                "voornaam",
                "tussenvoegsel",
                "achternaam",
                sessionName
            )
        )
        currentRowIndex += 1

        var refRowIndex = 6

        val members = customerRepository.members
        val itemsCount = itemRepository.data.size
        val groupsCount = customerRepository.groups.size
        for (member in members) {
            // Define reference to total in overzicht sheet
            val totalRefFormula = ExcelHandler.ExcelFormula("Overzicht!" + ExcelHandler.cellStr(refRowIndex, itemsCount + groupsCount + 5)) // TODO: fix bounds with new row width
            refRowIndex += 1

            // Skip extra members
            if (member.isExtra) continue

            val memberRow = sheet.createRow(currentRowIndex)
            ExcelHandler.writeRow(
                memberRow,
                listOf(
                    member.id,
                    member.name,
                    totalRefFormula,
                )
            )

            currentRowIndex += 1
        }
    }
}