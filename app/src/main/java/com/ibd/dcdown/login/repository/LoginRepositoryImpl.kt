package com.ibd.dcdown.login.repository

import android.content.Context
import com.ibd.dcdown.dto.User
import com.ibd.dcdown.main.viewmodel.HomeViewModel
import com.ibd.dcdown.tools.AuthUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
): LoginRepository {

    override suspend fun login(id: String, pw: String) =
        AuthUtil.setAccount(context, User(id, pw))

}