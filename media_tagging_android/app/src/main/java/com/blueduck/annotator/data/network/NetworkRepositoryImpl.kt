package com.blueduck.annotator.data.network

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.blueduck.annotator.data.local.MyFileDatabase
import com.blueduck.annotator.data.paging.FirestoreFileRemoteMediator
import com.blueduck.annotator.data.paging.FirestoreProjectRemoteMediator
import com.blueduck.annotator.enums.FirestoreCollection
import com.blueduck.annotator.enums.FirestoreDocumentProperties
import com.blueduck.annotator.model.MyFile
import com.blueduck.annotator.model.Project
import com.blueduck.annotator.model.Tag
import com.blueduck.annotator.model.User
import com.blueduck.annotator.preferences.getTagCreatedAt
import com.blueduck.annotator.preferences.setTagCreatedAt
import com.blueduck.annotator.util.Constant
import com.blueduck.annotator.util.Response
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class NetworkRepositoryImpl @Inject constructor(
    private val localDatabase: MyFileDatabase,
    private val networkDatabase: FirebaseFirestore,
    val context: Context
) : NetworkRepository {

    override suspend fun getCurrentUser(uid: String): CurrentUserResponse {
        return try {
            val querySnapshot = networkDatabase.collection("users").whereEqualTo("userId", uid).get().await()
            return if (querySnapshot != null) {
                val result = querySnapshot.firstOrNull()?.data
                if (result != null) {
                    val jsonObject = Gson().toJson(result)
                    val user = Gson().fromJson(jsonObject.toString(), User::class.java)
                    Response.Success(user)
                }else{
                    Response.Failure(Exception("Something Went Wrong"))
                }
            }else{
                Response.Failure(Exception("Something Went Wrong"))
            }
        }catch (e: Exception){
            Response.Failure(Exception("Something Went Wrong ${e.message}"))
        }
    }




    @OptIn(ExperimentalPagingApi::class)
    override  fun getAllProjects(userId: String, storageType: String): Flow<PagingData<Project>> {
        val pagingSourceFactory = { localDatabase.myFileDao().getAllProjects(storageType) }
        return Pager(
            config = PagingConfig(pageSize = Constant.ITEMS_PER_PAGE),
            remoteMediator = FirestoreProjectRemoteMediator(localDatabase, networkDatabase, userId, context),
            pagingSourceFactory = pagingSourceFactory
        ).flow

        /*return Pager(
            config = PagingConfig(pageSize = Constant.ITEMS_PER_PAGE)
        ) {
            FirestorePagingSource(networkDatabase, userId, context)
        }.flow*/
     }



    override suspend fun addProject(project: Project, result: (AddProjectResponse) -> Unit) {
        result(Response.Loading)
        networkDatabase.collection(FirestoreCollection.PROJECTS.value).document(project.id).set(project).addOnSuccessListener {
            result(Response.Success(project))
        }.addOnFailureListener {
            result(Response.Failure(it))
        }
    }

    override suspend fun deleteProject(projectId: String, result: (DeleteProjectResponse) -> Unit) {
        result(Response.Loading)
        networkDatabase.collection(FirestoreCollection.PROJECTS.value).document(projectId).delete().addOnSuccessListener {
            result(Response.Success(true))
        }.addOnFailureListener {
            result(Response.Failure(it))
        }
    }

    override fun updateProjectLockPassword(password: String, selectedProjectId: String, result: (UpdateProjectLockPasswordResponse) -> Unit) {
        result(Response.Loading)
        networkDatabase.collection(FirestoreCollection.PROJECTS.value).document(selectedProjectId).update(FirestoreDocumentProperties.Project.PROJECT_LOCK_PASSWORD.value, password).addOnSuccessListener {
            result(Response.Success(true))
        }.addOnFailureListener{
            result(Response.Failure(it))
        }
    }

    override suspend fun renameProject(projectId: String, newProjectName: String,  result: (RenameProjectResponse) -> Unit) {
        result(Response.Loading)
         networkDatabase.collection(FirestoreCollection.PROJECTS.value).document(projectId).update(FirestoreDocumentProperties.Project.NAME.value, newProjectName).addOnSuccessListener {
             result(Response.Success(true))
         }.addOnFailureListener{
             result(Response.Failure(it))
         }
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getAllFiles(projectId: String): Flow<PagingData<MyFile>> {
        val pagingSourceFactory = { localDatabase.myFileDao().getAllFiles(projectId) }
        return Pager(
            config = PagingConfig(pageSize = Constant.ITEMS_PER_PAGE),
            remoteMediator = FirestoreFileRemoteMediator(
                context = context,
                projectId = projectId,
                firestore = networkDatabase,
                myFileDatabase = localDatabase
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    override fun getAllFilesFromLocal(projectId: String): Flow<PagingData<MyFile>> {
        val pagingSourceFactory = { localDatabase.myFileDao().getAllFiles(projectId) }
        return Pager(
            config = PagingConfig(pageSize = Constant.ITEMS_PER_PAGE),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    override  fun getAllTags(projectId: String, onSuccess: () -> Unit)  {

        CoroutineScope(Dispatchers.Default).launch {
            if(context.getTagCreatedAt() == 0.toLong()){
                localDatabase.myFileDao().deleteAllTags()
                println("dfdsdfdf deleted")
            }

            // Retrieve data from Firestore using a query with limit and offset
            val tag = localDatabase.myFileDao().getTagWithLargestCreationDate()
            val querySnapshot = networkDatabase.collection(FirestoreCollection.PROJECTS.value).document(projectId).collection(
                FirestoreCollection.TAGS.value)
                .orderBy(FirestoreDocumentProperties.Tag.MODIFIED_AT.value)
                .whereGreaterThan(FirestoreDocumentProperties.Tag.MODIFIED_AT.value, tag?.modifiedAt ?: 0).get().await()
            val tags = ArrayList<Tag>()
            if (querySnapshot != null){
                for (doc in querySnapshot) {
                    val jsonObject = Gson().toJson(doc.data)
                    val t = Gson().fromJson(jsonObject.toString(), Tag::class.java)
                    tags.add(t)
                    context.setTagCreatedAt(t.modifiedAt)
                }
            }



            println("fsdfsfdfddf ${tags.size}")
            // Save the retrieved data to Room
            if (tags.isNotEmpty()){
                localDatabase.myFileDao().addAllTags(tags)
                onSuccess()
            }
        }

    }


    override suspend fun addTag(tag: Tag, projectId: String, result: (AddTagResponse) -> Unit) {
        result(Response.Loading)
         try {
            val tagsCollection = networkDatabase.collection(FirestoreCollection.PROJECTS.value).document(projectId).collection(FirestoreCollection.TAGS.value)
            val querySnapshot = tagsCollection.whereEqualTo(FirestoreDocumentProperties.Tag.NAME.value, tag.name).get().await()
            val docSnapshot = querySnapshot.documents.firstOrNull()
            if (docSnapshot != null){
                val existingTag = Tag(
                    id = docSnapshot.getString(FirestoreDocumentProperties.Tag.ID.value)!!,
                    name = docSnapshot.getString(FirestoreDocumentProperties.Tag.NAME.value)!!,
                    modifiedAt = docSnapshot.getLong(FirestoreDocumentProperties.Tag.MODIFIED_AT.value)!!,
                )
                result(Response.Success(existingTag))
            }else{
                networkDatabase.collection(FirestoreCollection.PROJECTS.value).document(projectId).collection(FirestoreCollection.TAGS.value).document(tag.id).set(tag).await()
                result(Response.Success(tag))
            }
        }catch (e: Exception){
             result(Response.Failure(e))
        }
    }

    override suspend fun updateFileTag(file: MyFile, projectId: String, result: (UpdateTagResponse) -> Unit) {
        result(Response.Loading)
        try {
            println("sfsdfsd ${file.id}  $projectId")
            networkDatabase.collection(FirestoreCollection.PROJECTS.value).document(projectId)
                .collection(FirestoreCollection.FILES.value).document(file.id).update(FirestoreDocumentProperties.File.TAGS.value, file.tags)
                .addOnSuccessListener {
                    result(Response.Success(true))
                }.addOnFailureListener {
                    result(Response.Failure(it))
                }
        }catch (e: Exception){
            result(Response.Failure(e))
        }
    }

    override suspend fun renameFile(
        newName: String,
        fileId: String,
        projectId: String,
        result: (RenameFileResponse) -> Unit
    ) {
        result(Response.Loading)
        try {
            networkDatabase.collection(FirestoreCollection.PROJECTS.value).document(projectId)
                .collection(FirestoreCollection.FILES.value).document(fileId).update(FirestoreDocumentProperties.File.NAME.value, newName)
                .addOnSuccessListener {
                    result(Response.Success(true))
                }.addOnFailureListener {
                    result(Response.Failure(it))
                }
        }catch (e: Exception){
            result(Response.Failure(e))
        }
    }

    override suspend fun deleteFile(projectId: String, fileId: String, result: (DeleteFileResponse) -> Unit) {
        result(Response.Loading)
        networkDatabase.collection(FirestoreCollection.PROJECTS.value).document(projectId).collection(FirestoreCollection.FILES.value).document(fileId).delete().addOnSuccessListener {
            result(Response.Success(true))
        }.addOnFailureListener {
            result(Response.Failure(it))
        }
    }
}