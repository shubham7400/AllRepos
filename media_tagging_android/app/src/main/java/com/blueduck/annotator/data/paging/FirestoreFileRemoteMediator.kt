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
import com.blueduck.annotator.model.MyFile
import com.blueduck.annotator.preferences.getFileCreatedAt
import com.blueduck.annotator.preferences.setFileCreatedAt
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalPagingApi::class)
class FirestoreFileRemoteMediator(private val context: Context, val projectId: String, private val firestore: FirebaseFirestore, private val myFileDatabase: MyFileDatabase) : RemoteMediator<Int, MyFile>() {

     override suspend fun load(loadType: LoadType, state: PagingState<Int, MyFile>): MediatorResult {
        return try {

            if(context.getFileCreatedAt() == 0.toLong()){
                myFileDatabase.myFileDao().deleteAllFiles()
                println("dfdsdfdf deleted")
            }

            // Retrieve data from Firestore using a query with limit and offset
            val file = myFileDatabase.myFileDao().getFileWithLargestCreationDate(projectId)
            val querySnapshot = firestore.collection(FirestoreCollection.PROJECTS.value).document(projectId).collection(FirestoreCollection.FILES.value)
                .orderBy(FirestoreDocumentProperties.File.CREATED_AT.value)
                .whereGreaterThan(FirestoreDocumentProperties.File.CREATED_AT.value, file?.createdAt ?: 0).get().await()
            val files = ArrayList<MyFile>()
            if (querySnapshot != null){
                for (doc in querySnapshot) {
                    val jsonObject = Gson().toJson(doc.data)
                    val f = Gson().fromJson(jsonObject.toString(), MyFile::class.java)
                    files.add(f)
                    context.setFileCreatedAt(f.createdAt)
                }
            }



            println("fsdfsfdfddf ${files.size}")
            // Save the retrieved data to Room
            if (files.isNotEmpty()){
                myFileDatabase.myFileDao().addFiles(files)
            }

            val endOfPaginationReached = files.isEmpty()

            // Return success with endOfPaginationReached=true if no more data available
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: Exception) {
            // Return error if data retrieval fails
            println("fsdfds ${exception.message}")
            MediatorResult.Error(exception)
        }
    }

}
