package org.groover.bar.util.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
            .height(60.dp)
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
            fontSize = 25.sp,
            modifier = Modifier.weight(0.3f),
        )

        Text(
            item.priceString,
            modifier = Modifier.weight(0.3f),
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
                onClick = { setAmount(max(0, amount - 1)) },
            ) { Text("-") }

            Text(
                amount.toString(),
                fontSize = 25.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 15.dp).width(50.dp)
            )

            Button(
                modifier = Modifier.fillMaxHeight(),
                shape = RectangleShape,
                onClick = { setAmount(amount + 1) },
            ) { Text("+") }

            // Clear button
            Button(
                modifier = Modifier.padding(start = 20.dp).fillMaxHeight(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                onClick = { setAmount(0) },
            ) { Icon(Icons.Rounded.Refresh, null) }
        }
    }
}
