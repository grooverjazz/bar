package org.groover.bar.util.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.groover.bar.data.item.Item
import kotlin.math.max

@Composable
fun ItemList(
    items: List<Item>,
    amounts: MutableList<Int>,
) {
    BigList(height = 900.dp) {
        items.forEachIndexed { index, item ->
            ItemListEntry(item, item.colorC, amounts[index], setAmount = { amounts[index] = it })
        }
    }
}

@Composable
fun ItemListEntry(item: Item, color: Color, amount: Int, setAmount: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .height(80.dp)
            .fillMaxWidth()
            .background(color)
            .padding(5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        // Item text
        Text(
            item.name,
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            modifier = Modifier
                .weight(0.4f)
                .padding(start = 15.dp),
        )

        Text(
            item.priceString,
            modifier = Modifier.weight(0.1f),
            textAlign = TextAlign.Center,
        )

        // Amount buttons
        Row(
            modifier = Modifier.weight(0.4f),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Add and remove buttons
            Button(
                modifier = Modifier.fillMaxHeight(),
                shape = RectangleShape,
                onClick = { setAmount(amount + 1) },
            ) { Text("+", fontSize = 30.sp) }

            Text(
                amount.toString(),
                fontSize = 25.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = 15.dp)
                    .width(50.dp)
            )

            Button(
                modifier = Modifier.fillMaxHeight(),
                shape = RectangleShape,
                onClick = { setAmount(max(0, amount - 1)) },
            ) { Text("-", fontSize = 30.sp) }

            // Clear button
            Button(
                modifier = Modifier
                    .padding(start = 20.dp)
                    .fillMaxHeight(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                onClick = { setAmount(0) },
            ) { Icon(Icons.Rounded.Refresh, null) }
        }
    }
}
