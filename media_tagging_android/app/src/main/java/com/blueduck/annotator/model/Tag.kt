package com.blueduck.annotator.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.blueduck.annotator.util.Constant
import java.io.Serializable

@kotlinx.serialization.Serializable
@Entity(tableName = Constant.TAG_TABLE)
data class Tag(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val name: String,
    val modifiedAt: Long) : Serializable