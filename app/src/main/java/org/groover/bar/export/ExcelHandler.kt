package org.groover.bar.export

import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.util.CellReference
import org.apache.poi.xssf.usermodel.XSSFCell
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.groover.bar.util.data.Cents
import org.groover.bar.util.data.Cents.Companion.toDouble


class ExcelHandler {
    data class ExcelFormula(val formula: String)

    data class AsCents(val value: Any)

    companion object {
        fun writeRows(sheet: XSSFSheet, values: List<List<Any>>, startRowIndex: Int) {
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

        fun writeCell(cell: XSSFCell, value: Any, style: XSSFCellStyle? = null) {
            if (style != null)
                cell.cellStyle = style

            when (value) {
                is String -> {
                    cell.setCellValue(value)
                    if (value.endsWith('%')) {

                        cell.setCellValue(value.dropLast(1).toDouble() / 100)

                        val stylePercentage: CellStyle = style?.copy() ?: cell.sheet.workbook.createCellStyle()
                        stylePercentage.dataFormat = 9
                        cell.setCellStyle(stylePercentage)
                    }
                }
                is Int -> cell.setCellValue(value.toDouble())
                is Double -> cell.setCellValue(value)
                is Boolean -> cell.setCellValue(if (value) 1.0 else 0.0)
                is Cents -> writeCell(cell, AsCents(value.toDouble()), style) // Hack
                is AsCents -> {
                    writeCell(cell, value.value, style)

                    // Set euro style
                    val styleEuro: CellStyle = style?.copy() ?: cell.sheet.workbook.createCellStyle()
                    styleEuro.dataFormat = 8
                    cell.setCellStyle(styleEuro)
                }
                is ExcelFormula -> cell.cellFormula = value.formula
            }
        }

        fun cellStr(row: Int, col: Int): String = CellReference(row, col).formatAsString()
    }
}