package com.ibd.dcdown.login.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.ibd.dcdown.login.composable.LoginPage
import com.ibd.dcdown.main.composable.MainPage
import com.ibd.dcdown.ui.theme.DCDownTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DCDownTheme {
                LoginPage()
            }
        }
    }
}