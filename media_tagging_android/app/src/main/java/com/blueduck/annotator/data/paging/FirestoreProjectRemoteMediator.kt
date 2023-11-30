package com.blueduck.annotator.data.paging

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.blueduck.annotator.data.local.MyFileDatabase
import com.blueduck.annotator.enums.FirestoreCollection
import com.blueduck.annotator.enums.FirestoreDocumentProperties
import com.blueduck.annotator.model.Project
import com.blueduck.annotator.preferences.getFolderCreatedAt
import com.blueduck.annotator.preferences.setFolderCreatedAt
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalPagingApi::class)
class FirestoreProjectRemoteMediator(
    private val localDatabase: MyFileDatabase,
    private val networkDatabase: FirebaseFirestore,
    val userId: String,
    val context: Context,
) : RemoteMediator<Int, Project>() {
     override suspend fun load(loadType: LoadType, state: PagingState<Int, Project>): MediatorResult {
         return try {
            /* val lastTimestamp = when (loadType) {
                 LoadType.REFRESH -> null
                 LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                 LoadType.APPEND -> {
                     val lastItem = state.lastItemOrNull()
                     lastItem?.createdAt ?: return MediatorResult.Success(endOfPaginationReached = true)
                 }
             }

             val query = if (lastTimestamp != null) {
                 networkDatabase.collection(FirestoreCollection.FOLDERS.value).whereEqualTo(FirestoreDocument.USER_ID.value, userId)
                     .orderBy(FirestoreDocument.CREATED_AT.value)
                     .startAfter(lastTimestamp)
                     .limit(10)
             } else {
                 networkDatabase.collection(FirestoreCollection.FOLDERS.value).whereEqualTo(FirestoreDocument.USER_ID.value, userId)
                     .orderBy(FirestoreDocument.CREATED_AT.value)
                     .limit(10)
             }

             val documents = query.get().await()
             val folders = mutableListOf<Folder>()
             for (document in documents) {
                 val jsonObject = Gson().toJson(document.data)
                 val project = Gson().fromJson(jsonObject.toString(), Folder::class.java)
                 folders.add(project)
                // context.setFolderCreatedAt(project.createdAt)
             }

             val nextTimestamp = documents.documents.lastOrNull()?.getLong(FirestoreDocument.CREATED_AT.value) ?: lastTimestamp

             // Store data in the local Room database
             if (loadType == LoadType.REFRESH) {
                 localDatabase.myFileDao().deleteAllFolder()
             }
             localDatabase.myFileDao().addAllFolder(folders)

             println("fdfdsf ${documents.isEmpty}")
             return MediatorResult.Success(
                 endOfPaginationReached = documents.isEmpty,
                // nextKey = nextTimestamp
             )*/
             if(context.getFolderCreatedAt() == 0.toLong()){
                 localDatabase.myFileDao().deleteAllFolder()
                 println("dfdsdfdf deleted")
             }
            /* if (LoadType.REFRESH == loadType){
                 context.setFolderCreatedAt(0)
                 localDatabase.myFileDao().deleteAllFolder()
             }*/
             println("dfdfkkdfd ${loadType.name}")
             val querySnapshot = networkDatabase.collection(FirestoreCollection.PROJECTS.value)
                 .whereEqualTo(FirestoreDocumentProperties.Project.OWNER_ID.value, userId)
                 .orderBy(FirestoreDocumentProperties.Project.CREATED_AT.value)
                 .whereGreaterThan(FirestoreDocumentProperties.Project.CREATED_AT.value, context.getFolderCreatedAt()).limit(10).get().await()
             val projects = ArrayList<Project>()
             println("dfsdlfds size ${querySnapshot.size()}")
             if (querySnapshot != null){
                 for (doc in querySnapshot) {
                     val jsonObject = Gson().toJson(doc.data)
                     val project = Gson().fromJson(jsonObject.toString(), Project::class.java)
                     projects.add(project)
                     context.setFolderCreatedAt(project.createdAt)
                 }
             }
             if (projects.isNotEmpty()) {
                 localDatabase.myFileDao().addAllProject(projects)
             }
             //val endOfPaginationReached = folders.isEmpty()

             MediatorResult.Success(endOfPaginationReached = false)
         }catch (e: Exception){
             println("fdsfsd ${e.message}")
             MediatorResult.Error(e)
         }
    }

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }
}


/*
class FirestorePagingSource(val networkDatabase: FirebaseFirestore, val userId: String, context: Context) : PagingSource<QuerySnapshot, Folder>() {
    override fun getRefreshKey(state: PagingState<QuerySnapshot, Folder>): QuerySnapshot? = null

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, Folder> {
        return try {
            val query = networkDatabase.collection(FirestoreCollection.FOLDERS.value).whereEqualTo(FirestoreDocument.USER_ID.value, userId).orderBy(FirestoreDocument.CREATED_AT.value).limit(10)

            val currentPage = params.key ?: query.get().await()
            val lastVisibleProduct = currentPage.documents[currentPage.size() - 1]
            val nextPage = query.startAfter(lastVisibleProduct).get().await()

            val folders = ArrayList<Folder>()
            for (doc in currentPage) {
                val jsonObject = Gson().toJson(doc.data)
                val project = Gson().fromJson(jsonObject.toString(), Folder::class.java)
                folders.add(project)
                //context.setFolderCreatedAt(project.createdAt)
            }

            LoadResult.Page(
                data = folders,
                prevKey = null,
                nextKey = nextPage
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}*/
