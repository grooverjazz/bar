package org.groover.bar.export

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.util.fastMap
import androidx.compose.ui.util.fastMapIndexed
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.groover.bar.data.customer.CustomerRepository
import org.groover.bar.data.item.ItemRepository
import org.groover.bar.data.order.OrderRepository
import org.groover.bar.export.ExcelHandler.Companion.cellStr
import org.groover.bar.export.ExcelHandler.Companion.withStyle
import org.groover.bar.export.ExcelHandler.Companion.withStyles
import org.groover.bar.export.ExcelHandler.Companion.writeRow
import org.groover.bar.export.ExcelHandler.ExcelFormula
import org.groover.bar.export.StyleManager.StyleAlignment
import org.groover.bar.export.StyleManager.StyleFormat
import org.groover.bar.data.util.removeFirst
import org.groover.bar.export.ExcelHandler.Companion.writeRows


class OverzichtExportHandler(
    private val styleManager: StyleManager,
    private val updateProgress: (Float) -> Unit,
    customerRepository: CustomerRepository,
    itemRepository: ItemRepository,
    orderRepository: OrderRepository,
) {
    private var currentRow: Int = 0

    private val customers = customerRepository.data

    // Reorder members such that Hospitality is on top
    private val members = listOf(customerRepository.members.find(0)!!) +
        customerRepository.members.data.removeFirst { it.id == 0 }
    private val membersCount = members.size
    private val extraMembersCount = members.count { it.isExtra }

    private val groups = customerRepository.groups.data
    private val groupsCount = groups.size

    private val items = itemRepository.data
    private val itemsCount = items.size

    private val orders = orderRepository.data

    // (Gets all customer orders)
    private fun getCustomerOrders(): Map<Int, List<Int>> {
        // Initialize all amounts
        val allAmounts: MutableMap<Int, MutableList<Int>> = customers.associate { data ->
            data.id to MutableList(itemsCount) { 0 }
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

    // (Gets the formula summing a row from the member and group table)
    //  (excludes extra members)
    private fun customerSum(rowIndex: Int): Pair<String, String> {
        val memberStartCell = cellStr(7 + extraMembersCount, rowIndex)
        val memberEndCell = cellStr(7 + membersCount - 1, rowIndex)
        val memberPart = "$memberStartCell:$memberEndCell"

        val groupStartCell = cellStr(7 + membersCount - 1 + 4, rowIndex)
        val groupEndCell = cellStr(7 + membersCount - 1 + 4 + groupsCount - 1, rowIndex)
        val groupPart = if (groupsCount == 0) "0" else "$groupStartCell:$groupEndCell"

        return Pair(memberPart, groupPart)
    }


    // Creates a map from <isExtra: Boolean, isHospitality / isEven: Boolean> to List<XSSFCellStyle>
    //   (the last in this list is the default style,
    //   the second-to last is the default style with currency formatting,
    //   the rest of the styles are laid out per order)
    private fun createColorMap(): Map<Pair<Boolean, Boolean>, List<XSSFCellStyle>> {
        // Gets the styles of the color mixed with all item colors, and of the original color
        fun getStyles(col: Color): List<XSSFCellStyle> {
            val colors = items.map { it.color.copy(alpha = 0.25f).compositeOver(col) }
            return (
                colors.map { styleManager.getStyle(backgroundColor = it) }
                    + styleManager.getStyle(backgroundColor = col, format = StyleFormat.Currency)
                    + styleManager.getStyle(backgroundColor = col)
            )
        }

        return mapOf(
            // Extra members: hospitality and other
            Pair(true, true) to getStyles(Color(242, 206, 239)),
            Pair(true, false) to getStyles(Color(193, 240, 200)),

            // Regular members: odd and even index
            Pair(false, false) to getStyles(Color(255, 255, 255)),
            Pair(false, true) to getStyles(Color(217, 217, 217)),
        )
    }

    // (Writes the overview table with customer and item info)
    private fun writeOverview(
        sheet: XSSFSheet,
    ) {
        // Create styles
        val leftAlignStyle = styleManager.getStyle(alignment = StyleAlignment.Left)
        val boldStyle = styleManager.getStyle(bold = true)
        val percentageStyle = styleManager.getStyle(format = StyleFormat.Percentage)
        val currencyStyle = styleManager.getStyle(format = StyleFormat.Currency)
        val totaleOmzetStyle = styleManager.getStyle(format = StyleFormat.Currency, alignment = StyleAlignment.Left)

        // Calculate customer values
        val aantalLeden = membersCount
        val aantalExtraLeden = extraMembersCount
        val aantalAanwezigeLeden = ExcelFormula("countif(${customerSum(2).first}, \"ja\")")
        val totaleOmzet = ExcelFormula("sum(${customerSum(3 + itemsCount + groupsCount).first})")

        // Calculate item values
        val allItemNames = items.fastMap { it.name }
        val allItemPrices = items.fastMap { it.price }
        val allItemAfzet = List(itemsCount) { index ->
            val (memberSumStr, groupSumStr) = customerSum(3 + index)
            ExcelFormula("sum($memberSumStr, $groupSumStr)")
        }
        val allItemOmzet = List(itemsCount) { index ->
            val prijsCell = cellStr(1, 3 + index)
            val afzetCell = cellStr(2, 3 + index)
            ExcelFormula("$afzetCell * $prijsCell")
        }
        val allItemBtw = items.map { it.btwPercentage.toDouble() / 100 }

        // Write table
        writeRows(sheet,
            listOf(
                (listOf("OVERZICHT", "(data excl. Hospitality en 'extra' leden)", "")                       + allItemNames).withStyle(boldStyle),
                listOf("aantal leden",           aantalLeden         .withStyle(leftAlignStyle),   "prijs") + allItemPrices.withStyle(currencyStyle),
                listOf("aantal 'extra' leden",   aantalExtraLeden    .withStyle(leftAlignStyle),   "afzet") + allItemAfzet,
                listOf("aantal aanwezige leden", aantalAanwezigeLeden.withStyle(leftAlignStyle),   "omzet") + allItemOmzet.withStyle(currencyStyle),
                listOf("totale omzet",           totaleOmzet         .withStyle(totaleOmzetStyle), "btw")   + allItemBtw.withStyle(percentageStyle),
            ),
            currentRow
        )

        currentRow += 5
    }

    private fun writeMemberOrders(
        sheet: XSSFSheet,
        colorMap: Map<Pair<Boolean, Boolean>, List<XSSFCellStyle>>,
        customerOrders: Map<Int, List<Int>>,
        pricesRangeStr: String
    ) {
        // Get styles
        val boldStyle = styleManager.getStyle(bold = true)
        val rightBoldStyle = styleManager.getStyle(bold = true, alignment = StyleAlignment.Right)

        // Members title
        writeRow(sheet.createRow(currentRow),
            listOf("LEDEN".withStyle(boldStyle)),
        )
        currentRow += 1

        // Calculate values
        val styledItemNames = items.fastMap { item ->
            val style = styleManager.getStyle(backgroundColor = item.color, bold = true)
            item.name.withStyle(style)
        }
        val groupNames = groups.fastMap { it.name }

        // Members header
        writeRow(sheet.createRow(currentRow),
            listOf("id".withStyle(rightBoldStyle), "naam", "aanwezig") + styledItemNames + groupNames + listOf("OMZET"),
            style = boldStyle,
        )
        currentRow += 1

        // Members
        members.forEachIndexed { index, member ->
            // Create formula for presence
            val totalCell = cellStr(currentRow, 3 + itemsCount + groupsCount)
            val aanwezig = ExcelFormula("if($totalCell,\"ja\",\"nee\")")

            // Get regular orders
            val regularOrders: List<Int> = customerOrders[member.id]!!

            // Get all formulas for group shares
            val groupShares = groups.fastMapIndexed { groupIndex, group ->
                if (!group.memberIds.contains(member.id)) "" else {
                    val groupTotalCellStr = cellStr(7 + membersCount + 3 + groupIndex, 3 + itemsCount)
                    ExcelFormula("$groupTotalCellStr / ${group.memberIds.size}")
                }
            }

            // Get formula for total regular orders
            val regularOrdersRange =
                "${cellStr(currentRow, 3)}:${cellStr(currentRow, 3 + itemsCount - 1)}"
            val regularOrdersTotal = "sumproduct($pricesRangeStr, $regularOrdersRange)"

            // Get formula for total group shares
            val groupSharesRange = if (groups.isEmpty()) "0" else
                "${cellStr(currentRow, 3 + itemsCount)}:${cellStr(currentRow, 3 + itemsCount + groupsCount - 1)}"
            val groupSharesTotalStr = "sum($groupSharesRange)"

            // Get formula for total
            val totalStr = ExcelFormula("$regularOrdersTotal + $groupSharesTotalStr")

            // Get row color
            val memberStyleList = colorMap[Pair(member.isExtra, if (member.isExtra) member.id == 0 else index % 2 == 0)]!!
            val memberDataStyle = memberStyleList.last()
            val memberCurrencyStyle = memberStyleList[memberStyleList.size - 2]

            // Write row
            writeRow(sheet.createRow(currentRow),
                listOf(member.id, member.name, aanwezig).withStyle(memberDataStyle)
                        + regularOrders.fastMap { if (it == 0) "" else it }.withStyles(memberStyleList)
                        + groupShares.withStyle(memberCurrencyStyle)
                        + listOf(totalStr.withStyle(memberCurrencyStyle)),
            )

            currentRow += 1

            updateProgress(0.3f + (index.toFloat() / membersCount) * 0.5f)
        }
    }

    private fun writeGroupOrders(
        sheet: XSSFSheet,
        customerOrders: Map<Int, List<Int>>,
        pricesRangeStr: String,
    ) {
        // Get styles
        val boldStyle = styleManager.getStyle(bold = true)
        val rightBoldStyle = styleManager.getStyle(bold = true, alignment = StyleAlignment.Right)

        // Groups title
        writeRow(sheet.createRow(currentRow),
            listOf("GROEPEN"),
            style = boldStyle,
        )
        currentRow += 1

        // Calculate values
        val itemNames = items.fastMap { it.name }

        // Groups header
        writeRow(sheet.createRow(currentRow),
            listOf("id".withStyle(rightBoldStyle), "naam", "") + itemNames + listOf("OMZET"),
            style = boldStyle,
        )
        currentRow += 1

        // Groups
        groups.forEach { group ->
            val groupOrders: List<Int> = customerOrders[group.id]!!

            // Create formula for total
            val ordersRangeStr = "${cellStr(currentRow, 3)}:${cellStr(currentRow, 3 + itemsCount - 1)}"
            val total = ExcelFormula("sumproduct($pricesRangeStr, $ordersRangeStr)")

            // Get styles
            val currencyStyle = styleManager.getStyle(format = StyleFormat.Currency)

            // Write row
            writeRow(sheet.createRow(currentRow),
                listOf(group.id, group.name, "") + groupOrders + listOf(total.withStyle(currencyStyle)),
            )

            currentRow += 1
        }
    }

    fun export(sheet: XSSFSheet) {
        currentRow = 0

        // Initialize lookups
        val customerOrders: Map<Int, List<Int>> = getCustomerOrders()
        val colorMap: Map<Pair<Boolean, Boolean>, List<XSSFCellStyle>> = createColorMap()
        updateProgress(0.2f)

        // Write overview (customer info, item info)
        val oldRowIndex = currentRow
        writeOverview(sheet)
        updateProgress(0.3f)

        // Define range of prices (used in sumproduct of total)
        val pricesRangeStr = "${cellStr(oldRowIndex + 1, 3)}:${cellStr(oldRowIndex + 1, 2 + itemsCount)}"

        // Write member orders table
        writeMemberOrders(sheet, colorMap, customerOrders, pricesRangeStr)
        updateProgress(0.8f)

        // Write empty row
        currentRow += 1

        // Write group orders table
        writeGroupOrders(sheet, customerOrders, pricesRangeStr)
        updateProgress(1f)
    }
}