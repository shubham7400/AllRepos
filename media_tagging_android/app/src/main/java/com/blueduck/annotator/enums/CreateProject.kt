package com.blueduck.annotator.enums

sealed class CreateProject {
    enum class FileType(val value: String){
        IMAGE("image"),
        VIDEO("video"),
        AUDIO("audio"),
        TEXT("text"),
    }
    enum class ProjectType(val value: String){
        TAG("tag"),
        SIMILARITY("similarity"),
    }
    enum class StorageType(val value: String){
        LOCAL("local"),
        CLOUD("cloud"),
        DRIVE("drive"),
    }
    enum class PrivacyType(val value: String){
        PASSWORD("password"),
        NONE("none"),
    }

}
