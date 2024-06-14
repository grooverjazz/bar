package org.groover.bar.export

import android.content.Context
import android.util.Log
import android.widget.Toast
import org.apache.commons.lang3.mutable.Mutable
import org.groover.bar.data.group.Group
import org.groover.bar.data.group.GroupRepository
import org.groover.bar.data.item.ItemRepository
import org.groover.bar.data.member.Member
import org.groover.bar.data.member.MemberRepository
import org.groover.bar.data.order.Order
import org.groover.bar.data.order.OrderRepository
import org.groover.bar.util.data.CSV
import org.groover.bar.util.data.Cents
import org.groover.bar.util.data.FileOpener
import java.io.File
import java.util.Dictionary

class OrderExport(
    private val memberRepository: MemberRepository,
    private val groupRepository: GroupRepository,
    private val itemRepository: ItemRepository,
) {
    val memberOrders: MutableMap<Int, MutableList<Int>>
    val groupOrders: MutableMap<Int, MutableList<Int>>

    init {
        val itemAmount = itemRepository.data.size
        Log.i("ExportHandler", itemAmount.toString())

        memberOrders = memberRepository.data.associate { member ->
            member.id to MutableList(itemAmount) { 0 }
        }.toMutableMap()

        groupOrders = groupRepository.data.associate { group ->
            group.id to MutableList(itemAmount) { 0 }
        }.toMutableMap()
    }

    fun addOrder(index: Int, order: Order) {
        val itemsCount = itemRepository.data.size

        // Get the customer (Member or Group) that took the order
        val customerId = order.customerId
        val customer = (memberRepository.lookupById(customerId)
            ?: groupRepository.lookupById(customerId))
            ?: throw Exception("Kan gebruiker niet vinden!")

        // See if the order was done by a Member or a Group
        when (customer) {
            is Member -> {
                val member: Member = customer
                memberOrders[member.id]!![index] += 1
            }
            is Group -> {
                val group: Group = customer
                groupOrders[group.id]!![index] += 1
            }
        }
    }
}

class ExportHandler(
    private val context: Context,
    private val fileOpener: FileOpener,
    private val memberRepository: MemberRepository,
    private val groupRepository: GroupRepository,
    private val itemRepository: ItemRepository,
    private val orderRepository: OrderRepository,
) {
    fun getOrders(): OrderExport {
        val orders = orderRepository.data
        val items = itemRepository.data

        val export = OrderExport(
            memberRepository = memberRepository,
            groupRepository = groupRepository,
            itemRepository = itemRepository
        )

        orders.forEachIndexed { index, order ->
            export.addOrder(index, order)
        }

        return export
    }
}