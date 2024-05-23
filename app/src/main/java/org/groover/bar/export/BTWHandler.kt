package org.groover.bar.export

import android.content.Context
import org.groover.bar.data.item.ItemRepository
import org.groover.bar.data.order.OrderRepository
import org.groover.bar.util.data.CSV
import java.io.File

class BTWHandler(
    val context: Context,
    private val orderRepository: OrderRepository,
    private val itemRepository: ItemRepository,
) {
    private fun getBtwAmounts(): Map<Int, Int> {
        val orders = orderRepository.data
        val items = itemRepository.data

        // From percentage to total spent amount
        val amounts = mutableMapOf<Int, Int>()

        for (item in items) {
            // Get the total price of all orders
            val itemId = item.id
            val price = item.price
            val totalItemPrice = orders.sumOf { order ->
                order.getAmount(itemId) * price
            }

            val btwPercentage = item.btwPercentage
            val newAmount = amounts.getOrDefault(btwPercentage, 0) + totalItemPrice
            amounts[btwPercentage] = newAmount
        }

        return amounts.toMap()
    }

    fun export() {
        val btwAmounts = getBtwAmounts()

        // TODO: in sessie-mapje
        val fileName = "export_BTW"

        // Get the current directory
        // (Android/data/org.groover.bar/files)
        val dir = context.getExternalFilesDir("")

        // Open a file for writing
        // TODO: opslaan in export-map
        val writeFile = File(dir, "$fileName.csv")
        assert(!writeFile.exists()) { "Export bestaat al!" }
        writeFile.createNewFile()

        // Get title row string
        val titleRowStr = CSV.serialize(
            "BTW-percentage",
            "Totale uitgaven met dit percentage"
        )

        // Get data string
        val dataStr = btwAmounts.toList()
            .joinToString("\n") { (btwAmount, total) ->
                val totalStr = "${total / 100},${"%02d".format(total % 100)}"

                CSV.serialize(btwAmount.toString(), totalStr)
            }

        // Write content to file
        val data = titleRowStr + "\n" + dataStr
        writeFile.writeText(data)
    }
}