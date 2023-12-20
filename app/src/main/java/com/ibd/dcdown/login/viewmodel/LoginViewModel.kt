package com.ibd.dcdown.login.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ibd.dcdown.dto.User
import com.ibd.dcdown.login.repository.LoginRepository
import com.ibd.dcdown.login.repository.LoginRepositoryImpl
import com.ibd.dcdown.tools.AuthUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val lr: LoginRepository) : ViewModel() {
    var isProcessing by mutableStateOf(false)
        private set
    private val _eventChannel = Channel<E>()
    val eventChannel = _eventChannel.receiveAsFlow()

    fun login(id: String, pw: String) = viewModelScope.launch {
        isProcessing = true
        runCatching {
            lr.login(id, pw)
        }.also {
            isProcessing = false
        }.onSuccess {
            _eventChannel.send(E.LoginEnd(it))
        }.onFailure {
            _eventChannel.send(E.Toast(it.message))
        }
    }

    sealed interface E {
        data class Toast(val message: String?) : E
        data class LoginEnd(val user: User): E
    }
}