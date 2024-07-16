package org.groover.bar.export

import androidx.compose.ui.util.fastMapIndexed
import org.apache.poi.ss.usermodel.Color
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.Font
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.xssf.usermodel.XSSFColor
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.groover.bar.data.customer.CustomerRepository
import org.groover.bar.data.item.ItemRepository
import org.groover.bar.data.order.OrderRepository
import org.groover.bar.export.ExcelHandler.AsCents
import org.groover.bar.export.ExcelHandler.Companion.cellStr
import org.groover.bar.export.ExcelHandler.Companion.writeRow
import org.groover.bar.export.ExcelHandler.ExcelFormula
import org.groover.bar.util.data.removeFirst


class OverzichtExportHandler(
    private val customerRepository: CustomerRepository,
    private val itemRepository: ItemRepository,
    private val orderRepository: OrderRepository,
) {
    private fun getOrders(): Map<Int, List<Int>> {
        val orders = orderRepository.data
        val items = itemRepository.data
        val customers = customerRepository.data

        // Initialize all amounts
        val allAmounts: MutableMap<Int, MutableList<Int>> = customers.associate { data ->
            data.id to MutableList(items.size) { 0 }
        }.toMutableMap()

        orders.forEach { order ->
            // Get the customer (Member or Group) that took the order
            val customerId = order.customerId

            // Count the order amount for each item in allAmounts
            items.forEachIndexed { index, item ->
                allAmounts[customerId]!![index] += order.amounts[item.id]
            }
        }

        return allAmounts
    }

    private fun groupMemberRowSum(rowIndex: Int, memberCount: Int, groupCount: Int): Pair<String, String> {
        val memberStartCell = cellStr(7, rowIndex)
        val memberEndCell = cellStr(7 + memberCount - 1, rowIndex)

        val groupStartCell = cellStr(7 + memberCount - 1 + 4, rowIndex)
        val groupEndCell = cellStr(7 + memberCount - 1 + 4 + groupCount - 1, rowIndex)

        return Pair("$memberStartCell:$memberEndCell", "$groupStartCell:$groupEndCell")
    }


    fun export(sheet: XSSFSheet) {
        val workbook = sheet.workbook

        // Create bold style
        val boldFont: Font = workbook.createFont()
        boldFont.bold = true
        val boldStyle = workbook.createCellStyle()
        boldStyle.setFont(boldFont)

        // Create align styles
        val leftAlignStyle = workbook.createCellStyle()
        leftAlignStyle.alignment = HorizontalAlignment.LEFT
        val rightAlignBoldStyle = workbook.createCellStyle()
        rightAlignBoldStyle.alignment = HorizontalAlignment.RIGHT
        rightAlignBoldStyle.setFont(boldFont)

        // Reorder members such that Hospitality is on top
        val members = listOf(customerRepository.members.find(0)!!) + customerRepository.members.data.removeFirst { it.id == 0 }
        val groups = customerRepository.groups.data
        val items = itemRepository.data
        val orderExport = getOrders()

        var currentRowIndex = 0

        // Leden table
        val aantalLedenValues = listOf(
            "aantal leden",
            members.size,
        )
        val aantalExtraLedenValues = listOf(
            "aantal verzonnen leden",
            members.count { it.isExtra },
        )
        val aantalAanwezigeLedenValues = listOf(
            "aantal aanwezige leden",
            groupMemberRowSum(2, members.size, groups.size).let { (memberSumStr, _) ->
                ExcelFormula("countif($memberSumStr, \"ja\")")
            },
        )
        val totaleOmzetValues = listOf(
            "totale omzet",
            groupMemberRowSum(3 + items.size + groups.size, members.size, groups.size).let { (memberSumStr, _) ->
                AsCents(ExcelFormula("sum($memberSumStr)"))
            }
        )

        // First 5 rows (member and item table)
        ExcelHandler.writeRows(
            sheet,
            listOf(
                aantalLedenValues + listOf("") + items.map { it.name },
                aantalExtraLedenValues + listOf("PRIJS") + items.map { it.price },
                aantalAanwezigeLedenValues + listOf("AFZET") + List(items.size) { index ->
                        groupMemberRowSum(3 + index, members.size, groups.size).let { (memberSumStr, groupSumStr) ->
                        ExcelFormula("sum($memberSumStr, $groupSumStr)")
                    }
                },
                totaleOmzetValues + listOf("OMZET") + List(items.size) { index ->
                    val prijsCell = cellStr(1, 3 + index)
                    val afzetCell = cellStr(2, 3 + index)
                    AsCents(ExcelFormula("$afzetCell * $prijsCell"))
                },
                listOf("", "", "BTW") + items.map { "${it.btwPercentage}%" },
            ),
            0
        )

        // Make member stats align to the left
        sheet.getRow(0).getCell(1).cellStyle = leftAlignStyle
        sheet.getRow(1).getCell(1).cellStyle = leftAlignStyle
        sheet.getRow(2).getCell(1).cellStyle = leftAlignStyle
        sheet.getRow(3).getCell(1).cellStyle.alignment = HorizontalAlignment.LEFT

        // Make item names bold
        val firstRow = sheet.getRow(0)
        items.indices.forEach { index -> firstRow.getCell(3 + index).cellStyle = boldStyle }

        // Define range of prices (used in sumproduct of total)
        val pricesRangeStr = "${cellStr(currentRowIndex + 1, 3)}:${cellStr(currentRowIndex + 1, 2 + items.size)}"
        currentRowIndex += 5

        // Members title
        writeRow(
            sheet.createRow(currentRowIndex),
            listOf("LEDEN"),
            style = boldStyle,
        )
        currentRowIndex += 1

        // Members header
        writeRow(
            sheet.createRow(currentRowIndex),
            listOf("id", "naam", "aanwezig") + items.map { it.name } + groups.map { it.name } + listOf("OMZET"),
            style = boldStyle,
        )
        sheet.getRow(currentRowIndex).getCell(0).cellStyle = rightAlignBoldStyle
        currentRowIndex += 1

        // Members
        members.forEachIndexed { index, member ->
            val memberOrders: List<Any> = orderExport[member.id]!!.map { if (it == 0) "" else it }

            // Create formula for presence
            val totalCell = cellStr(currentRowIndex, 3 + items.size + groups.size)
            val aanwezig = ExcelFormula("if($totalCell,\"ja\",\"nee\")")

            // Create formula for total
            val ordersRangeStr = "${cellStr(currentRowIndex, 3)}:${cellStr(currentRowIndex, 3 + items.size - 1)}"
            val groupRangeStr = if (groups.isEmpty()) "0" else
                "${cellStr(currentRowIndex, 3 + items.size)}:${cellStr(currentRowIndex, 3 + items.size + groups.size - 1)}"
            val total = AsCents(ExcelFormula("sumproduct($pricesRangeStr, $ordersRangeStr) + sum($groupRangeStr)"))

            // Get all group orders
            val memberGroupOrders = groups.fastMapIndexed { index, group ->
                if (!group.memberIds.contains(member.id)) "" else {
                    val groupCellStr = cellStr(7 + members.size + 3 + index, 3 + items.size)
                    ExcelFormula("$groupCellStr / ${group.memberIds.size}")
                }
            }

            // Get row color
            val rgb: ByteArray?
            if (member.id == 0) rgb = byteArrayOf(242.toByte(), 206.toByte(), 239.toByte())
            else if (member.isExtra) rgb = byteArrayOf(193.toByte(), 240.toByte(), 200.toByte())
            else if (index % 2 == 0) rgb = byteArrayOf(217.toByte(), 217.toByte(), 217.toByte())
            else rgb = null

            // Apply to style
            val style: XSSFCellStyle?
            if (rgb != null) {
                val color = XSSFColor()
                color.rgb = rgb

                style = workbook.createCellStyle()
                style.setFillForegroundColor(color)
                style.setFillPattern(FillPatternType.SOLID_FOREGROUND)
            }
            else style = null

            // Write row
            writeRow(
                sheet.createRow(currentRowIndex),
                listOf(member.id, member.name, aanwezig)
                        + memberOrders
                        + memberGroupOrders.map(ExcelHandler::AsCents)
                        + listOf(total),
                style = style
            )

            currentRowIndex += 1
        }

        // Empty row
        currentRowIndex += 1

        // Groups title
        writeRow(
            sheet.createRow(currentRowIndex),
            listOf("GROEPEN"),
            style = boldStyle,
        )
        currentRowIndex += 1

        // Groups header
        writeRow(
            sheet.createRow(currentRowIndex),
            listOf("id", "naam", "") + items.map { it.name } + listOf("OMZET"),
            style = boldStyle,
        )
        sheet.getRow(currentRowIndex).getCell(0).cellStyle = rightAlignBoldStyle
        currentRowIndex += 1

        // Groups
        groups.forEach { group ->
            val groupOrders: List<Int> = orderExport[group.id]!!

            // Create formula for total
            val ordersRangeStr = "${cellStr(currentRowIndex, 3)}:${cellStr(currentRowIndex, 3 + items.size - 1)}"
            val total = AsCents(ExcelFormula("sumproduct($pricesRangeStr, $ordersRangeStr)"))

            // Write row
            writeRow(
                sheet.createRow(currentRowIndex),
                listOf(group.id, group.name)
                        + listOf("")
                        + groupOrders
                        + listOf(total),
            )

            currentRowIndex += 1
        }
    }
}