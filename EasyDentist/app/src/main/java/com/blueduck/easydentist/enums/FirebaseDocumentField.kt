package com.blueduck.easydentist.enums


// By using this enum class, you can avoid hardcoding the names of fields of a document in a Firebase database and reduce the chance of errors caused by typos.
enum class FirebaseDocumentField(val value: String) {
    ACCOUNT_NAME("accountName"),
    PASSWORD("password"),
    EMAIL("email"),
    NAME("name"),
    POSITION("position"),
    POST_ID("postId"),
    USER_ID("userId"),
    DIAGNOSIS_REPORT("diagnosisReport"),
    POST_IMAGES("postImages"),
    CREATED_AT("createdAt"),
    DATE("date"),
}