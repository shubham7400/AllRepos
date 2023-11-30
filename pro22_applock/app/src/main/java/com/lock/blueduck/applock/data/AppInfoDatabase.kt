package com.lock.blueduck.applock.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.lock.blueduck.applock.data.dao.AppInfoDao
import com.lock.blueduck.applock.model.AppInfo

@Database(entities = [AppInfo::class], version = 1)
abstract class AppInfoDatabase : RoomDatabase() {

    abstract fun appInfoDao() : AppInfoDao

}