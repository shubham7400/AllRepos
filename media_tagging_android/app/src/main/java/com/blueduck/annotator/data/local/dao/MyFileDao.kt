package com.blueduck.annotator.data.local.dao

import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.blueduck.annotator.model.Project
import com.blueduck.annotator.model.MyFile
import com.blueduck.annotator.model.Tag
import com.blueduck.annotator.model.UploadFile
import com.blueduck.annotator.util.Constant.PROJECT_TABLE
import com.blueduck.annotator.util.Constant.MY_FILE_TABLE
import com.blueduck.annotator.util.Constant.TAG_TABLE
import kotlinx.coroutines.flow.Flow

@Dao
interface MyFileDao {
    @Update
    suspend fun updateFile(file: MyFile)

    @Query("SELECT * FROM $PROJECT_TABLE WHERE projectStorageType = :storageType ORDER BY createdAt DESC")
    fun getAllProjects( storageType: String) : PagingSource<Int, Project>

    @Query("UPDATE $PROJECT_TABLE SET name = :newProjectName WHERE id = :id")
    suspend fun renameProject(id: String, newProjectName: String)

    @Query("UPDATE $PROJECT_TABLE SET projectLockPassword = :password WHERE id = :id")
    suspend fun updateProjectLockPassword(id: String, password: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAllProject(projects: List<Project>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addProject(project: Project)

    @Query("DELETE FROM $PROJECT_TABLE WHERE id = :projectId")
    suspend fun deleteProject(projectId: String)

    @Query("DELETE FROM $PROJECT_TABLE")
    suspend fun deleteAllFolder()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFile(file: MyFile)

    @Query("SELECT * FROM $MY_FILE_TABLE WHERE projectId = :projectId ORDER BY createdAt DESC")
    fun getAllFiles(projectId: String): PagingSource<Int, MyFile>

    @Query("SELECT * FROM $MY_FILE_TABLE WHERE projectId = :projectId ORDER BY createdAt DESC")
    fun getAllFilesFromLocal(projectId: String): Flow<MyFile>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFiles( images: List<MyFile>)

    @Query("SELECT * FROM $MY_FILE_TABLE WHERE id = :id")
    suspend fun getFileByFileId(id: String): MyFile?

    @Query("SELECT * FROM $MY_FILE_TABLE WHERE projectId = :projectId ORDER BY createdAt DESC LIMIT 1")
    suspend fun getFileWithLargestCreationDate(projectId: String): MyFile?

    @Query("DELETE FROM $MY_FILE_TABLE")
    suspend fun deleteAllFiles()

    @Query("UPDATE $MY_FILE_TABLE SET tags = :tags WHERE id = :id")
    suspend fun updateFileTag(tags: ArrayList<String>, id: String)

    @Query("UPDATE $MY_FILE_TABLE SET name = :newName WHERE id = :fileId")
    suspend fun renameFile(newName: String, fileId: String)

    @Query("DELETE FROM $MY_FILE_TABLE WHERE id = :fileId")
    suspend fun deleteFile(fileId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUploadFile(uploadFile: UploadFile)



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAllTags( tags: List<Tag>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTag(tag: Tag)

    @Query("DELETE FROM $TAG_TABLE")
    suspend fun deleteAllTags()

    @Query("SELECT * FROM $TAG_TABLE ORDER BY modifiedAt DESC LIMIT 1")
    suspend fun getTagWithLargestCreationDate(): Tag?

    @Query("SELECT * FROM $TAG_TABLE WHERE id = :tagId")
    suspend fun getTagById(tagId: String): Tag?


}