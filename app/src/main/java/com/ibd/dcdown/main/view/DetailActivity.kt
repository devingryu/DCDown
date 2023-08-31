package com.ibd.dcdown.main.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.ibd.dcdown.main.composable.DetailPage
import com.ibd.dcdown.ui.theme.DCDownTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailActivity: ComponentActivity() {

    private val id by lazy { intent.extras?.getString("id") }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val id = id ?: run { finish(); return }
        setContent {
            DCDownTheme {
                DetailPage(id)
            }
        }
    }
}