package org.groover.bar.export

import org.apache.poi.xssf.usermodel.XSSFSheet
import org.groover.bar.data.group.GroupRepository
import org.groover.bar.data.item.ItemRepository
import org.groover.bar.data.member.MemberRepository
import org.groover.bar.data.order.OrderRepository

class IncassoExportHandler(
    private val memberRepository: MemberRepository,
    private val groupRepository: GroupRepository,
    private val itemRepository: ItemRepository,
    private val orderRepository: OrderRepository,
    private val sessionName: String
)  {
    fun export(sheet: XSSFSheet) {
        var currentRowIndex = 0

        val headerRow = sheet.createRow(currentRowIndex)
        ExcelHandler.writeRow(
            headerRow,
            listOf(
                "id",
                "voornaam",
                "tussenvoegsel",
                "achternaam",
                sessionName
            )
        )
        currentRowIndex += 1

        val members = memberRepository.data
        val itemsCount = itemRepository.data.size
        members.forEach { member ->
            // Define reference to total in overzicht sheet
            val totalRefFormula = ExcelHandler.ExcelFormula("Overzicht!" + ExcelHandler.cellStr(currentRowIndex + 4, itemsCount + 5))

            val memberRow = sheet.createRow(currentRowIndex)
            ExcelHandler.writeRow(
                memberRow,
                listOf(
                    member.id,
                    member.roepVoornaam,
                    member.tussenvoegsel,
                    member.achternaam,
                    totalRefFormula,
                )
            )

            currentRowIndex += 1
        }
    }
}