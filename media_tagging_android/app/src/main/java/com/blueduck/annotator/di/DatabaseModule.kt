package com.blueduck.annotator.di

import android.content.Context
import androidx.media3.exoplayer.ExoPlayer
import androidx.room.Room
import com.blueduck.annotator.data.local.MyFileDatabase
import com.blueduck.annotator.util.Constant.MY_FILE_DATABASE
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
    fun provideDatabase( @ApplicationContext context: Context) : MyFileDatabase {
        return Room.databaseBuilder(context, MyFileDatabase::class.java, MY_FILE_DATABASE).build()
    }


    @Provides
    @Singleton
    fun provideExoPLayer(context: Context): ExoPlayer {
        return ExoPlayer.Builder(context).build()
    }

}