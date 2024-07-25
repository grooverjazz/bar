package org.groover.bar.export

import androidx.compose.ui.graphics.Color
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.xssf.usermodel.XSSFColor
import org.apache.poi.xssf.usermodel.XSSFWorkbook

/**
 * Class responsible for creating and managing Excel cell styles.
 *   (wrapper for weird Apache POI functionality)
 */
class StyleManager(
    private val workbook: XSSFWorkbook,
) {
    /**
     * Enum that defines style text alignment.
     */
    enum class StyleAlignment {
        Left,
        Center,
        Right
    }

    /**
     * Enum that defines style text formatting.
     */
    enum class StyleFormat {
        Percentage,
        Currency,
    }

    /**
     * Data class that allows StyleManager to reuse previously-created styles.
     *   (key into StyleManager._styles)
     */
    private data class StyleKey(
        val alignment: StyleAlignment? = null,
        val format: StyleFormat? = null,
        val bold: Boolean? = null,
        val backgroundColor: Color? = null,
    )

    // Bold font
    private val boldFont = workbook.createFont().apply { bold = true }

    // Currency format
    private val currencyFormat = workbook.createDataFormat().getFormat("€ #,##0.00")

    // Map of all already-created styles
    private val _styles = emptyMap<StyleKey, XSSFCellStyle>().toMutableMap()

    // (Creates or fetches a style based on the specified parameters)
    fun getStyle(
        alignment: StyleAlignment? = null,
        format: StyleFormat? = null,
        bold: Boolean? = null,
        backgroundColor: Color? = null,
    ): XSSFCellStyle {
        val key = StyleKey(alignment, format, bold, backgroundColor)

        return _styles.getOrPut(key) { createStyle(alignment, format, bold, backgroundColor) }
    }

    // (Creates a style based on the specified parameters)
    private fun createStyle(
        alignment: StyleAlignment?,
        format: StyleFormat?,
        bold: Boolean?,
        backgroundColor: Color?
    ): XSSFCellStyle {
        val newStyle = workbook.createCellStyle()

        when (alignment) {
            StyleAlignment.Left -> newStyle.alignment = HorizontalAlignment.LEFT
            StyleAlignment.Center -> newStyle.alignment = HorizontalAlignment.CENTER
            StyleAlignment.Right -> newStyle.alignment = HorizontalAlignment.RIGHT
            else -> {}
        }

        when (format) {
            StyleFormat.Percentage -> newStyle.dataFormat = 9
            StyleFormat.Currency -> newStyle.dataFormat = currencyFormat
            else -> {}
        }

        when (bold) {
            true -> newStyle.setFont(boldFont)
            else -> {}
        }

        when (backgroundColor) {
            null -> {}
            else -> {
                val color = XSSFColor()
                color.rgb = byteArrayOf(
                    (backgroundColor.red * 255f).toInt().toByte(),
                    (backgroundColor.green * 255f).toInt().toByte(),
                    (backgroundColor.blue * 255f).toInt().toByte(),
                )

                newStyle.setFillForegroundColor(color)
                newStyle.fillPattern = FillPatternType.SOLID_FOREGROUND
            }
        }

        return newStyle
    }
}