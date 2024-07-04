package org.groover.bar.util.app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.groover.bar.util.data.BarData

@Composable
fun <T: BarData> EditableBigList(
    height: Dp,
    elements: List<T>,
    getName: (T) -> String,
    getVisible: (T) -> Boolean,
    getColor: (T) -> Color,
    fontColor: Color,
    onClick: (T) -> Unit,
    onMove: ((id: Int, moveUp: Boolean) -> Unit)?,
    onToggleVisible: ((id: Int) -> Unit)?,
    onRemove: ((id: Int) -> Unit)?,
    preContent: (@Composable (() -> Unit))? = null,
) {
    val controlsShowMap: SnapshotStateMap<Int, Boolean> = remember { mutableStateMapOf() }
    val getControlsShow: (id: Int) -> Boolean = { controlsShowMap.getOrDefault(it, false) }

    BigList(height = height) {
        preContent?.invoke()

        elements.forEach { element ->
            val id = element.id

            EditListItem(
                name = getName(element),
                visible = getVisible(element),
                color = getColor(element),
                fontColor = fontColor,
                onClick = { onClick(element) },
                controlsShow = getControlsShow(id),
                toggleControlsShow = { controlsShowMap[id] = !getControlsShow(id) },
                onMove = if (onMove == null) null else { moveUp: Boolean -> onMove(id, moveUp) },
                onToggleVisible = if (onToggleVisible == null) null else ({ onToggleVisible(id) }),
                onRemove = if (onRemove == null) null else ({ onRemove(id) })
            )
        }
    }
}

@Composable
private fun EditListItem(
    name: String,
    visible: Boolean,
    color: Color,
    fontColor: Color,
    onClick: () -> Unit,
    controlsShow: Boolean,
    toggleControlsShow: () -> Unit,
    onMove: ((moveUp: Boolean) -> Unit)?,
    onToggleVisible: (() -> Unit)?,
    onRemove: (() -> Unit)?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .blur(if (visible) 0.dp else 1.dp)
    ) {
        LongPressBigButton(name + if (visible) "" else " (verborgen)",
            color = color.copy(if (visible) 1.0f else 0.1f),
            fontColor = fontColor.copy(if (visible) 1.0f else 0.4f),
            onClick = {
                if (controlsShow)
                    toggleControlsShow()
                else
                    onClick()
            },
            onLongClick = toggleControlsShow,
            rounded = false,
        )

        if (controlsShow) {
            Spacer(Modifier.size(10.dp))

            Row(
                modifier = Modifier
                    .height(60.dp)
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (onRemove != null)
                    ItemControl(1f, Color.Red, onRemove, Icons.Rounded.Delete)

                if (onToggleVisible != null)
                    ItemControl(1f, Color.Blue, onToggleVisible, Icons.Rounded.Face)

                if (onMove != null) {
                    ItemControl(2f, MaterialTheme.colorScheme.tertiary, { onMove(true) }, Icons.Rounded.KeyboardArrowUp)
                    ItemControl(2f, MaterialTheme.colorScheme.tertiary, { onMove(false) }, Icons.Rounded.KeyboardArrowDown)
                }
            }

            Spacer(Modifier.size(10.dp))
        }
    }
}

@Composable
private fun RowScope.ItemControl(
    weight: Float,
    color: Color,
    onClick: () -> Unit,
    icon: ImageVector
) {
    Button(modifier = Modifier
        .weight(weight)
        .fillMaxHeight(),
        colors = ButtonDefaults.buttonColors(color),
        shape = RoundedCornerShape(5.dp),
        onClick = onClick
    ) {
        Icon(icon, null, modifier = Modifier.size(40.dp))
    }
}
