package com.ibd.dcdown.tools

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object Extensions {
    fun AppCompatActivity.repeatOnStarted(block: suspend CoroutineScope.() -> Unit) {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED, block)
        }
    }

    fun Fragment.repeatOnStarted(block: suspend CoroutineScope.() -> Unit) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED, block)
        }
    }
}