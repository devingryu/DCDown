package com.ibd.dcdown.main.composable

import android.content.Intent
import android.os.Environment
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.work.WorkManager
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.ibd.dcdown.BuildConfig
import com.ibd.dcdown.R
import com.ibd.dcdown.dto.ConPack
import com.ibd.dcdown.login.view.LoginActivity
import com.ibd.dcdown.main.service.ConDownloadWorker
import com.ibd.dcdown.main.view.DetailActivity
import com.ibd.dcdown.main.view.SearchActivity
import com.ibd.dcdown.main.viewmodel.HomeViewModel
import com.ibd.dcdown.main.viewmodel.MyConViewModel
import com.ibd.dcdown.tools.AuthUtil
import com.ibd.dcdown.tools.C
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPage() {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState()
    var sheetData: ConPack? by rememberSaveable { mutableStateOf(null) }
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val screens = listOf(
        MainScreen.Home,
        MainScreen.History,
        MainScreen.More
    )

    val setSheetData: (ConPack?) -> Unit = { sheetData = it }

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
            composable("home") { MainHomeScreen(setSheetData) }
            composable("history") { MainMyConScreen(setSheetData) }
            composable("more") { MainMoreScreen() }
        }
        sheetData?.let {
            ConMenuBottomSheet(
                sheetState = sheetState,
                data = it,
                onClick = { type, data ->
                    when (type) {
                        C.CON_PACK_CLICK_DETAIL -> {
                            Intent(context, DetailActivity::class.java).apply {
                                putExtra("id", it.idx)
                                context.startActivity(this)
                            }
                        }

                        C.CON_PACK_CLICK_DOWNLOAD_DEFAULT -> {
                            Toast.makeText(context, R.string.start_download, Toast.LENGTH_SHORT)
                                .show()
                            WorkManager.getInstance(context)
                                .enqueue(ConDownloadWorker.Builder(it.idx, listOf(), false).build())
                        }

                        C.CON_PACK_CLICK_DOWNLOAD_COMPRESSED -> {
                            Toast.makeText(context, R.string.start_download, Toast.LENGTH_SHORT)
                                .show()
                            WorkManager.getInstance(context)
                                .enqueue(ConDownloadWorker.Builder(it.idx, listOf(), true).build())
                        }
                    }
                    sheetData = null
                },
                onDismiss = { sheetData = null })
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun MainHomeScreen(
    setSheetData: (ConPack?) -> Unit,
    vm: HomeViewModel = hiltViewModel(),
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

    val filter = listOf(Filter.Hot, Filter.New)
    Box(Modifier.fillMaxSize()) {
        if (vm.isRefreshing)
            CircularProgressIndicator(
                Modifier
                    .size(48.dp)
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
                    setSheetData(it)
                }, onLoadMore = {
                    vm.requestList(false)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainMyConScreen(
    setSheetData: (ConPack?) -> Unit,
    vm: MyConViewModel = hiltViewModel(),
) {
    val user by AuthUtil.loginUser.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState()

    LaunchedEffect(user) {
        if (vm.list.isEmpty())
            user?.let { user ->
                vm.requestList(user)
            }
    }

    if (user != null) {
        Box(Modifier.fillMaxSize()) {
            if (vm.isRefreshing)
                CircularProgressIndicator(
                    Modifier
                        .size(48.dp)
                        .align(Alignment.Center)
                )
            else
                ConPackListCompact(
                    modifier = Modifier.clip(RoundedCornerShape(12.dp)),
                    data = vm.list,
                    header = {
                        item {
                            Text(
                                stringResource(R.string.purchase_list),
                                Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp, 64.dp, 0.dp, 8.dp),
                                style = MaterialTheme.typography.headlineLarge
                            )
                        }
                    },
                    onClickItem = {
                        Intent(context, DetailActivity::class.java).apply {
                            putExtra("id", it.packageIdx)
                            context.startActivity(this)
                        }
                    },
                    onClickItemMore = {
                        if (it.packageIdx != null)
                            setSheetData(
                                ConPack(
                                    it.title ?: "",
                                    "",
                                    it.packageIdx,
                                    it.img,
                                    listOf()
                                )
                            )
                    },
                )
        }

    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = {
                context.startActivity(
                    Intent(
                        context,
                        LoginActivity::class.java
                    )
                )
            }) {
                Text(stringResource(R.string.login))
            }
        }
    }

}

@Composable
private fun MainMoreScreen(
    vm: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var saveLocation by remember { mutableStateOf("") }
    var archiveLocation by remember { mutableStateOf("") }
    val user by AuthUtil.loginUser.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        saveLocation =
            "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)}/DCDown/"
        archiveLocation =
            "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)}/DCDown/"
    }

    LazyColumn(verticalArrangement = Arrangement.Center) {
        item {
            Text(
                stringResource(R.string.settings),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 64.dp, 0.dp, 8.dp),
                style = MaterialTheme.typography.headlineLarge
            )
        }
        item {
            PreferenceGroup(stringResource(R.string.account)) {
                user?.let { user ->
                    BasicPreference(user.session?.nickname ?: "", user.id)
                    BasicPreference(stringResource(R.string.login_invalidate), stringResource(R.string.login_invalidate_content)) {
                        coroutineScope.launch {
                            AuthUtil.setAccount(context, AuthUtil.loginUser.value, true)
                        }
                    }
                    BasicPreference(stringResource(R.string.logout), "") {
                        coroutineScope.launch {
                            AuthUtil.setAccount(context, null)
                        }
                    }
                } ?: run {
                    BasicPreference(stringResource(R.string.login), "") {
                        context.startActivity(Intent(context, LoginActivity::class.java))
                    }
                }
            }
        }
        item {
            PreferenceGroup(stringResource(R.string.save)) {
                BasicPreference(stringResource(R.string.save_location), saveLocation)
                BasicPreference(stringResource(R.string.archive_location), archiveLocation)
            }
        }
        item {
            PreferenceGroup(stringResource(R.string.app_info)) {
                BasicPreference(
                    stringResource(R.string.version),
                    content = BuildConfig.VERSION_NAME
                )
                BasicPreference(stringResource(R.string.open_source_license), "") {
                    context.startActivity(Intent(context, OssLicensesMenuActivity::class.java))
                }
            }
        }
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
    object New : Filter(1, Icons.Filled.NewReleases, "신규")
}