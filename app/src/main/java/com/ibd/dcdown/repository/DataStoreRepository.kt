package com.ibd.dcdown.repository

interface DataStoreRepository {
    suspend fun getSaveDirectory(): String?
    suspend fun setSaveDirectory(dir: String)
}