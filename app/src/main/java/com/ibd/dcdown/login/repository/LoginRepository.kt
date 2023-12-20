package com.ibd.dcdown.login.repository

import com.ibd.dcdown.dto.User

interface LoginRepository {
    suspend fun login(id: String, pw: String): User
}