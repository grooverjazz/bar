package org.groover.bar.export

import org.apache.poi.xssf.usermodel.XSSFSheet
import org.groover.bar.data.item.ItemRepository
import org.groover.bar.export.ExcelHandler.Companion.cellStr
import org.groover.bar.export.ExcelHandler.Companion.withStyle
import org.groover.bar.util.data.BTWPercentage

class BTWExportHandler(
    private val styleManager: StyleManager,
    private val itemRepository: ItemRepository,
)  {
    fun export(sheet: XSSFSheet) {
        val itemsCount = itemRepository.data.size
        var currentRowIndex = 0

        // Create styles
        val boldStyle = styleManager.getStyle(bold = true)
        val currencyStyle = styleManager.getStyle(format = StyleManager.StyleFormat.Currency)

        // Header row
        val headerRow = sheet.createRow(currentRowIndex)
        ExcelHandler.writeRow(
            headerRow,
            listOf(
                "BTW-percentage",
                "Omzet met dit percentage",
            ),
            style = boldStyle,
        )
        currentRowIndex += 1

        // Go through all BTW amounts
        BTWPercentage.entries.forEach { percentage ->
            // Create formulas
            val overzichtBtwRange   = "Overzicht!${cellStr(4, 3)}:${cellStr(4, 3 + itemsCount - 1)}"
            val overzichtOmzetRange = "Overzicht!${cellStr(3, 3)}:${cellStr(3, 3 + itemsCount - 1)}"
            val amountFormula = ExcelHandler.ExcelFormula("sumif($overzichtBtwRange, ${percentage.value}%, $overzichtOmzetRange)")

            ExcelHandler.writeRow(
                sheet.createRow(currentRowIndex),
                listOf(
                    percentage.toString(),
                    amountFormula.withStyle(currencyStyle)
                )
            )

            currentRowIndex += 1
        }
    }
}