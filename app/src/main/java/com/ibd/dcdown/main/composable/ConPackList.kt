package com.ibd.dcdown.main.composable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ibd.dcdown.dto.ConPack
import com.ibd.dcdown.tools.Extensions.OnBottomReached
import com.ibd.dcdown.ui.theme.DCDownTheme
import com.ibd.dcdown.ui.theme.Gray200

@Composable
fun ConPackList(
    data: List<ConPack>,
    isLoading: Boolean,
    onClick: (ConPack) -> Unit,
    onLoadMore: () -> Unit
) {
    val listState = rememberLazyListState().apply {
        OnBottomReached(onLoadMore)
    }
    LazyColumn(state = listState, contentPadding = PaddingValues(bottom = 64.dp)) {
        items(data, key = { it.idx }) {
            ConPackListItem(data = it, Modifier.clickable { onClick(it) })
        }
        if (isLoading)
            item {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(32.dp)
                        .padding(vertical = 16.dp)) {
                    CircularProgressIndicator(
                        Modifier
                            .fillMaxHeight()
                            .align(Alignment.Center)
                    )
                }
            }
    }
}

@Composable
fun ConPackListItem(data: ConPack, modifier: Modifier = Modifier) {
    Box(modifier = modifier.background(MaterialTheme.colorScheme.surface)) {
        Row(Modifier.padding(12.dp, 12.dp)) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(data.img)
                    .addHeader("Referer", "https://dccon.dcinside.com/")
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .border(BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant), CircleShape)
                    .align(Alignment.CenterVertically),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                Modifier
                    .weight(1f)
                    .padding(top = 4.dp)
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
        Box(
            Modifier
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .height(1.dp)
                .fillMaxWidth(0.9f)
                .align(Alignment.BottomCenter)
        )
    }
}

@Preview
@Composable
private fun Preview() {
    DCDownTheme {
        ConPackListItem(
            ConPack(
                name = "아주긴디시콘제목",
                author = "작성자",
                idx = "",
                img = null,
                data = arrayListOf()
            )
        )
    }
}