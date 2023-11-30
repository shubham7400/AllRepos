package com.blueduck.dajumgum

import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow

interface DataStoreOperations {
    suspend fun save(key: Preferences.Key<Boolean>, value: Boolean)
    fun read(key: Preferences.Key<Boolean>): Flow<Boolean>
}