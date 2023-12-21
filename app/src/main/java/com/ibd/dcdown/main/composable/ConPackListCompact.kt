package com.ibd.dcdown.main.composable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ibd.dcdown.R
import com.ibd.dcdown.dto.MyCon
import com.ibd.dcdown.tools.C
import com.ibd.dcdown.ui.theme.DCDownTheme

@Composable
fun ConPackListCompact(
    modifier: Modifier = Modifier,
    data: List<MyCon>,
    header: LazyListScope.() -> Unit = {},
    onClickItem: (MyCon) -> Unit = {},
    onClickItemMore: (MyCon) -> Unit = {},
) {
    val listState = rememberLazyListState()
    LazyColumn(
        modifier = modifier,
        state = listState,
        contentPadding = PaddingValues(bottom = 64.dp)
    ) {
        header()
        items(data) {
            ConPackListCompactItem(
                data = it,
                modifier = Modifier.clickable { onClickItem(it) },
                onMoreClick = { onClickItemMore(it) })
        }
        item {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 32.dp)
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(id = R.string.load_end),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.outline
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun ConPackListCompactItem(
    data: MyCon,
    modifier: Modifier = Modifier,
    onMoreClick: () -> Unit = {}
) {
    val context = LocalContext.current
    Box(modifier = modifier.background(MaterialTheme.colorScheme.surface)) {
        Row(Modifier.padding(16.dp, 12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(data.img)
                    .addHeader("Referer", C.DEFAULT_REFERER)
                    .crossfade(100)
                    .build(),
                contentDescription = data.title,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .border(
                        BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                        CircleShape
                    )
                    .align(Alignment.CenterVertically),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))

            Text(
                data.title ?: "",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Box(Modifier.padding(4.dp).clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = false, radius = 16.dp),
                onClick = onMoreClick
            )) {
                Icon(
                    Icons.Filled.MoreVert,
                    null,
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewCompact() {
    DCDownTheme {
        ConPackListCompactItem(
            MyCon(null, "0", "아주긴제목아주긴제목아주긴제목아주긴제목")
        )
    }
}