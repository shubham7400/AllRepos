package com.blueduck.annotator.data.network

import androidx.paging.PagingData
import com.blueduck.annotator.model.MyFile
import com.blueduck.annotator.model.Project
import com.blueduck.annotator.model.Tag
import com.blueduck.annotator.model.User
import com.blueduck.annotator.util.Response
import kotlinx.coroutines.flow.Flow


typealias CurrentUserResponse = Response<User>
typealias GetAllFolderResponse = Response<ArrayList<Project>>
typealias AddProjectResponse = Response<Project>
typealias RenameProjectResponse = Response<Boolean>
typealias DeleteProjectResponse = Response<Boolean>
typealias UpdateProjectLockPasswordResponse = Response<Boolean>
typealias AddTagResponse = Response<Tag>
typealias UpdateTagResponse = Response<Boolean>
typealias RenameFileResponse = Response<Boolean>
typealias DeleteFileResponse = Response<Boolean>



interface NetworkRepository {
    suspend fun getCurrentUser(uid: String): CurrentUserResponse

    fun getAllProjects(userId: String, storageType: String) : Flow<PagingData<Project>>
    suspend fun addProject(project: Project, result: (AddProjectResponse) -> Unit)
    suspend fun deleteProject(projectId: String, result: (DeleteProjectResponse) -> Unit)

    fun updateProjectLockPassword(password: String, selectedProjectId: String, result: (UpdateProjectLockPasswordResponse) -> Unit)
    suspend fun renameProject(projectId: String, newProjectName: String, result: (RenameProjectResponse) -> Unit)

    fun getAllFiles(projectId: String): Flow<PagingData<MyFile>>

    fun getAllFilesFromLocal(projectId: String): Flow<PagingData<MyFile>>



    fun getAllTags(projectId: String, onSuccess: () -> Unit)

    suspend fun addTag(tag: Tag, projectId: String, result: (AddTagResponse) -> Unit)
    suspend fun updateFileTag(file: MyFile, projectId: String,  result: (UpdateTagResponse) -> Unit)
    suspend fun renameFile(newName: String, fileId: String, projectId: String, result: (RenameFileResponse) -> Unit)
    suspend fun deleteFile(projectId: String, fileId: String, result: (DeleteFileResponse) -> Unit)


}