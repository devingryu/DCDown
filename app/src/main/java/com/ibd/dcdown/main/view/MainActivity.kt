package com.ibd.dcdown.main.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.ibd.dcdown.main.composable.MainPage
import com.ibd.dcdown.tools.PermissionUtil
import com.ibd.dcdown.tools.PermissionUtil.requestNotificationPermission
import com.ibd.dcdown.ui.theme.DCDownTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DCDownTheme {
                MainPage()
            }
        }
        requestNotificationPermission()
    }
}