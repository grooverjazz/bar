package org.groover.bar.export

import android.content.Context
import android.util.Log
import android.widget.Toast
import org.apache.poi.ss.util.CellReference
import org.apache.poi.xssf.usermodel.XSSFCell
import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.groover.bar.data.item.ItemRepository
import org.groover.bar.data.member.MemberRepository
import org.groover.bar.util.data.Cents
import org.groover.bar.util.data.FileOpener

class ExcelHandler(
    private val context: Context,
    private val fileOpener: FileOpener,
    private val memberRepository: MemberRepository,
    private val itemRepository: ItemRepository,
    private val exportHandler: ExportHandler,
) {
    fun export() {
        // Create a new workbook
        val workbook = XSSFWorkbook()

        handleOverzicht(workbook)
        handleBTW(workbook)
        handleIncasso(workbook)

        // Create a sheet in the workbook

        fileOpener.write("GroteExport.xlsx", workbook)

        workbook.close()

        Toast
            .makeText(context, "Excel geexporteerd!", Toast.LENGTH_SHORT)
            .show()
    }

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
        }
    }

    private fun cellStr(row: Int, col: Int): String = CellReference(row, col).formatAsString()

    private fun handleOverzicht(workbook: XSSFWorkbook) {
        val items = itemRepository.data
        val members = memberRepository.data
        val orderExport = exportHandler.getOrders()

        val sheet = workbook.createSheet("Overzicht")

        val pricesRow = sheet.createRow(7)
        writeRow(
            pricesRow,
            listOf("PRIJZEN") + items.map { it.price },
            4
        )

        val headerRow = sheet.createRow(8)
        writeRow(
            headerRow,
            listOf(
                "id",
                "voornaam",
                "tussenvoegsel",
                "achternaam",
                "aanwezig",
            ) + items.map { it.name },
            0
        )

        members.forEachIndexed { index, member ->
            val memberOrders: List<Int> = orderExport.memberOrders[member.id]!!
            val aanwezig = memberOrders.sum() > 0

            val rowIndex = index + 9

            val row = sheet.createRow(rowIndex)

            val values = listOf(
                member.id,
                member.roepVoornaam,
                member.tussenvoegsel,
                member.achternaam,
                aanwezig
            ) + memberOrders

            writeRow(
                row,
                values
            )

            row.createCell(values.size).cellFormula = "sumproduct(${cellStr(rowIndex, 5)}:${cellStr(rowIndex, values.size-1)}, ${cellStr(7, 5)}:${cellStr(7, values.size-1)})"
        }
    }

    private fun handleBTW(workbook: XSSFWorkbook) {
        val sheet = workbook.createSheet("BTW")

    }

    private fun handleIncasso(workbook: XSSFWorkbook) {
        val sheet = workbook.createSheet("Incasso")

    }

}