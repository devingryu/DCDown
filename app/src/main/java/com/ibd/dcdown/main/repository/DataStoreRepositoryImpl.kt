package com.ibd.dcdown.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.ibd.dcdown.tools.dataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : DataStoreRepository {
    override suspend fun getSaveDirectory(): String? = runCatching {
        val currentState = context.dataStore.data.first()
        currentState[Keys.saveDir]
    }.getOrNull()


    override suspend fun setSaveDirectory(dir: String) {
        context.dataStore.edit {
            it[Keys.saveDir] = dir
        }
    }

    private object Keys {
        val saveDir = stringPreferencesKey("save-dir")
    }
}