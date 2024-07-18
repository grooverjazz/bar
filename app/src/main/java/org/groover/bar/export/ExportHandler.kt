package org.groover.bar.export

import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.groover.bar.data.customer.CustomerRepository
import org.groover.bar.data.item.ItemRepository
import org.groover.bar.data.order.OrderRepository
import org.groover.bar.util.data.FileOpener

class ExportHandler(
    private val fileOpener: FileOpener,
    private val optionsHandler: OptionsHandler,
    private val customerRepository: CustomerRepository,
    private val itemRepository: ItemRepository,
    private val orderRepository: OrderRepository,
) {
    fun export(exportName: String, updateProgress: (Float) -> Unit) {
        // Create a new workbook
        val workbook = XSSFWorkbook()
        val styleManager = StyleManager(workbook)
        updateProgress(0.2f)

        // Export overzicht
        OverzichtExportHandler(
            styleManager = styleManager,
            updateProgress = { progress: Float-> updateProgress(0.2f + 0.5f * progress) },
            customerRepository = customerRepository,
            itemRepository = itemRepository,
            orderRepository = orderRepository,
        ).export(workbook.createSheet("Overzicht"))
        updateProgress(0.7f)

        // Export BTW
        BTWExportHandler(
            styleManager = styleManager,
            itemRepository = itemRepository,
        ).export(workbook.createSheet("BTW"))
        updateProgress(0.8f)

        // Export incasso
        IncassoExportHandler(
            updateProgress = { progress: Float-> updateProgress(0.8f + 0.1f * progress) },
            customerRepository = customerRepository,
            itemRepository = itemRepository,
            sessionName = optionsHandler.sessionName,
        ).export(workbook.createSheet("Incasso"))
        updateProgress(0.9f)

        // Export and close
        fileOpener.write("$exportName.xlsx", workbook)
        workbook.close()
        updateProgress(1f)
    }
}