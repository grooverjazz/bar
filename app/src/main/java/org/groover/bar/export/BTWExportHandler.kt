package org.groover.bar.export

import org.apache.poi.xssf.usermodel.XSSFSheet
import org.groover.bar.data.group.GroupRepository
import org.groover.bar.data.item.ItemRepository
import org.groover.bar.data.member.MemberRepository
import org.groover.bar.data.order.OrderRepository
import org.groover.bar.util.data.Cents

class BTWExportHandler(
    private val itemRepository: ItemRepository,
    private val orderRepository: OrderRepository,
)  {
    private fun getBtwAmounts(): Map<Int, Cents> {
        val orders = orderRepository.data
        val items = itemRepository.data

        // From percentage to total spent amount
        val amounts = mutableMapOf<Int, Cents>()

        for (item in items) {
            // Get the total price of all orders
            val itemId = item.id
            val price = item.price
            val totalItemPrice = Cents(orders.sumOf { order ->
                order.getAmount(itemId) * price.amount
            })

            val btwPercentage = item.btwPercentage
            val newAmount = amounts.getOrDefault(btwPercentage, Cents(0)) + totalItemPrice
            amounts[btwPercentage] = newAmount
        }

        return amounts
    }

    fun export(sheet: XSSFSheet) {
        var currentRowIndex = 0

        val headerRow = sheet.createRow(currentRowIndex)
        ExcelHandler.writeRow(
            headerRow,
            listOf(
                "BTW-percentage",
                "Afzet met dit percentage"
            )
        )
        currentRowIndex += 1

        val btwAmounts = getBtwAmounts()
        btwAmounts.forEach { (percentage, amount) ->
            val amountRow = sheet.createRow(currentRowIndex)
            ExcelHandler.writeRow(
                amountRow,
                listOf(
                    percentage,
                    amount
                )
            )

            currentRowIndex += 1
        }
    }
}