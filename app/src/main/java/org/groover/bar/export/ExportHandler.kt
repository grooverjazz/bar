package org.groover.bar.export

import android.content.Context
import org.groover.bar.data.group.Group
import org.groover.bar.data.group.GroupRepository
import org.groover.bar.data.item.ItemRepository
import org.groover.bar.data.member.Member
import org.groover.bar.data.member.MemberRepository
import org.groover.bar.data.order.OrderRepository
import org.groover.bar.util.data.CSV
import java.io.File

class ExportHandler(
    val context: Context,
    private val memberRepository: MemberRepository,
    private val groupRepository: GroupRepository,
    private val itemRepository: ItemRepository,
    private val orderRepository: OrderRepository,
) {
    // Get the current directory
    // (Android/data/org.groover.bartablet2/files)
    private val dir = context.getExternalFilesDir("")
    private fun getExportRows(): List<ExportRow> {
        val orders = orderRepository.data
        val items = itemRepository.data

        val totalAmounts = mutableMapOf<Int, Float>()

        for (order in orders) {
            // Calculate the price of the order
            val price = order.getTotalPrice(items)

            // Get the customer (Member or Group) that took the order
            val customerId = order.customerId
            val customer = (memberRepository.lookupById(customerId)
                ?: groupRepository.lookupById(customerId))!!

            // See if the order was done by a Member or a Group
            when (customer) {
                is Member -> {
                    // Add the single order to the temporary map
                    val member: Member = customer

                    val newPrice = totalAmounts.getOrDefault(member.id, 0f) + price
                    totalAmounts[member.id] = newPrice
                }
                is Group -> {
                    // Split the costs of the order and add to the export
                    val group: Group = customer
                    val splitPrice = price / group.memberIds.size

                    for (memberId in group.memberIds) {
                        val newPrice = totalAmounts.getOrDefault(memberId, 0f) + splitPrice
                        totalAmounts[memberId] = newPrice
                    }
                }
            }
        }

        // Map all total amounts to export rows
        val rows = totalAmounts.map { (memberId, totalAmount) ->
            val member = memberRepository.lookupById(memberId)!!

            ExportRow(
                member.id,
                member.voornaam,
                member.tussenvoegsel,
                member.achternaam,
                totalAmount
            )
        }

        // Return
        return rows
    }


    fun export() {
        val exportRows = getExportRows()

        // TODO: laat beheer dit bewerken
        val incassoName = "export_Bar"

        // Open a file for writing
        // TODO: opslaan in export-map
        val writeFile = File(dir, "$incassoName.csv")
        assert(!writeFile.exists()) { "Export bestaat al!" }
        writeFile.createNewFile()

        // Get title row string
        val titleRowStr = CSV.serialize(
            "id",
            "voornaam",
            "tussenvoegsel",
            "achternaam",
            incassoName
        )

        // Get data string
        val dataStr = exportRows
            .joinToString("\n", transform = ExportRow.Companion::serialize)

        // Write content to file
        val data = titleRowStr + "\n" + dataStr
        writeFile.writeText(data)
    }
}