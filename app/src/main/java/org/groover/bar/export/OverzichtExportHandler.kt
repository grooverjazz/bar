package org.groover.bar.export

import org.apache.poi.xssf.usermodel.XSSFSheet
import org.groover.bar.data.item.ItemRepository
import org.groover.bar.data.customer.CustomerRepository
import org.groover.bar.data.order.OrderRepository

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
                allAmounts[customerId]!![index] += order.getAmount(item.id)
            }
        }

        return allAmounts
    }

    private fun groupMemberRowSum(rowIndex: Int, memberCount: Int, groupCount: Int): Pair<String, String> {
        val memberStartCell = ExcelHandler.cellStr(6, rowIndex)
        val memberEndCell = ExcelHandler.cellStr(6 + memberCount - 1, rowIndex)

        val groupStartCell = ExcelHandler.cellStr(6 + memberCount + 2 + 1, rowIndex)
        val groupEndCell = ExcelHandler.cellStr(6 + memberCount + 2 + groupCount + 1 - 1, rowIndex)

        return Pair("$memberStartCell:$memberEndCell", "$groupStartCell:$groupEndCell")
    }

    fun export(sheet: XSSFSheet) {
        val members = customerRepository.members.data
        val groups = customerRepository.groups.data
        val items = itemRepository.data
        val orderExport = getOrders()

        var currentRowIndex = 0

        // Leden table
        val aantalLedenValues = listOf(
            "aantal leden",
            members.size
        )
        val aantalVerzonnenLedenValues = listOf(
            "aantal verzonnen leden",
            members.count { it.isExtra }
        )
        val aantalAanwezigeLedenValues = listOf(
            "aantal aanwezige leden",
            groupMemberRowSum(4, members.size, groups.size).let { (memberSumStr, groupSumStr) ->
                ExcelHandler.ExcelFormula("countif($memberSumStr, \"ja\") + countif($groupSumStr, \"nee\")")
            }
        )

        // Afzet row
        ExcelHandler.writeRow(
            sheet.createRow(currentRowIndex),
            listOf(
                "", "", "", "",
                "AFZET",
            ) + List(items.size) { index ->
                groupMemberRowSum(5 + index, members.size, groups.size).let { (memberSumStr, groupSumStr) ->
                    ExcelHandler.ExcelFormula("sum($memberSumStr, $groupSumStr)")
                }
            }
        )
        currentRowIndex += 1

        // Omzet row
        ExcelHandler.writeRow(
            sheet.createRow(currentRowIndex),
            listOf("")
            + aantalLedenValues
            + listOf(
                "",
                "OMZET"
            )
            + List(items.size) { index ->
                val afzetCell = ExcelHandler.cellStr(0, 5 + index)
                val prijsCell = ExcelHandler.cellStr(4, 5 + index)
                ExcelHandler.ExcelFormula("$afzetCell * $prijsCell")
            }
        )
        currentRowIndex += 1

        // Verzonnen leden row
        ExcelHandler.writeRow(
            sheet.createRow(currentRowIndex),
            listOf("")
            + aantalVerzonnenLedenValues
        )
        currentRowIndex += 1

        // BTW row
        ExcelHandler.writeRow(
            sheet.createRow(currentRowIndex),
            listOf("")
            + aantalAanwezigeLedenValues
            + listOf(
                "",
                "BTW",
            )
            + items.map { it.btwPercentage }
        )
        currentRowIndex += 1

        // Define range of prices (used in sumproduct of total)
        val pricesRangeStr = "${ExcelHandler.cellStr(currentRowIndex, 5)}:${ExcelHandler.cellStr(currentRowIndex, 4 + items.size)}"
        // Prices row
        ExcelHandler.writeRow(
            sheet.createRow(currentRowIndex),
            listOf("PRIJZEN")
            + items.map { it.price },
            startIndex = 4
        )
        currentRowIndex += 1

        // Member header row
        ExcelHandler.writeRow(
            sheet.createRow(currentRowIndex),
            listOf(
                "id",
                "naam",
                "aanwezig",
            ) + items.map { it.name }
            + groups.map { it.name }
            + listOf("OMZET"),
        )
        currentRowIndex += 1

        // Members
        members.forEach { member ->
            val memberOrders: List<Int> = orderExport[member.id]!!

            // Get range of items
            val rowWidth = 3 + memberOrders.size
            val ordersRangeStr = "${ExcelHandler.cellStr(currentRowIndex, 5)}:${ExcelHandler.cellStr(currentRowIndex, rowWidth - 1)}"
            val groupRangeStr = "${ExcelHandler.cellStr(currentRowIndex, rowWidth)}:${ExcelHandler.cellStr(currentRowIndex, rowWidth + groups.size - 1)}"

            // Create formulas
            val aanwezig = ExcelHandler.ExcelFormula("if(sum($ordersRangeStr),\"ja\",\"nee\")")
            val total = ExcelHandler.ExcelFormula("sumproduct($pricesRangeStr, $ordersRangeStr) + sum($groupRangeStr)")

            val memberGroupOrders = groups.mapIndexed { index, group ->
                if (group.memberIds.contains(member.id)) { // TODO: fix bounds with new row width
                    val str = ExcelHandler.cellStr(6 + members.size + 2 + 1 + index, 4 + items.size + 1)
                    ExcelHandler.ExcelFormula("$str / ${group.memberIds.size}")
                }
                else {
                    ""
                }
            }

            // Write row
            ExcelHandler.writeRow(
                sheet.createRow(currentRowIndex),
                listOf(
                    member.id,
                    member.name,
                    aanwezig
                )
                + memberOrders
                + memberGroupOrders
                + listOf(total)
            )

            currentRowIndex += 1
        }
        currentRowIndex += 2

        // Group header row
        ExcelHandler.writeRow(
            sheet.createRow(currentRowIndex),
            listOf(
                "id",
                "naam",
                "aanwezig"
            ) + items.map { it.name }
            + listOf("OMZET"),
            startIndex = 2
        )
        currentRowIndex += 1

        // Groups
        groups.forEach { group ->
            val groupOrders: List<Int> = orderExport[group.id]!!

            // Get range of items
            val rowWidth = 5 + groupOrders.size
            val ordersRangeStr = "${ExcelHandler.cellStr(currentRowIndex, 5)}:${ExcelHandler.cellStr(currentRowIndex, rowWidth - 1)}"

            // Create formulas
            val aanwezig = ExcelHandler.ExcelFormula("if(sum($ordersRangeStr),\"ja\",\"nee\")")
            val total = ExcelHandler.ExcelFormula("sumproduct($pricesRangeStr, $ordersRangeStr)")

            // Write row
            ExcelHandler.writeRow(
                sheet.createRow(currentRowIndex),
                listOf(
                    group.id,
                    group.name,
                    aanwezig,
                )
                + groupOrders
                + listOf(total),
                startIndex = 2
            )

            currentRowIndex += 1
        }
    }
}