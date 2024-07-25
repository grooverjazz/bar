package org.groover.bar.export

import org.apache.poi.xssf.usermodel.XSSFSheet
import org.groover.bar.data.customer.CustomerRepository
import org.groover.bar.data.item.ItemRepository
import org.groover.bar.export.ExcelHandler.Companion.cellStr
import org.groover.bar.export.ExcelHandler.Companion.withStyle

/**
 * Class responsible for exporting the 'Incasso' sheet.
 */
class IncassoExportHandler(
    private val styleManager: StyleManager,
    private val updateProgress: (Float) -> Unit,
    customerRepository: CustomerRepository,
    itemRepository: ItemRepository,
    private val sessionName: String
)  {
    private val members = customerRepository.members.data
    private val membersCount = members.size

    private val itemsCount = itemRepository.data.size

    private val groupsCount = customerRepository.groups.data.size

    // (Exports the sheet)
    fun export(sheet: XSSFSheet) {
        var currentRowIndex = 0

        // Create styles
        val boldStyle = styleManager.getStyle(bold = true)
        val currencyStyle = styleManager.getStyle(format = StyleManager.StyleFormat.Currency)

        // Header row
        val headerRow = sheet.createRow(currentRowIndex)
        ExcelHandler.writeRow(
            headerRow,
            listOf(
                "id",
                "naam",
                sessionName,
            ),
            style = boldStyle,
        )
        currentRowIndex += 1

        // Incasso
        var overzichtRowIndex = 7
        for ((index, member) in members.withIndex()) {
            // Skip extra members
            if (member.isExtra) {
                overzichtRowIndex += 1
                continue
            }

            // Define reference to total in overzicht sheet
            val referenceCell = "Overzicht!${cellStr(overzichtRowIndex, itemsCount + groupsCount + 3)}"
            val totalRefFormula = ExcelHandler.ExcelFormula(referenceCell)

            // Write values
            val memberRow = sheet.createRow(currentRowIndex)
            ExcelHandler.writeRow(
                memberRow,
                listOf(
                    member.id,
                    member.name,
                    totalRefFormula.withStyle(currencyStyle),
                )
            )

            currentRowIndex += 1
            overzichtRowIndex += 1
            updateProgress(index.toFloat() / membersCount)
        }
    }
}