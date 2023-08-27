package com.ibd.dcdown.main.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ibd.dcdown.dto.ConPack
import com.ibd.dcdown.repository.ConRepository
import com.ibd.dcdown.repository.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val ds: DataStoreRepository, private val cr: ConRepository) :
    ViewModel() {
    private val _eventChannel = Channel<E>()
    val eventChannel = _eventChannel.receiveAsFlow()

    private var idx = 0
    var list = mutableStateListOf<ConPack>()
        private set

    fun requestHotList(isRefresh: Boolean) = viewModelScope.launch {
        if (isRefresh)
            idx = 0

        val url = "https://dccon.dcinside.com/hot/${idx++}"
        runCatching { cr.requestConPacks(url) }
            .onFailure {
                sendEvent(E.Toast(it.localizedMessage))
            }.onSuccess {
                if (it != null) {
                    list.addAll(it)
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