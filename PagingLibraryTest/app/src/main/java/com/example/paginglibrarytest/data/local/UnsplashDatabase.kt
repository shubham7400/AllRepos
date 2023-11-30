package com.example.paginglibrarytest.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.paginglibrarytest.data.local.dao.UnsplashImageDao
import com.example.paginglibrarytest.data.local.dao.UnsplashRemoteKeysDao
import com.example.paginglibrarytest.model.UnsplashImage
import com.example.paginglibrarytest.model.UnsplashRemoteKeys

@Database(entities = [UnsplashImage::class, UnsplashRemoteKeys::class], version = 1)
abstract class UnsplashDatabase : RoomDatabase() {

    abstract fun unsplashImageDao() : UnsplashImageDao

    abstract fun unsplashRemoteKeysDao() : UnsplashRemoteKeysDao

}