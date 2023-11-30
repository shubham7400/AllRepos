package com.blueduck.annotator.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.blueduck.annotator.data.local.dao.MyFileDao
import com.blueduck.annotator.model.Project
import com.blueduck.annotator.model.MyFile
import com.blueduck.annotator.model.Tag
import com.blueduck.annotator.model.UploadFile
import com.blueduck.annotator.util.Converters

@Database(entities = [MyFile::class, Project::class, Tag::class, UploadFile::class], version = 10)
@TypeConverters(Converters::class)
abstract class MyFileDatabase : RoomDatabase() {
    abstract fun myFileDao() : MyFileDao
}