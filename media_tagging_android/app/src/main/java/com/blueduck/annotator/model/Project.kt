package com.blueduck.annotator.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.blueduck.annotator.util.Constant
import java.io.Serializable

@kotlinx.serialization.Serializable
@Entity(tableName = Constant.PROJECT_TABLE)
data class Project(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val ownerId: String,
    var name : String,
    val isPublic: Boolean,
    var description : String,
    val createdAt: Long,
    val numberOfFiles: Int,
    val projectFileType: String,
    val fileEncryptionPassword: String,
    val projectLockPassword: String,
    val editorArrayId: ArrayList<String>,
    val reviewerArrayId: ArrayList<String>,
    val size: String,
    val projectType: String,
    val projectStorageType: String,
    val lastModifiedDate: Long) : Serializable
