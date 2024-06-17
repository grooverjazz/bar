package org.groover.bar.export

import org.apache.poi.xssf.usermodel.XSSFSheet
import org.groover.bar.data.group.GroupRepository
import org.groover.bar.data.item.ItemRepository
import org.groover.bar.data.member.MemberRepository
import org.groover.bar.data.order.OrderRepository

class OverzichtExportHandler(
    private val memberRepository: MemberRepository,
    private val groupRepository: GroupRepository,
    private val itemRepository: ItemRepository,
    private val orderRepository: OrderRepository,
) {
    private fun getOrders(): Map<Int, List<Int>> {
        val orders = orderRepository.data
        val items = itemRepository.data
        val members = memberRepository.data
        val groups = groupRepository.data

        // Initialize all amounts
        val allAmounts: MutableMap<Int, MutableList<Int>> = (members + groups).associate { data ->
            data.id to MutableList(items.size) { 0 }
        }.toMutableMap()

        orders.forEach { order ->
            // Get the customer (Member or Group) that took the order
            val customerId = order.customerId

            // Count the order amount for each item in allAmounts
            items.forEachIndexed { index, item ->
                allAmounts[customerId]!![index] += order.getAmount(item.id)
            }
        }

        return allAmounts
    }

    fun export(sheet: XSSFSheet) {
        val members = memberRepository.data
        val groups = groupRepository.data
        val items = itemRepository.data
        val orderExport = getOrders()

        var currentRowIndex = 4

        val pricesRow = sheet.createRow(currentRowIndex)
        ExcelHandler.writeRow(
            pricesRow,
            listOf("PRIJZEN") + items.map { it.price },
            startIndex = 4
        )
        val pricesRangeStr = "${ExcelHandler.cellStr(currentRowIndex, 5)}:${ExcelHandler.cellStr(currentRowIndex, 4 + items.size)}"
        currentRowIndex += 1

        val memberHeaderRow = sheet.createRow(currentRowIndex)
        ExcelHandler.writeRow(
            memberHeaderRow,
            listOf(
                "id",
                "voornaam",
                "tussenvoegsel",
                "achternaam",
                "aanwezig",
            ) + items.map { it.name }
        )
        currentRowIndex += 1

        members.forEach { member ->
            val memberOrders: List<Int> = orderExport[member.id]!!
            val aanwezig = if (memberOrders.sum() > 0) "ja" else "nee"

            val row = sheet.createRow(currentRowIndex)

            val values = listOf(
                member.id,
                member.roepVoornaam,
                member.tussenvoegsel,
                member.achternaam,
                aanwezig
            ) + memberOrders

            ExcelHandler.writeRow(
                row,
                values
            )

            // Add total
            val ordersRangeStr = "${ExcelHandler.cellStr(currentRowIndex, 5)}:${ExcelHandler.cellStr(currentRowIndex, values.size - 1)}"
            row.createCell(values.size).cellFormula = "sumproduct($pricesRangeStr, $ordersRangeStr)"

            currentRowIndex += 1
        }

        currentRowIndex += 2

        val groupHeaderRow = sheet.createRow(currentRowIndex)
        ExcelHandler.writeRow(
            groupHeaderRow,
            listOf(
                "id",
                "naam",
                "aanwezig"
            ) + items.map { it.name },
            startIndex = 2
        )
        currentRowIndex += 1
        groups.forEach { group ->
            val groupOrders: List<Int> = orderExport[group.id]!!
            val aanwezig = if (groupOrders.sum() > 0) "ja" else "nee"

            val row = sheet.createRow(currentRowIndex)

            // Define total
            val ordersRangeStr = "${ExcelHandler.cellStr(currentRowIndex, 5)}:${ExcelHandler.cellStr(currentRowIndex, 5 + groupOrders.size)}"
            val totalFormula = ExcelHandler.ExcelFormula("sumproduct($pricesRangeStr, $ordersRangeStr)")

            val values = listOf(
                group.id,
                group.name,
                aanwezig,
                totalFormula,
            ) + groupOrders

            ExcelHandler.writeRow(
                row,
                values,
                startIndex = 2
            )

            currentRowIndex += 1
        }
    }
}