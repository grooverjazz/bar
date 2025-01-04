package org.groover.bar.app.util

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp

/**
 * The wrapper layout for all bar screens.
 */
@Composable
fun BarLayout(content: @Composable ColumnScope.() -> Unit) {
    // Change scale of app if needed
    val scale = 1f
    val density = LocalDensity.current.density * scale

    CompositionLocalProvider(LocalDensity provides Density(density = density)) {
        Column(Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            content = content
        )
    }
}
