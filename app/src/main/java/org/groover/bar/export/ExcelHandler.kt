package org.groover.bar.export

import org.apache.poi.ss.util.CellReference
import org.apache.poi.xssf.usermodel.XSSFCell
import org.apache.poi.xssf.usermodel.XSSFRow
import org.groover.bar.util.data.Cents

class ExcelHandler {
    data class ExcelFormula(val formula: String)

    companion object {
        fun writeRow(row: XSSFRow, values: List<Any>, startIndex: Int = 0) {
            values.forEachIndexed { index, s ->
                val cell = row.createCell(startIndex + index)
                writeCell(cell, s)
            }
        }

        fun writeCell(cell: XSSFCell, value: Any) {
            when (value) {
                is String -> cell.setCellValue(value)
                is Int -> cell.setCellValue(value.toDouble())
                is Boolean -> cell.setCellValue(if (value) 1.0 else 0.0)
                is Cents -> cell.setCellValue(value.stringWithoutEuro.replace(',','.').toDouble())
                is ExcelFormula -> cell.cellFormula = value.formula
            }
        }

        fun cellStr(row: Int, col: Int): String = CellReference(row, col).formatAsString()
    }
}