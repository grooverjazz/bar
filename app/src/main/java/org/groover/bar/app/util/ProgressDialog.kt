package org.groover.bar.app.util

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun ProgressDialog(title: String, progress: Float) {
    Dialog(onDismissRequest = {}) {
        Card(
            modifier = Modifier.size(300.dp, 150.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Title
                Spacer(Modifier.size(10.dp))
                Text(title, fontSize = 30.sp)
                Spacer(Modifier.size(30.dp))

                // Progress bar
                LinearProgressIndicator(progress = { progress })
            }
        }
    }
}