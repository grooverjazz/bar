package org.groover.bar.export

import android.content.Context
import android.widget.Toast
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.groover.bar.data.customer.CustomerRepository
import org.groover.bar.data.item.ItemRepository
import org.groover.bar.data.order.OrderRepository
import org.groover.bar.util.data.FileOpener

class ExportHandler(
    private val context: Context,
    private val fileOpener: FileOpener,
    private val optionsHandler: OptionsHandler,
    private val customerRepository: CustomerRepository,
    private val itemRepository: ItemRepository,
    private val orderRepository: OrderRepository,
) {
    fun export(exportName: String) {
        // Create a new workbook
        val workbook = XSSFWorkbook()
        val styleManager = StyleManager(workbook)

        // Export overzicht
        OverzichtExportHandler(
            styleManager = styleManager,
            customerRepository = customerRepository,
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
            customerRepository = customerRepository,
            itemRepository = itemRepository,
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