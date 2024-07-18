package org.groover.bar.export

import androidx.compose.ui.util.fastMap
import org.apache.poi.ss.util.CellReference
import org.apache.poi.xssf.usermodel.XSSFCell
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.groover.bar.util.data.Cents
import org.groover.bar.util.data.Cents.Companion.toDouble


class ExcelHandler {
    data class ExcelFormula(val formula: String)

    data class WithStyle(val value: Any, val style: XSSFCellStyle)

    companion object {
        fun List<Any>.withStyle(style: XSSFCellStyle) = this.fastMap { it.withStyle(style) }
        fun Any.withStyle(style: XSSFCellStyle) = WithStyle(this, style)
        fun List<Any>.withStyles(styles: List<XSSFCellStyle>) = (this zip styles).fastMap { (value, style) -> value.withStyle(style) }

        fun writeRows(sheet: XSSFSheet, values: List<List<Any>>, startRowIndex: Int = 0) {
            values.forEachIndexed { index, rowValues ->
                val row = sheet.createRow(index + startRowIndex)
                writeRow(row, rowValues)
            }
        }

        fun writeRow(row: XSSFRow, values: List<Any>, startIndex: Int = 0, style: XSSFCellStyle? = null) {
            values.forEachIndexed { index, value ->
                val cell = row.createCell(startIndex + index)
                writeCell(cell, value, style)
            }
        }

        private fun writeCell(cell: XSSFCell, value: Any, style: XSSFCellStyle? = null) {
            if (style != null)
                cell.cellStyle = style

            when (value) {
                // Special cases
                is WithStyle -> writeCell(cell, value.value, value.style)
                is ExcelFormula -> cell.cellFormula = value.formula

                is String -> if (value.isNotEmpty()) cell.setCellValue(value)
                is Int -> cell.setCellValue(value.toDouble())
                is Double -> cell.setCellValue(value)
                is Boolean -> cell.setCellValue(if (value) 1.0 else 0.0)
                is Cents -> cell.setCellValue(value.toDouble())
            }
        }

        fun cellStr(row: Int, col: Int): String = CellReference(row, col).formatAsString()
    }
}