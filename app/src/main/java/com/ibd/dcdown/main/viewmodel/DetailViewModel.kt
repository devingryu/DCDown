package com.ibd.dcdown.main.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ibd.dcdown.dto.ConData
import com.ibd.dcdown.dto.ConPack
import com.ibd.dcdown.main.repository.ExternalStorageRepository
import com.ibd.dcdown.repository.ConRepository
import com.ibd.dcdown.repository.DataStoreRepository
import com.ibd.dcdown.tools.C
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val ds: DataStoreRepository,
    private val cr: ConRepository,
    private val esr: ExternalStorageRepository,
) : ViewModel() {
    private val _eventChannel = Channel<E>()
    val eventChannel = _eventChannel.receiveAsFlow()

    var data by mutableStateOf<ConPack?>(null)
        private set
    var list = mutableStateListOf<ConData>()
        private set

    var isLoading by mutableStateOf(false)
        private set

    var id: String = ""

    fun toggle(id: String) {
        val idx = list.indexOfFirst { it.id == id }
        if (idx != -1)
            list[idx] = list[idx].copy(selected = !list[idx].selected)
    }

    fun toggleAll() {
        val currentState = list.all { it.selected }
        for (i in list.indices) {
            list[i] = list[i].copy(selected = !currentState)
        }
    }

    fun requestList(id: String, force: Boolean = false) = viewModelScope.launch {
        if (isLoading || (!force && list.isNotEmpty())) return@launch
        isLoading = true

        this@DetailViewModel.id = id
        runCatching { cr.requestConPack(id) }
            .also { isLoading = false }
            .onSuccess {
                data = it
                list.clear()
                list.addAll(it.data)
            }.onFailure {
                sendEvent(E.Toast(it.message))
            }

    }

    private fun requestSave(list: List<ConData>, baseDir: String) = viewModelScope.launch {
        if (list.isEmpty()) return@launch
        val errors = esr.saveImages(
            baseDir,
            list.map { "${it.name}.${it.ext}" to "${C.IMG_BASE_URL}${it.uri}" }
        )
        sendEvent(E.Toast("${list.size - errors.size}/${list.size}개 다운로드 성공"))
    }

    fun requestSaveSelected(ignoreSelection: Boolean) {
        data?.let { data ->
            val dir = "/DCDown/${data.name}/"
            val list = if (ignoreSelection) ArrayList(list) else list.filter { it.selected }
            requestSave(list, dir)
        }
    }

    private fun sendEvent(e: E) = viewModelScope.launch {
        _eventChannel.send(e)
    }

    sealed interface E {
        data class Toast(val message: String?) : E
    }
}