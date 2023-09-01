package com.ibd.dcdown.main.composable


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ibd.dcdown.ui.theme.DCDownTheme
import com.ibd.dcdown.ui.theme.Gray900

@Composable
fun CircleCheckBox(isChecked: Boolean, modifier: Modifier = Modifier, insets: PaddingValues = PaddingValues(8.dp),onCheckedChange: (Boolean) -> Unit) {
    val backgroundColor =
        if (isChecked) MaterialTheme.colorScheme.primary else Gray900.copy(alpha = 0.15f)
    val borderColor =
        if (isChecked) MaterialTheme.colorScheme.primary else Color.White
    Box(
        modifier
            .size(36.dp)
            .toggleable(
                value = isChecked,
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = false),
                onValueChange = onCheckedChange
            )
            .padding(insets)
            .clip(CircleShape)
            .background(backgroundColor)
            .border(1.dp, borderColor, CircleShape)
            .padding(2.dp)
    ) {
        if (isChecked)
            Icon(Icons.Filled.Check, null, tint = Color.White)
    }
}

@Preview
@Composable
private fun CircleCheckBoxPreview() {
    DCDownTheme {
        Row {
            CircleCheckBox(isChecked = false, onCheckedChange = {})
            Spacer(modifier = Modifier.width(2.dp))
            CircleCheckBox(isChecked = true, onCheckedChange = {})
        }
    }
}