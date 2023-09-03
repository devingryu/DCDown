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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.load.model.GlideUrl
import com.ibd.dcdown.R
import com.ibd.dcdown.dto.ConPack
import com.ibd.dcdown.tools.C
import com.ibd.dcdown.tools.Extensions.OnBottomReached
import com.ibd.dcdown.ui.theme.DCDownTheme

@Composable
fun ConPackList(
    modifier: Modifier = Modifier,
    data: List<ConPack>,
    isLoading: Boolean,
    hasMore: Boolean,
    header: LazyListScope.() -> Unit = {},
    onClickItem: (ConPack) -> Unit = {},
    onClickItemMore: (ConPack) -> Unit = {},
    onLoadMore: () -> Unit = {}
) {
    val listState = rememberLazyListState().apply {
        if (!isLoading && hasMore)
            OnBottomReached(onLoadMore)
    }
    LazyColumn(
        modifier = modifier,
        state = listState,
        contentPadding = PaddingValues(bottom = 64.dp)
    ) {
        header()
        items(data) {
            ConPackListItem(data = it, modifier = Modifier.clickable { onClickItem(it) }, onMoreClick = { onClickItemMore(it) })
        }
        if (!hasMore)
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
        if (isLoading)
            item {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(32.dp)
                        .padding(vertical = 16.dp)
                ) {
                    CircularProgressIndicator(
                        Modifier
                            .fillMaxHeight()
                            .align(Alignment.Center)
                    )
                }
            }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ConPackListItem(data: ConPack, modifier: Modifier = Modifier, onMoreClick: () -> Unit = {}) {
    Box(modifier = modifier.background(MaterialTheme.colorScheme.surface)) {
        Row(Modifier.padding(12.dp, 8.dp)) {
            GlideImage(
                model = GlideUrl(data.img) { mapOf("Referer" to C.DEFAULT_REFERER) },
                contentDescription = data.name,
                modifier = Modifier
                    .size(64.dp)
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
//        Box(
//            Modifier
//                .background(MaterialTheme.colorScheme.surfaceVariant)
//                .height(1.dp)
//                .fillMaxWidth(0.9f)
//                .align(Alignment.BottomCenter)
//        )
        Icon(
            Icons.Filled.MoreVert,
            null,
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 6.dp, top = 16.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = false, radius = 16.dp),
                    onClick = onMoreClick
                )
                .size(18.dp)
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