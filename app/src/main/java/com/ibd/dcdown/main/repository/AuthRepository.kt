package com.ibd.dcdown.main.repository

import com.ibd.dcdown.proto.Checkin

interface AuthRepository {
    suspend fun requestCheckin(): Checkin.AndroidCheckinResponse
}