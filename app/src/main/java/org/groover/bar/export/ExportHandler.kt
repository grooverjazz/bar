package org.groover.bar.export

import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.groover.bar.data.util.OptionsHandler
import org.groover.bar.data.customer.CustomerRepository
import org.groover.bar.data.item.ItemRepository
import org.groover.bar.data.order.OrderRepository
import org.groover.bar.data.util.FileOpener

/**
 * Class responsible for handling export functionality.
 */
class ExportHandler(
    private val fileOpener: FileOpener,
    private val optionsHandler: OptionsHandler,
    private val customerRepository: CustomerRepository,
    private val itemRepository: ItemRepository,
    private val orderRepository: OrderRepository,
) {
    // (Exports the file)
    fun export(
        exportName: String,
        openExternal: Boolean,
        updateProgress: (Float) -> Unit,
    ) {
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
            styleManager = styleManager,
            updateProgress = { progress: Float-> updateProgress(0.8f + 0.1f * progress) },
            customerRepository = customerRepository,
            itemRepository = itemRepository,
            sessionName = optionsHandler.sessionName,
        ).export(workbook.createSheet("Incasso"))
        updateProgress(0.9f)

        // Export and close
        val fileName = "$exportName.xlsx"
        fileOpener.write(fileName, workbook)
        workbook.close()
        updateProgress(1f)

        // Open in external program
        if (openExternal)
            fileOpener.openExternal(fileName)
    }
}