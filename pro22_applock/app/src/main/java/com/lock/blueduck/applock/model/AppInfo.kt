package com.lock.blueduck.applock.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.lock.blueduck.applock.util.Constants.APP_INFO_TABLE
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = APP_INFO_TABLE, indices = [Index(value = ["packageName"], unique = true)])
data class AppInfo(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val appName: String,
    val packageName: String,
    var isLocked: Boolean,
    val appIcon: ByteArray
    ) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AppInfo

        if (!appIcon.contentEquals(other.appIcon)) return false

        return true
    }

    override fun hashCode(): Int {
        return appIcon.contentHashCode()
    }
}