package com.ibd.dcdown.main.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ibd.dcdown.dto.ConPack
import com.ibd.dcdown.repository.ConRepository
import com.ibd.dcdown.repository.DataStoreRepository
import com.ibd.dcdown.tools.C
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val cr: ConRepository
) : ViewModel() {
    private val _eventChannel = Channel<E>()
    val eventChannel = _eventChannel.receiveAsFlow()

    private var idx = 1
    var list = mutableStateListOf<ConPack>()
        private set
    var isRefreshing by mutableStateOf(false)
        private set
    var isLoadingMore by mutableStateOf(false)
        private set
    var hasMore by mutableStateOf(true)
        private set
    var filter by mutableIntStateOf(1)
        private set

    fun requestList(isRefresh: Boolean) = viewModelScope.launch {
        if (isRefreshing || isLoadingMore) return@launch
        if (isRefresh) {
            idx = 1
            hasMore = true
            list.clear()
        }

        val loc = if (filter == C.FILTER_HOT) "hot" else "new"
        val url = "https://dccon.dcinside.com/$loc/${idx++}"

        if (isRefresh)
            isRefreshing = true
        else
            isLoadingMore = true
        runCatching { cr.requestConPacks(url) }
            .also {
                if (isRefresh) isRefreshing = false
                else isLoadingMore = false
            }.onFailure {
                sendEvent(E.Toast(it.message))
            }.onSuccess {
                if (it != null) {
                    if (it.isEmpty()) hasMore = false
                    else list.addAll(it)
                } else {
                    sendEvent(E.Toast("오류가 발생했습니다."))
                }
            }
    }

    private fun sendEvent(e: E) = viewModelScope.launch {
        _eventChannel.send(e)
    }

    fun changeFilter(@C.FilterType filter: Int) {
        if (this.filter != filter) {
            this.filter = filter
            requestList(true)
        }
    }

    sealed interface E {
        data class Toast(val message: String?) : E
    }
}