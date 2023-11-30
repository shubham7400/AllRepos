package com.blueduck.annotator.model

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.blueduck.annotator.util.Constant
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = Constant.UPLOAD_FILE_TABLE)
data class UploadFile(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val name: String,
    val size: String,
    val ownerId: String,
    val projectId: String,
    val fileType: String,
    val fileUrl: String,
    val thumbnailUrl: String,
    val fileUri: String,
    val thumbnailUri: String,
): java.io.Serializable



