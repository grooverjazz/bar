package org.groover.bar.export

import android.content.Context
import org.groover.bar.data.item.ItemRepository
import org.groover.bar.data.order.OrderRepository
import org.groover.bar.util.data.CSV
import org.groover.bar.util.data.Cents
import java.io.File

class BTWHandler(
    val context: Context,
    private val orderRepository: OrderRepository,
    private val itemRepository: ItemRepository,
) {
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
                CSV.serialize(btwAmount.toString(), total.stringWithoutEuro)
            }

        // Write content to file
        val data = titleRowStr + "\n" + dataStr
        writeFile.writeText(data)
    }
}