package org.groover.bar.data.item.composable

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
import org.groover.bar.app.util.BarList
import kotlin.math.max

/**
 * A list of all items.
 */
@Composable
fun ItemList(
    items: List<Item>,
    amounts: MutableList<Int>,
) {
    BarList(height = 870.dp) {
        items.forEachIndexed { index, item ->
            if (item.visible)
                ItemListEntry(item, amounts[index], setAmount = { amounts[index] = it })
        }
    }
}

@Composable
fun ItemListEntry(item: Item, amount: Int, setAmount: (Int) -> Unit) {
    Row(Modifier
            .height(80.dp)
            .fillMaxWidth()
            .background(item.color)
            .padding(5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        // Item text
        Text(item.name,
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            modifier = Modifier
                .weight(0.43f)
                .padding(start = 15.dp),
        )

        Text(item.price.toStringWithEuro(),
            modifier = Modifier.weight(0.1f),
            textAlign = TextAlign.Center,
        )

        // Amount buttons
        Row(Modifier.weight(0.37f),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Add and remove buttons
            Button({ setAmount(amount + 1) },
                modifier = Modifier.fillMaxHeight(),
                shape = RectangleShape,
            ) { Text("+", fontSize = 30.sp) }

            Text(amount.toString(),
                fontSize = 25.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = 15.dp)
                    .width(50.dp),
            )

            Button({ setAmount(max(0, amount - 1)) },
                modifier = Modifier.fillMaxHeight(),
                shape = RectangleShape,
            ) { Text("-", fontSize = 30.sp) }

            // Clear button
            Button({ setAmount(0) },
                modifier = Modifier
                    .padding(start = 20.dp)
                    .fillMaxHeight(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            ) { Icon(Icons.Rounded.Refresh, null) }
        }
    }
}
