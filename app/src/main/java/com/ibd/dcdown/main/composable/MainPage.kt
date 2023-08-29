package com.ibd.dcdown.main.composable

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ibd.dcdown.R
import com.ibd.dcdown.main.viewmodel.HomeViewModel
import com.ibd.dcdown.tools.C
import okhttp3.internal.wait

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPage() {
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = !isSystemInDarkTheme()
    val statusBarColor = MaterialTheme.colorScheme.surface
    val navBarColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    DisposableEffect(systemUiController, useDarkIcons) {
        systemUiController.setStatusBarColor(statusBarColor, useDarkIcons)
        systemUiController.setNavigationBarColor(navBarColor, useDarkIcons)
        onDispose { }
    }

    val navController = rememberNavController()
    val screens = listOf(
        MainScreen.Home,
        MainScreen.History,
        MainScreen.More
    )

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text("홈") },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.largeTopAppBarColors(scrolledContainerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
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
        }
    ) { innerPadding ->
        NavHost(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            startDestination = MainScreen.Home.route
        ) {
            composable("home") { MainHomeScreen() }
            composable("search") { MainSearchScreen() }
            composable("history") { MainHistoryScreen() }
            composable("more") { MainMoreScreen() }
        }
    }
}

@Composable
private fun MainHomeScreen(
    vm: HomeViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        if (vm.list.isEmpty())
            vm.requestList(true)
    }
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
    ConPackList(data = vm.list, isLoading = vm.isLoadingMore, {

    }, {
        vm.requestList(false)
    })
}

@Composable
private fun MainSearchScreen(
) {
    Column(verticalArrangement = Arrangement.Center) {
        Text("Search")
    }
}

@Composable
private fun MainHistoryScreen(
    vm: HomeViewModel = hiltViewModel()
) {
    Column(verticalArrangement = Arrangement.Center) {
        Text("History")
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