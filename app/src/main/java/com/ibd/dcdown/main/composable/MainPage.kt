package com.ibd.dcdown.main.composable

import android.content.Intent
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ibd.dcdown.R
import com.ibd.dcdown.dto.User
import com.ibd.dcdown.login.view.LoginActivity
import com.ibd.dcdown.main.view.DetailActivity
import com.ibd.dcdown.main.view.SearchActivity
import com.ibd.dcdown.main.viewmodel.HomeViewModel
import com.ibd.dcdown.repository.ConRepositoryImpl
import com.ibd.dcdown.tools.AuthUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun MainPage() {
    val context = LocalContext.current
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = !isSystemInDarkTheme()
    val statusBarColor = MaterialTheme.colorScheme.surface
    val navBarColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val screens = listOf(
        MainScreen.Home,
        MainScreen.History,
        MainScreen.More
    )

    DisposableEffect(systemUiController, useDarkIcons) {
        systemUiController.setStatusBarColor(statusBarColor, useDarkIcons)
        systemUiController.setNavigationBarColor(navBarColor, useDarkIcons)
        onDispose { }
    }
//    LaunchedEffect(Unit) {
//        withContext(Dispatchers.IO) {
//            val myCons = ConRepositoryImpl().requestMyCons(user)
//        }
//    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                screens.forEach { screen ->
                    val selected =
                        currentDestination?.hierarchy?.any { it.route == screen.route } == true
                    NavigationBarItem(
                        icon = {
                            Icon(
                                if (selected) screen.enabledIcon else screen.disabledIcon,
                                contentDescription = null
                            )
                        },
                        label = { Text(stringResource(screen.label)) },
                        selected = selected,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        })
                }
            }
        },
        floatingActionButton = {
            if (currentDestination?.hierarchy?.any { it.route == MainScreen.Home.route } == true) {
                FloatingActionButton(onClick = {
                    Intent(context, SearchActivity::class.java).apply {
                        context.startActivity(this)
                    }
                }) {
                    Icon(Icons.Filled.Search, stringResource(R.string.save))
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            startDestination = MainScreen.Home.route,
        ) {
            composable("home") { MainHomeScreen() }
            composable("history") { MainHistoryScreen() }
            composable("more") { MainMoreScreen() }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun MainHomeScreen(
    vm: HomeViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        if (vm.list.isEmpty())
            vm.requestList(true)
    }
    val sheetState = rememberModalBottomSheetState()
    var isSheetVisible by rememberSaveable { mutableStateOf(false) }

    val event by vm.eventChannel.collectAsState(initial = null)
    val context = LocalContext.current
    LaunchedEffect(event) {
        event.let {
            when (it) {
                is HomeViewModel.E.Toast -> {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }

                else -> {}
            }
        }
    }

    val filter = listOf(Filter.Hot, Filter.New)
    Box(Modifier.fillMaxSize()) {
        if (vm.isRefreshing)
            CircularProgressIndicator(
                Modifier
                    .size(24.dp)
                    .align(Alignment.Center)
            )
        Column(Modifier.fillMaxSize()) {
            ConPackList(
                modifier = Modifier.clip(RoundedCornerShape(12.dp)),
                data = vm.list,
                isLoading = vm.isLoadingMore,
                hasMore = true,
                header = {
                    item {
                        Text(
                            stringResource(R.string.home),
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp, 64.dp, 0.dp, 8.dp),
                            style = MaterialTheme.typography.headlineLarge
                        )
                    }
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
                    isSheetVisible = true
                }, onLoadMore = {
                    vm.requestList(false)
                }
            )
        }

        if (isSheetVisible)
            ModalBottomSheet(sheetState = sheetState, onDismissRequest = { isSheetVisible = false }) {
                Text("MODAL", modifier = Modifier.padding(16.dp))
            }
    }
}

@Composable
private fun MainHistoryScreen(
    vm: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    Column(verticalArrangement = Arrangement.Center) {
        Button(onClick = { context.startActivity(Intent(context, LoginActivity::class.java)) }) {
            Text("asdfasfd")
        }
    }
}

@Composable
private fun MainMoreScreen(
    vm: HomeViewModel = hiltViewModel()
) {
    Column(verticalArrangement = Arrangement.Center) {
        Text("More")
    }
}

sealed class MainScreen(
    val route: String,
    @StringRes val label: Int,
    val disabledIcon: ImageVector,
    val enabledIcon: ImageVector
) {
    object Home : MainScreen("home", R.string.home, Icons.Outlined.Home, Icons.Filled.Home)
    object History :
        MainScreen(
            "history",
            R.string.purchase_list,
            Icons.Outlined.ShoppingCart,
            Icons.Filled.ShoppingCart
        )

    object More : MainScreen("more", R.string.more, Icons.Outlined.Settings, Icons.Filled.Settings)
}

sealed class Filter(
    val id: Int,
    val icon: ImageVector,
    val label: String
) {
    object Hot : Filter(0, Icons.Filled.Whatshot, "인기")
    object New : Filter(1, Icons.Filled.NewReleases,"신규")
}