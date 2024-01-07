package com.ibd.dcdown.main.composable

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ibd.dcdown.R
import com.ibd.dcdown.dto.ConPack
import com.ibd.dcdown.main.view.DetailActivity
import com.ibd.dcdown.main.viewmodel.SearchViewModel.E
import com.ibd.dcdown.main.viewmodel.SearchViewModel
import com.ibd.dcdown.tools.Extensions.bottomBorder

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SearchPage(vm: SearchViewModel = hiltViewModel()) {
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
                is E.Toast -> {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }

                else -> {}
            }
        }
    }
    val focusManager = LocalFocusManager.current
    val sheetState = rememberModalBottomSheetState()
    var sheetData: ConPack? by rememberSaveable { mutableStateOf(null) }

    val filter = listOf(Filter.Hot, Filter.New)
    var query by remember { mutableStateOf("") }
    Scaffold { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)) {
            val surfaceColor = MaterialTheme.colorScheme.surface
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .bottomBorder(1.dp, MaterialTheme.colorScheme.outline),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { (context as? Activity)?.finish() }) {
                    Icon(Icons.Filled.ArrowBack, stringResource(R.string.to_back))
                }
                TextField(
                    value = query, onValueChange = { query = it },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = surfaceColor,
                        unfocusedContainerColor = surfaceColor,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text(stringResource(R.string.input_query)) },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = {
                                query = ""
                                vm.query = ""
                                focusManager.clearFocus()
                                vm.list.clear()
                            }) {
                                Icon(Icons.Filled.Close, null)
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            vm.query = query
                            vm.requestList(true)
                            focusManager.clearFocus()
                        }
                    )
                )
            }
            Box(modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))) {
                ConPackList(
                    data = vm.list,
                    isLoading = vm.isLoadingMore || vm.isRefreshing,
                    hasMore = vm.hasMore,
                    header = {
                        if (vm.list.isNotEmpty())
                            stickyHeader {
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.surface)
                                        .padding(start = 12.dp, bottom = 8.dp, top = 8.dp)
                                ) {
                                    filter.forEach {
                                        val isSelected = vm.filter == it.id
                                        FilterChip(text = it.label, isSelected = isSelected, headingIcon = {
                                            Icon(
                                                it.icon,
                                                null,
                                                tint = if (!isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onPrimary
                                            )
                                        }, onClick = { vm.changeFilter(it.id) })
                                        Spacer(Modifier.width(4.dp))
                                    }
                                }
                            }
                    }, onClickItem = {
                        Intent(context, DetailActivity::class.java).apply {
                            putExtra("id", it.idx)
                            context.startActivity(this)
                        }
                    }, onClickItemMore = {

                    }, onLoadMore = {
                        vm.requestList(false)
                    }
                )
                if (vm.isRefreshing)
                    CircularProgressIndicator(Modifier.align(Alignment.Center))

                sheetData?.let {
                    ConMenuBottomSheet(
                        sheetState = sheetState,
                        data = it,
                        onClick = { type, data -> },
                        onDismiss = { sheetData = null })
                }
            }

        }
    }
}