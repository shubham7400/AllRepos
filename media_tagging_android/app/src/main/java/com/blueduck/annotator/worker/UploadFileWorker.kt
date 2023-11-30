package com.blueduck.annotator.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.blueduck.annotator.Notification
import com.blueduck.annotator.R
import com.blueduck.annotator.data.local.MyFileDatabase
import com.blueduck.annotator.encryption.EncryptionAndDecryption
import com.blueduck.annotator.encryption.EncryptionAndDecryption.Companion.encryptImage
import com.blueduck.annotator.encryption.EncryptionAndDecryption.Companion.encryptVideo
import com.blueduck.annotator.enums.CreateProject
import com.blueduck.annotator.enums.FirebaseStorageCollection
import com.blueduck.annotator.enums.FirestoreCollection
import com.blueduck.annotator.enums.ProjectProperties
import com.blueduck.annotator.model.MyFile
import com.blueduck.annotator.util.Constant.ERROR
import com.blueduck.annotator.util.Constant.FILE_URI
import com.blueduck.annotator.util.Constant.PROVIDER
import com.blueduck.annotator.util.createAudioFile
 import com.blueduck.annotator.util.createVideoThumb
 import com.blueduck.annotator.util.fileDetails
import com.blueduck.annotator.util.getJsonObjectOfThumbnailUrl
import com.blueduck.annotator.util.getThumbnailAndSaveToInternalStorage
import com.blueduck.annotator.util.saveBitmapToInternalStorageAndGetUri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

@HiltWorker
class UploadFileWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val firebaseStorage: FirebaseStorage,
    @Assisted private val firebaseFirestore: FirebaseFirestore,
    @Assisted private val localDatabase: MyFileDatabase,
    @Assisted private val parameters: WorkerParameters) :  CoroutineWorker(context, parameters) {
    override suspend fun doWork(): Result {
        setForeground(createForegroundInfo())

        val storageRef = firebaseStorage.reference

        val projectId = parameters.inputData.getString(ProjectProperties.ID.value)!!
        val ownerId = parameters.inputData.getString(ProjectProperties.OWNER_ID.value)!!
        val fileEncryptionPassword = parameters.inputData.getString(ProjectProperties.FILE_ENCRYPTION_PASSWORD.value)!!
        val projectFileType = parameters.inputData.getString(ProjectProperties.PROJECT_FILE_TYPE.value) !!
        var fileUri = parameters.inputData.getString(FILE_URI)!!.toUri()

        return try {
            when(projectFileType){
                CreateProject.FileType.IMAGE.value -> {
                    var thumbImageUri = getThumbnailAndSaveToInternalStorage(fileUri, context = context)
                    if (fileEncryptionPassword.isNotEmpty()){
                        thumbImageUri = encryptImage(thumbImageUri = thumbImageUri!!, fileEncryptionPassword = fileEncryptionPassword, context = context)
                    }
                    val thumbImageUploadTask = storageRef.child("${FirebaseStorageCollection.IMAGES.value}/${thumbImageUri!!.lastPathSegment}").putFile(thumbImageUri).await()
                    if (thumbImageUploadTask.task.isSuccessful){
                        val thumbImageUrl = thumbImageUploadTask.storage.downloadUrl.await().toString()
                        if (fileEncryptionPassword.isNotEmpty()){
                            fileUri = encryptImage(thumbImageUri = fileUri, fileEncryptionPassword = fileEncryptionPassword, context = context)
                        }
                        try {
                            val originalImageUploadTask = storageRef.child("${FirebaseStorageCollection.IMAGES.value}/${fileUri.lastPathSegment}").putFile(fileUri).await()
                            if (originalImageUploadTask.task.isSuccessful){
                                val originalImageUrl = originalImageUploadTask.storage.downloadUrl.await().toString()

                                val fileDetails = fileDetails(uri = fileUri, context = context)

                                if (fileDetails != null){
                                    val myFile = MyFile(
                                        id = UUID.randomUUID().toString(),
                                        name = fileDetails.name,
                                        tags = arrayListOf(),
                                        createdAt = System.currentTimeMillis(),
                                        size = fileDetails.size,
                                        ownerId = ownerId,
                                        projectId = projectId,
                                        metaData = thumbImageUrl.getJsonObjectOfThumbnailUrl(),
                                        mimeType = "",
                                        fileUrl = originalImageUrl,
                                        rawData = null
                                    )
                                    firebaseFirestore.collection(FirestoreCollection.PROJECTS.value).document(myFile.projectId).collection(
                                        FirestoreCollection.FILES.value).document(myFile.id).set(myFile)
                                        .addOnSuccessListener {
                                            CoroutineScope(Dispatchers.Default).launch{
                                                localDatabase.myFileDao().addFile(myFile)
                                            }
                                            Result.success()
                                        }.addOnFailureListener {
                                            Result.failure(workDataOf(ERROR to it.message))
                                        }
                                    Result.success()
                                }else{
                                    Result.failure(workDataOf(ERROR to "Something went wrong."))
                                }

                            }else{
                                Result.failure(workDataOf(ERROR to originalImageUploadTask.task.exception?.message))
                            }

                        }catch (e: Exception){
                            println("dfdddfeeee ${e.message}")
                        }
                        Result.success()
                     }else{
                        Result.failure(workDataOf(ERROR to thumbImageUploadTask.task.exception?.message))
                    }

                }
                CreateProject.FileType.VIDEO.value -> {
                    val bitmap = createVideoThumb(context = context, uri = fileUri)
                    var thumbImageUri = saveBitmapToInternalStorageAndGetUri(bitmap = bitmap!!, context = context)
                    if (fileEncryptionPassword.isNotEmpty()){
                        thumbImageUri = encryptImage(thumbImageUri = thumbImageUri!!, fileEncryptionPassword = fileEncryptionPassword, context = context)
                    }
                    val thumbImageUploadTask = storageRef.child("${FirebaseStorageCollection.IMAGES.value}/${thumbImageUri!!.lastPathSegment}").putFile(thumbImageUri).await()
                    if (thumbImageUploadTask.task.isSuccessful){
                        val thumbImageUrl = thumbImageUploadTask.storage.downloadUrl.await().toString()
                        if (fileEncryptionPassword.isNotEmpty()){
                            fileUri =  encryptVideo(videoUri = fileUri, fileEncryptionPassword = fileEncryptionPassword, context = context)
                        }
                        val originalVideoUploadTask = storageRef.child("${FirebaseStorageCollection.VIDEOS.value}/${fileUri.lastPathSegment}").putFile(fileUri).await()
                        if (originalVideoUploadTask.task.isSuccessful){
                            val originalVideoUrl = originalVideoUploadTask.storage.downloadUrl.await().toString()
                            val fileDetails = fileDetails(uri = fileUri, context = context)

                            if (fileDetails != null){
                                val myFile = MyFile(
                                    id = UUID.randomUUID().toString(),
                                    name = fileDetails.name,
                                    tags = arrayListOf(),
                                    createdAt = System.currentTimeMillis(),
                                    size = fileDetails.size,
                                    ownerId = ownerId,
                                    projectId = projectId,
                                    metaData = thumbImageUrl.getJsonObjectOfThumbnailUrl(),
                                    mimeType = "",
                                    fileUrl = originalVideoUrl,
                                    rawData = null
                                )
                                firebaseFirestore.collection(FirestoreCollection.PROJECTS.value).document(myFile.projectId).collection(
                                    FirestoreCollection.FILES.value).document(myFile.id).set(myFile)
                                    .addOnSuccessListener {
                                        CoroutineScope(Dispatchers.Default).launch{
                                            localDatabase.myFileDao().addFile(myFile)
                                        }
                                        Result.success()
                                    }.addOnFailureListener {
                                        Result.failure(workDataOf(ERROR to it.message))
                                    }
                                Result.success()
                            }else{
                                Result.failure(workDataOf(ERROR to "Something went wrong."))
                            }

                        }else{
                            Result.failure(workDataOf(ERROR to originalVideoUploadTask.task.exception?.message))
                        }
                    }else{
                        Result.failure(workDataOf(ERROR to thumbImageUploadTask.task.exception?.message))
                    }

                }
                CreateProject.FileType.AUDIO.value -> {
                    val fileDetails = fileDetails(uri = fileUri, context = context)

                    if (fileEncryptionPassword.isNotEmpty()) {
                        val encryptedFile = createAudioFile(context = context )
                        EncryptionAndDecryption.encrypt(EncryptionAndDecryption.decryptPassword(fileEncryptionPassword), fileUri, encryptedFile, context)
                        fileUri = FileProvider.getUriForFile(context, "${context.applicationContext.packageName}$PROVIDER", encryptedFile)
                    }
                    val originalAudioUploadTask = storageRef.child("${FirebaseStorageCollection.AUDIOS.value}/${fileUri.lastPathSegment}").putFile(fileUri).await()


                    if (originalAudioUploadTask.task.isSuccessful) {
                        val originalAudioUrl = originalAudioUploadTask.storage.downloadUrl.await().toString()

                        if (fileDetails != null) {
                            val myFile = MyFile(
                                id = UUID.randomUUID().toString(),
                                name = fileDetails.name,
                                tags = arrayListOf(),
                                createdAt = System.currentTimeMillis(),
                                size = fileDetails.size,
                                ownerId = ownerId,
                                projectId = projectId,
                                metaData = "",
                                mimeType = "",
                                fileUrl = originalAudioUrl,
                                rawData = null
                            )
                            firebaseFirestore.collection(FirestoreCollection.PROJECTS.value).document(myFile.projectId).collection(FirestoreCollection.FILES.value).document(myFile.id).set(myFile)
                                .addOnSuccessListener {
                                    CoroutineScope(Dispatchers.Default).launch {
                                        localDatabase.myFileDao().addFile(myFile)
                                    }
                                    Result.success()
                                }.addOnFailureListener {
                                    Result.failure(workDataOf(ERROR to it.message))
                                }
                            Result.success()
                        } else {
                            Result.failure(workDataOf(ERROR to "Something went wrong."))
                        }
                    } else {
                        Result.failure(workDataOf(ERROR to "Something went wrong."))
                    }
                }
                CreateProject.FileType.TEXT.value -> {
                    val fileDetails = fileDetails(uri = fileUri, context = context)

                    if (fileEncryptionPassword.isNotEmpty()){
                        fileUri = encryptImage(thumbImageUri = fileUri, fileEncryptionPassword = fileEncryptionPassword, context = context)
                    }
                    val pdfUploadTask = storageRef.child("${FirebaseStorageCollection.IMAGES.value}/${fileUri.lastPathSegment}").putFile(fileUri).await()
                    if (pdfUploadTask.task.isSuccessful){
                        val pdfUrl = pdfUploadTask.storage.downloadUrl.await().toString()

                        if (fileDetails != null){
                            val myFile = MyFile(
                                id = UUID.randomUUID().toString(),
                                name = fileDetails.name,
                                tags = arrayListOf(),
                                createdAt = System.currentTimeMillis(),
                                size = fileDetails.size,
                                ownerId = ownerId,
                                projectId = projectId,
                                metaData = "",
                                mimeType = "",
                                fileUrl = pdfUrl,
                                rawData = null
                            )
                            firebaseFirestore.collection(FirestoreCollection.PROJECTS.value).document(myFile.projectId).collection(
                                FirestoreCollection.FILES.value).document(myFile.id).set(myFile)
                                .addOnSuccessListener {
                                    CoroutineScope(Dispatchers.Default).launch{
                                        localDatabase.myFileDao().addFile(myFile)
                                    }
                                    Result.success()
                                }.addOnFailureListener {
                                    Result.failure(workDataOf(ERROR to it.message))
                                }
                            Result.success()
                        }else{
                            Result.failure(workDataOf(ERROR to "Something went wrong."))
                        }

                    }else{
                        Result.failure(workDataOf(ERROR to pdfUploadTask.task.exception?.message))
                    }
                }
                else -> return Result.failure()
            }

        }catch (e: Exception){
            Result.failure(workDataOf(ERROR to e.message))
        }

    }



    private fun createForegroundInfo(): ForegroundInfo {
        val id = Notification.NOTIFICATION_CHANNEL_ID
        val title = "Downloading..."
        val cancel = "cancel"
        // This PendingIntent can be used to cancel the worker
        val intent = WorkManager.getInstance(applicationContext)
            .createCancelPendingIntent(getId())

        // Create a Notification channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }

        val notification = NotificationCompat.Builder(applicationContext, id)
            .setContentTitle(title)
            .setTicker(title)
            .setContentText("progress")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setOngoing(true)
            // Add the cancel action to the notification which can
            // be used to cancel the worker
            .addAction(android.R.drawable.ic_delete, cancel, intent)
            .build()

        return ForegroundInfo(Notification.NOTIFICATION_ID, notification)
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                Notification.NOTIFICATION_CHANNEL_ID,
                Notification.NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )

            val manager = applicationContext.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }


    private suspend fun startForeground() {
        setForeground(
            ForegroundInfo(
                Notification.NOTIFICATION_ID,
                NotificationCompat.Builder(context, Notification.NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle("Downloading...")
                    .setContentText("Downloading in progress!")
                    .build()
            )
        )
    }

}