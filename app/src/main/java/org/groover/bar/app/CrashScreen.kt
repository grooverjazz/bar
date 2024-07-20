package org.groover.bar.app

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.groover.bar.app.util.BarLayout
import org.groover.bar.app.util.BarList
import org.groover.bar.app.util.BarTitle

@Composable
private fun CrashScreen(location: String, e: Exception) {
    BarLayout {
        // Title
        Spacer(Modifier.size(20.dp))
        BarTitle("Crash!")
        Spacer(Modifier.size(20.dp))

        Text("$location: ${e.message}")
        Spacer(Modifier.size(20.dp))

        BarList(height = 600.dp) {
            Spacer(Modifier.size(20.dp))

            e.stackTrace.forEach { element ->
                Text(element.toString(),
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            Spacer(Modifier.size(20.dp))
        }
    }
}

@Composable
fun <T> tryOrCrash(location: String, action: () -> T): T? {
    try {
        return action()
    }
    catch (e: Exception) {
        CrashScreen(location, e)
        return null
    }
}