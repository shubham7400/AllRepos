package com.blueduck.annotator.enums

import androidx.room.PrimaryKey
import com.blueduck.annotator.model.MyFile
import java.io.Serializable
import java.util.UUID

enum class ProjectProperties(val value: String) {

    OWNER_ID("ownerId"),
    ID("id"),
    FILE_ENCRYPTION_PASSWORD("fileEncryptionPassword"),
    PROJECT_FILE_TYPE("projectFileType"),
    PROJECT_STORAGE_TYPE("projectStorageType"),

}



