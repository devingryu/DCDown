package com.ibd.dcdown.main.composable

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Deselect
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ibd.dcdown.R
import com.ibd.dcdown.main.viewmodel.DetailViewModel
import com.ibd.dcdown.tools.C

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailPage(id: String, vm: DetailViewModel = hiltViewModel()) {
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = !isSystemInDarkTheme()
    val systemBarColor = MaterialTheme.colorScheme.surface
    DisposableEffect(systemUiController, useDarkIcons) {
        systemUiController.setSystemBarsColor(systemBarColor, useDarkIcons)
        onDispose { }
    }
    val event by vm.eventChannel.collectAsState(initial = null)
    val context = LocalContext.current
    LaunchedEffect(event) {
        event.let {
            when (it) {
                is DetailViewModel.E.Toast -> {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }

                else -> {}
            }
        }
    }
    LaunchedEffect(Unit) {
        vm.requestList(id)
    }

    val activity = (LocalContext.current as? Activity)
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = { Text(vm.data?.name ?: "") },
                navigationIcon = {
                    IconButton(onClick = { activity?.finish() }) {
                        Icon(Icons.Filled.ArrowBack, stringResource(R.string.to_back))
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(scrolledContainerColor = MaterialTheme.colorScheme.surface),
                scrollBehavior = scrollBehavior,
                actions = {
                    IconButton(onClick = { vm.toggleAll() }) {
                        val isSelectedAll = vm.list.all { it.selected }
                        Icon(
                            if (isSelectedAll) Icons.Filled.Deselect else Icons.Filled.SelectAll,
                            stringResource(if (isSelectedAll) R.string.deselect else R.string.select_all)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { vm.requestSaveSelected(false) }) {
                Icon(Icons.Filled.Save, stringResource(R.string.save))
            }
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            modifier = Modifier
                .padding(paddingValues)
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = PaddingValues(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 128.dp),
            columns = GridCells.Adaptive(minSize = 96.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            items(vm.list) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .toggleable(it.selected) { _ -> vm.toggle(it.id) }
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data("${C.IMG_BASE_URL}${it.uri}")
                            .addHeader("Referer", C.DEFAULT_REFERER)
                            .crossfade(100)
                            .build(),
                        contentDescription = it.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        placeholder = ColorPainter(MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp))
                    )
                    CircleCheckBox(
                        modifier = Modifier.align(Alignment.BottomEnd),
                        isChecked = it.selected,
                        onCheckedChange = { _ -> vm.toggle(it.id) })
                }
            }
        }
    }
}