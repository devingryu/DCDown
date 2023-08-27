package com.ibd.dcdown.main.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ibd.dcdown.dto.ConPack
import com.ibd.dcdown.ui.theme.DCDownTheme

@Composable
fun ConPackList(data: List<ConPack>, isLoading: Boolean) {
    LazyColumn {
        items(data, key = { it.idx }) {
            ConPackListItem(data = it)
        }
        item {

        }
    }
}

@Composable
fun ConPackListItem(data: ConPack) {
    Box {
        Row(Modifier.padding(16.dp, 12.dp)) {
            AsyncImage(
                model = data.img,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .align(Alignment.CenterVertically),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                Text(data.name, style = MaterialTheme.typography.titleMedium)
                Text(data.author, style = MaterialTheme.typography.labelSmall)
            }
        }
        IconButton(onClick = { /*TODO*/ },
            Modifier
                .size(24.dp)
                .padding(4.dp)
                .align(Alignment.BottomEnd)) {
            Icon(
                imageVector = Icons.Filled.Download,
                contentDescription = null
            )
        }
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