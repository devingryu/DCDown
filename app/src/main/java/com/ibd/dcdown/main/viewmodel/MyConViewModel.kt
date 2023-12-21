package com.ibd.dcdown.main.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ibd.dcdown.dto.ConPack
import com.ibd.dcdown.dto.MyCon
import com.ibd.dcdown.dto.MyConResponse
import com.ibd.dcdown.dto.User
import com.ibd.dcdown.repository.ConRepository
import com.ibd.dcdown.repository.DataStoreRepository
import com.ibd.dcdown.tools.C
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyConViewModel @Inject constructor(
    private val cr: ConRepository
): ViewModel() {
    private val _eventChannel = Channel<HomeViewModel.E>()
    val eventChannel = _eventChannel.receiveAsFlow()

    var list = mutableStateListOf<MyCon>()
        private set
    var isRefreshing by mutableStateOf(false)
        private set

    fun requestList(user: User) = viewModelScope.launch {
        if (isRefreshing) return@launch
        list.clear()

        isRefreshing = true
        runCatching { cr.requestMyCons(user) }
            .also {
                isRefreshing = false
            }.onFailure {
                sendEvent(HomeViewModel.E.Toast(it.message))
            }.onSuccess {
                if (it.useList != null)
                    list.addAll(it.useList)
                if (it.unuseList != null)
                    list.addAll(it.unuseList)
            }
    }

    private fun sendEvent(e: HomeViewModel.E) = viewModelScope.launch {
        _eventChannel.send(e)
    }

}