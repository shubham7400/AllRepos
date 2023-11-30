package com.lock.blueduck.applock.di

import android.content.Context
import androidx.room.Room
import com.lock.blueduck.applock.data.AppInfoDatabase
import com.lock.blueduck.applock.repository.Repository
import com.lock.blueduck.applock.util.Constants.APP_INFO_DATABASE
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase( @ApplicationContext context: Context) : AppInfoDatabase {
        return Room.databaseBuilder(context, AppInfoDatabase::class.java, APP_INFO_DATABASE).build()
    }

    @Provides
    @Singleton
    fun provideRepository(database: AppInfoDatabase) : Repository {
        return Repository(database)
    }
}