package com.ibd.dcdown.main.composable

import android.widget.Space
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ibd.dcdown.ui.theme.DCDownTheme

@Composable
fun FilterChip(
    text: String,
    isSelected: Boolean,
    headingIcon: @Composable() (BoxScope.() -> Unit) = {},
    trailingIcon: @Composable() (BoxScope.() -> Unit) = {},
    onClick: () -> Unit = {},
) {
    val textColor =
        if (!isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onPrimary
    val containerTint =
        if (!isSelected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.primary
    val borderTint =
        if (!isSelected) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary
    Row(
        Modifier
            .clip(CircleShape)
            .background(containerTint)
            .border(1.dp, borderTint, CircleShape)
            .clickable(onClick = onClick)
            .padding(12.dp, 8.dp),
        verticalAlignment = Alignment.CenterVertically,

        ) {
        Box(Modifier.sizeIn(0.dp, 14.dp)) {
            headingIcon()
        }
        Spacer(Modifier.width(4.dp))
        Text(text, style = MaterialTheme.typography.labelLarge.copy(color = textColor))
        Spacer(Modifier.width(4.dp))
        Box(Modifier.sizeIn(0.dp, 14.dp)) {
            trailingIcon()
        }
    }
}

@Preview
@Composable
private fun FilterChipPreview() {
    DCDownTheme {
        val isSelected = false
        FilterChip("chip", isSelected, { Icon(Icons.Filled.Whatshot, null, tint = Color.Red) })
    }
}