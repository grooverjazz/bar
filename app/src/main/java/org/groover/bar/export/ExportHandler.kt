package org.groover.bar.export

import android.content.Context
import android.widget.Toast
import org.apache.poi.ss.util.CellReference
import org.apache.poi.xssf.usermodel.XSSFCell
import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.groover.bar.data.group.GroupRepository
import org.groover.bar.data.item.ItemRepository
import org.groover.bar.data.member.MemberRepository
import org.groover.bar.data.order.OrderRepository
import org.groover.bar.util.data.Cents
import org.groover.bar.util.data.FileOpener

class ExportHandler(
    private val context: Context,
    private val fileOpener: FileOpener,
    private val optionsHandler: OptionsHandler,
    private val memberRepository: MemberRepository,
    private val groupRepository: GroupRepository,
    private val itemRepository: ItemRepository,
    private val orderRepository: OrderRepository,
) {
    fun export(exportName: String) {
        // Create a new workbook
        val workbook = XSSFWorkbook()

        // Export overzicht
        OverzichtExportHandler(
            memberRepository = memberRepository,
            groupRepository = groupRepository,
            itemRepository = itemRepository,
            orderRepository = orderRepository,
        ).export(workbook.createSheet("Overzicht"))

        // Export BTW
        BTWExportHandler(
            itemRepository = itemRepository,
            orderRepository = orderRepository,
        ).export(workbook.createSheet("BTW"))

        // Export incasso
        IncassoExportHandler(
            memberRepository = memberRepository,
            groupRepository = groupRepository,
            itemRepository = itemRepository,
            orderRepository = orderRepository,
            sessionName = optionsHandler.sessionName,
        ).export(workbook.createSheet("Incasso"))

        // Export and close
        fileOpener.write("$exportName.xlsx", workbook)
        workbook.close()

        // Show toast
        Toast
            .makeText(context, "Excel geexporteerd!", Toast.LENGTH_SHORT)
            .show()
    }
}