package com.ibd.dcdown.viewmodels.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ibd.dcdown.repository.ConRepository
import com.ibd.dcdown.repository.DataStoreRepository
import com.ibd.dcdown.tools.ConPack
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val ds: DataStoreRepository, private val cr: ConRepository) :
    ViewModel() {
    private val _eventChannel = Channel<E>()
    val eventChannel = _eventChannel.receiveAsFlow()

    private var hotIdx = 0
    private var newIdx = 0
    private val _hotList = MutableStateFlow<List<ConPack>>(listOf())
    val hotList = _hotList.asStateFlow()
    fun requestHotList(isRefresh: Boolean) = viewModelScope.launch {
        if (isRefresh)
            hotIdx = 0

        val url = "https://dccon.dcinside.com/hot/${hotIdx++}"
        runCatching { cr.requestConPacks(url) }
            .onFailure {
                sendEvent(E.Toast(it.localizedMessage))
            }.onSuccess {
                if (it != null) {
                    _hotList.value = buildList {
                        addAll(_hotList.value)
                        addAll(it)
                    }
                } else {
                    sendEvent(E.Toast("오류가 발생헀습니다."))
                }
            }
    }

    private fun sendEvent(e: E) = viewModelScope.launch {
        _eventChannel.send(e)
    }

    sealed interface E {
        data class Toast(val message: String?) : E
    }
}