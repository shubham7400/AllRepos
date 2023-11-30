package com.blueduck.annotator.enums

sealed class FirestoreDocumentProperties {
    enum class User(val value: String){
        ID("id"),
        NAME("name"),
        PROFILE_IMAGE("profileImage"),
        EMAIL("email"),
        CREATED_AT("createdAt"),
        LAST_SEEN("lastSeen"),
        USER_STATUS("userStatus"),
        USER_TYPE("userType"),
    }

    enum class Project(val value: String){
        ID("id"),
        NAME("name"),
        OWNER_ID("ownerId"),
        IS_PUBLIC("isPublic"),
        DESCRIPTION("description"),
        FILES("files"),
        CREATED_AT("createdAt"),
        NUMBER_OF_FILES("numberOfFiles"),
        PROJECT_FILE_TYPE("projectFileType"),
        FILE_ENCRYPTION_PASSWORD("fileEncryptionPassword"),
        PROJECT_LOCK_PASSWORD("projectLockPassword"),
        EDITOR_ARRAY_ID("editorArrayId"),
        REVIEWER_ARRAY_ID("reviewerArrayId"),
        SIZE("size"),
        PROJECT_TYPE("projectType"),
        PROJECT_STORAGE_TYPE("projectStorageType"),
        LAST_MODIFIED_DATE("lastModifiedDate"),
    }

    enum class File(val value: String){
        ID("id"),
        NAME("name"),
        TAGS("tags"),
        OWNER_ID("ownerId"),
        PROJECT_ID("projectId"),
        META_DATA("metaData"),
        MIME_TYPE("mimeType"),
        CREATED_AT("createdAt"),
        FILE_URL("fileUrl"),
        RAW_DATA("rawData"),
        SIZE("size"),
    }

    enum class Tag(val value: String){
        ID("id"),
        NAME("name"),
        MODIFIED_AT("modifiedAt"),
    }
}

