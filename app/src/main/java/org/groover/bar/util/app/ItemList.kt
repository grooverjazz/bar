package org.groover.bar.util.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.ui.unit.dp
import org.groover.bar.data.item.Item
import org.groover.bar.data.item.ItemRepository
import kotlin.math.max

@Composable
fun ItemList(
    items: List<Item>,
    amounts: MutableList<Int>,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
    ) {
        items.forEachIndexed { index, item ->
            item {
                ItemListEntry(item, index, amounts[index], setAmount = { amounts[index] = it })
            }
        }
    }
}

@Composable
fun ItemListEntry(item: Item, index: Int, amount: Int, setAmount: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .height(50.dp)
            .fillMaxWidth()
            .background(if (index % 2 == 0) Color.LightGray else Color.White)
            .padding(5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        // Item text
        Text(
            item.name,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(0.2f),
        )

        Text(
            item.priceWithBtwString,
            modifier = Modifier.weight(0.2f),
        )

        // Amount buttons
        Row(
            modifier = Modifier.weight(0.6f),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Add and remove buttons
            Button(
                shape = RectangleShape,
                onClick = { setAmount(max(0, amount - 1)) },
            ) { Text("-") }

            Text(
                amount.toInt().toString(),
                modifier = Modifier.padding(horizontal = 15.dp)
            )

            Button(
                shape = RectangleShape,
                onClick = { setAmount(amount + 1) },
            ) { Text("+") }

            // Clear button
            Button(
                modifier = Modifier.padding(start = 20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                onClick = { setAmount(0) },
            ) { Icon(Icons.Rounded.Refresh, null) }
        }
    }
}
