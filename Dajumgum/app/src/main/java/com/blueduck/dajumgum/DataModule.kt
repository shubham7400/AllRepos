package com.blueduck.dajumgum

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

private const val DATA_SOURCE_NAME = "DelishDataStore"

@InstallIn(SingletonComponent::class)
@Module
class SharedModule {

    @Singleton
    @Provides
    fun provideDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { emptyPreferences() }
            ),
            scope = CoroutineScope(Dispatchers.IO),
            produceFile = { context.preferencesDataStoreFile(DATA_SOURCE_NAME) }
        )
    }

    @Singleton
    @Provides
    fun provideDataStoreRepository(dataStore: DataStore<Preferences>): DataStoreOperations =
        DataStoreRepository(dataStore)

          
}