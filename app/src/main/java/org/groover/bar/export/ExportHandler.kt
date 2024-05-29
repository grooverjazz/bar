package org.groover.bar.export

import android.content.Context
import org.groover.bar.data.group.Group
import org.groover.bar.data.group.GroupRepository
import org.groover.bar.data.item.ItemRepository
import org.groover.bar.data.member.Member
import org.groover.bar.data.member.MemberRepository
import org.groover.bar.data.order.OrderRepository
import org.groover.bar.util.data.CSV
import org.groover.bar.util.data.Cents
import java.io.File

class ExportHandler(
    val context: Context,
    private val memberRepository: MemberRepository,
    private val groupRepository: GroupRepository,
    private val itemRepository: ItemRepository,
    private val orderRepository: OrderRepository,
) {
    private fun getExportRows(): List<ExportRow> {
        val orders = orderRepository.data
        val items = itemRepository.data

        val totalAmounts = mutableMapOf<Int, Cents>()

        for (order in orders) {
            // Calculate the price of the order
            val price = order.getTotalPrice(items)

            // Get the customer (Member or Group) that took the order
            val customerId = order.customerId
            val customer = (memberRepository.lookupById(customerId)
                ?: groupRepository.lookupById(customerId))
                ?: throw Exception("Kan gebruiker niet vinden!")

            // See if the order was done by a Member or a Group
            when (customer) {
                is Member -> {
                    // Add the single order to the temporary map
                    val member: Member = customer

                    val newPrice = totalAmounts.getOrDefault(member.id, Cents(0)) + price
                    totalAmounts[member.id] = newPrice
                }
                is Group -> {
                    // Split the costs of the order and add to the export
                    val group: Group = customer
                    val splitPrices = Cents.split(price, group.memberIds.size)

                    for ((memberId, splitPrice) in group.memberIds.zip(splitPrices)) {
                        val newPrice = totalAmounts.getOrDefault(memberId, Cents(0)) + splitPrice
                        totalAmounts[memberId] = newPrice
                    }
                }
            }
        }

        // Map all total amounts to export rows
        val rows = totalAmounts.map { (memberId, totalAmount) ->
            val member = memberRepository.lookupById(memberId) ?: throw Exception("Kan lid met ID $memberId niet vinden!")

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

        // TODO: in sessie-mapje
        val fileName = "export_Incasso"

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
            "id",
            "voornaam",
            "tussenvoegsel",
            "achternaam",
            fileName
        )

        // Get data string
        val dataStr = exportRows
            .joinToString("\n", transform = ExportRow.Companion::serialize)

        // Write content to file
        val data = titleRowStr + "\n" + dataStr
        writeFile.writeText(data)
    }
}