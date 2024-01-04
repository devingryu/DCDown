package com.ibd.dcdown.main.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ibd.dcdown.ui.theme.DCDownTheme

@Composable
fun BasicPreference(title: String, content: String, onClick: (() -> Unit)? = null) {
    Row(Modifier
        .fillMaxWidth()
        .background(MaterialTheme.colorScheme.surface)
        .run {
            if (onClick != null) clickable(onClick = onClick)
            else this
        }
        .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            if (content.isNotBlank())
                Text(
                    content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
        }
        if (onClick != null)
            Icon(Icons.Default.ChevronRight, contentDescription = null)
    }

}

@Composable
fun SwitchPreference(
    title: String,
    content: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            Modifier
                .weight(1f)
                .padding(vertical = 12.dp)
        ) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            if (content.isNotBlank())
                Text(
                    content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
fun PreferenceGroup(
    title: String,
    children: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
        )
        children()
        Spacer(
            modifier = Modifier
                .height(1.dp)
                .background(MaterialTheme.colorScheme.outline)
        )
    }
}


@Preview
@Composable
private fun Preview() {
    var checked by remember { mutableStateOf(false) }
    DCDownTheme {
        Surface {
            Column {
                PreferenceGroup(title = "groupName") {
                    BasicPreference("Title", "content", {})
                    SwitchPreference(
                        title = "Title",
                        content = "content",
                        checked = checked,
                        onCheckedChange = { checked = it }
                    )
                }
                PreferenceGroup(title = "groupName") {
                    BasicPreference("Title", "content", {})
                    SwitchPreference(
                        title = "Title",
                        content = "content",
                        checked = checked,
                        onCheckedChange = { checked = it }
                    )
                }
            }


        }
    }
}