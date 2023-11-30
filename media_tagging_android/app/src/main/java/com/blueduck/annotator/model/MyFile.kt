package com.blueduck.annotator.model

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.blueduck.annotator.util.Constant.MY_FILE_TABLE
import com.blueduck.annotator.util.Constant.THUMBNAIL_URI
import com.blueduck.annotator.util.Constant.THUMBNAIL_URL
import kotlinx.serialization.Serializable
import org.json.JSONObject

@Serializable
@Entity(tableName = MY_FILE_TABLE)
data class MyFile(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val name: String,
    val tags: ArrayList<String>,
    val size: String,
    val createdAt: Long,
    val ownerId: String,
    val projectId: String,
    val metaData: String,
    val mimeType: String,
    val fileUrl: String,
    var rawData: ByteArray?
) : java.io.Serializable {
    // Override 'equals()' method to compare the contents of the array
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MyFile

        if (id != other.id) return false
        if (!rawData.contentEquals(other.rawData)) return false

        return true
    }

    // Override 'hashCode()' method to consider the contents of the array
    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + rawData.contentHashCode()
        return result
    }

    fun getThumbnailUri() : Uri?{
        return try {
            val thumbnailUriString = JSONObject(metaData).getString(THUMBNAIL_URI)
            Uri.parse(thumbnailUriString)
        }catch (e: Exception){
            null
        }
    }

    fun getThumbnailUrl() : String?{
        return try {
            JSONObject(metaData).getString(THUMBNAIL_URL)
        }catch (e: Exception){
            null
        }
    }

}