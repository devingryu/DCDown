package com.ibd.dcdown.main.composable

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirplaneTicket
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ibd.dcdown.R
import com.ibd.dcdown.dto.ConPack
import com.ibd.dcdown.tools.C
import com.ibd.dcdown.tools.C.ConPackMenuClickType
import com.ibd.dcdown.ui.theme.DCDownTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConMenuBottomSheet(
    sheetState: SheetState,
    data: ConPack,
    onClick: (@ConPackMenuClickType Int, ConPack) -> Unit,
    onDismiss: () -> Unit
) {
    val bottomInset = WindowInsets.systemBars.getBottom(LocalDensity.current)
    val context = LocalContext.current
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss,
        windowInsets = WindowInsets(bottom = bottomInset),
    ) {
        ConMenuBottomSheetInner(context = context, data = data, onClick = onClick)
    }
}

@Composable
private fun ConMenuBottomSheetInner(
    context: Context,
    data: ConPack,
    onClick: (@ConPackMenuClickType Int, ConPack) -> Unit,
) {
    Column(Modifier.fillMaxWidth()) {
        Row(
            Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { onClick(C.CON_PACK_CLICK_DETAIL, data) }
                )
                .padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(data.img)
                    .addHeader("Referer", C.DEFAULT_REFERER)
                    .crossfade(100)
                    .build(),
                contentDescription = data.name,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .border(
                        BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                        CircleShape
                    )
                    .align(Alignment.CenterVertically),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                Modifier
                    .weight(1f)
                    .padding(top = 2.dp)
            ) {
                Text(
                    data.name, style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    data.author, style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

            }
        }
        ConMenuBottomSheetItem(
            icon = { Icon(Icons.Default.Save, null) },
            text = stringResource(R.string.save_all)
        ) {
            onClick(C.CON_PACK_CLICK_DOWNLOAD_DEFAULT, data)
        }
        ConMenuBottomSheetItem(
            icon = { Icon(Icons.Default.AirplaneTicket, null) },
            text = stringResource(R.string.save_compressed)
        ) {
            onClick(C.CON_PACK_CLICK_DOWNLOAD_COMPRESSED, data)
        }
    }
}

@Composable
private fun ConMenuBottomSheetItem(
    icon: @Composable () -> Unit,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon()
        Text(
            text = text,
            modifier = Modifier.padding(start = 16.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
@Preview
private fun Preview() {
    val context = LocalContext.current
    DCDownTheme {
        Surface(Modifier.fillMaxSize()) {
            ConMenuBottomSheetInner(
                context = context,
                data = ConPack("이름이름", "유저유저", "000000", null, listOf()),
            ) { _, _ ->
            }
        }
    }
}

//@Composable
//@Preview
//private fun Preview2() {
//    val context = LocalContext.current
//    DCDownTheme {
//        Surface(Modifier.fillMaxSize()) {
//            ConMenuBottomSheetItem(icon = { Icon(Icons.Default.Save, null) }, text = "전체 저장") {
//            }
//        }
//    }
//}
