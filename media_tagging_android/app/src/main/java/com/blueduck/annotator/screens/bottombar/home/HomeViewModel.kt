package com.blueduck.annotator.screens.bottombar.home

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.Util
import androidx.media3.datasource.DefaultDataSourceFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.cachedIn
import com.blueduck.annotator.encryption.EncryptionAndDecryption
import com.blueduck.annotator.data.local.MyFileDatabase
import com.blueduck.annotator.data.network.AddProjectResponse
import com.blueduck.annotator.data.network.AddTagResponse
import com.blueduck.annotator.model.MyFile
import com.blueduck.annotator.model.User
import com.blueduck.annotator.data.network.NetworkRepository
import com.blueduck.annotator.data.network.DeleteFileResponse
import com.blueduck.annotator.data.network.DeleteProjectResponse
import com.blueduck.annotator.data.network.RenameFileResponse
import com.blueduck.annotator.data.network.RenameProjectResponse
import com.blueduck.annotator.data.network.UpdateProjectLockPasswordResponse
import com.blueduck.annotator.data.network.UpdateTagResponse
import com.blueduck.annotator.encryption.EncryptedFileDataSourceFactory
import com.blueduck.annotator.enums.CreateProject
import com.blueduck.annotator.model.Project
import com.blueduck.annotator.model.Tag
import com.blueduck.annotator.model.UploadFile
import com.blueduck.annotator.util.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: NetworkRepository, private val localDatabase: MyFileDatabase) : ViewModel(),  Player.Listener {


    var selectedFile: MyFile? = null
    var files: List<MyFile> = emptyList()
    var storageType: String = CreateProject.StorageType.CLOUD.value

    private var player: ExoPlayer? = null

    var selectedProject: Project? = null
    var user = MutableLiveData<User>()

    fun addUploadFile(uploadFile: UploadFile) {
        viewModelScope.launch {
            localDatabase.myFileDao().addUploadFile(uploadFile)
        }
    }

    fun addFile(file: MyFile) {
        viewModelScope.launch {
            localDatabase.myFileDao().addFile(file)
        }
    }

    fun getAllFiles(projectId: String): Flow<PagingData<MyFile>> {
        return repository.getAllFiles(projectId)
    }

    fun getAllFilesFromLocal(projectId: String): Flow<PagingData<MyFile>> {
        return repository.getAllFilesFromLocal(projectId = projectId).cachedIn(viewModelScope)
    }

    fun updateFile(file: MyFile) {
        viewModelScope.launch {
            localDatabase.myFileDao().updateFile(file)
        }
    }

    fun getAllProjects(userId: String) : Flow<PagingData<Project>>{
        return repository.getAllProjects(userId, storageType).cachedIn(viewModelScope)
    }

     fun addProject(project: Project, result: (AddProjectResponse) -> Unit) = viewModelScope.launch {
         if (storageType == CreateProject.StorageType.CLOUD.value){
             repository.addProject(project){
                 result(it)
                 when(it){
                     is Response.Success -> {
                         viewModelScope.launch {
                             localDatabase.myFileDao().addProject(project)
                         }
                     }
                     else -> {}
                 }
             }
         }else{
             localDatabase.myFileDao().addProject(project)
             result(Response.Success(project))
         }

    }

    fun getFileByFileId(fileId: String, result: (MyFile?) -> Unit) {
        viewModelScope.launch {
            val file = localDatabase.myFileDao().getFileByFileId(fileId)
            result(file)
        }
    }

    fun renameProject(newProjectName: String,  result: (RenameProjectResponse) -> Unit) {
        viewModelScope.launch {
            repository.renameProject(selectedProject!!.id, newProjectName){
               result(it)
                when(it){
                    is Response.Success -> {
                        viewModelScope.launch {
                            localDatabase.myFileDao().renameProject(selectedProject!!.id, newProjectName)
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    fun deleteProject( result: (DeleteProjectResponse) -> Unit) {
        viewModelScope.launch {
            repository.deleteProject(selectedProject!!.id){
                result(it)
                when(it){
                    is Response.Success -> {
                        viewModelScope.launch {
                            localDatabase.myFileDao().deleteProject(selectedProject!!.id)
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    fun updateProjectPassword(password: String, result: (UpdateProjectLockPasswordResponse) -> Unit) {
        viewModelScope.launch {
            repository.updateProjectLockPassword(password, selectedProject!!.id){
                result(it)
                when(it){
                    is Response.Success -> {
                        viewModelScope.launch {
                            localDatabase.myFileDao().updateProjectLockPassword(selectedProject!!.id, password)
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    /*tag code*/
    fun addTag(tag: Tag,  result: (AddTagResponse) -> Unit) {
        viewModelScope.launch {
            repository.addTag(tag, selectedProject!!.id) {
                result(it)
                when(it){
                    is Response.Loading -> {}
                    is Response.Success -> {
                        viewModelScope.launch {
                            localDatabase.myFileDao().addTag(it.data!!)
                        }
                    }
                    is Response.Failure -> {}
                }
            }
        }
    }

    var tags by mutableStateOf(emptyList<Tag>())

    fun getAllTags(tagIds: ArrayList<String>) {
        viewModelScope.launch {
            val lst = ArrayList<Tag>()

            for (tagId in tagIds) {
                val tag = localDatabase.myFileDao().getTagById(tagId) // Replace with your actual DAO method
                tag?.let {
                    lst.add(it)
                }
            }
            tags = lst
        }
    }

    fun getAllRemoteTag(projectId: String, onSuccess: () -> Unit) {
        repository.getAllTags(projectId, onSuccess = onSuccess)
    }

    fun updateFileTag(file: MyFile,  result: (UpdateTagResponse) -> Unit) {
        viewModelScope.launch {
            repository.updateFileTag(file, selectedProject!!.id){
                result(it)
                when(it){
                    is Response.Loading -> {}
                    is Response.Success -> {
                        viewModelScope.launch {
                            localDatabase.myFileDao().updateFileTag(file.tags,file.id)
                        }
                    }
                    is Response.Failure -> {}
                }
            }
        }
    }

        fun renameFile(newName: String, fileId: String, result: (RenameFileResponse) -> Unit) {
        viewModelScope.launch {
            repository.renameFile(newName, fileId, selectedProject!!.id){
                result(it)
                when(it){
                    is Response.Loading -> {}
                    is Response.Success -> {
                        viewModelScope.launch {
                            localDatabase.myFileDao().renameFile(newName,fileId)
                        }
                    }
                    is Response.Failure -> {}
                }
            }
        }
    }

    fun deleteFile(fileId: String, result: (DeleteFileResponse) -> Unit) {
        viewModelScope.launch {
            repository.deleteFile(selectedProject!!.id, fileId){
                result(it)
                when(it){
                    is Response.Success -> {
                        viewModelScope.launch {
                            localDatabase.myFileDao().deleteFile(fileId)
                        }
                    }
                    else -> {}
                }
            }
        }
    }



    /*  audio player code  */

    var playerState by mutableStateOf(PlayerStates.STATE_IDLE)

    var selectedAudioFile: MyFile? by mutableStateOf(null)
        private set

     var isPlaying: Boolean by mutableStateOf(false)

    private var playbackStateJob: Job? = null

    private val _playbackState = MutableStateFlow(PlaybackState(0L, 0L))
    val playbackState: StateFlow<PlaybackState> get() = _playbackState

    fun playPauseAudio() {
        if (player!!.isPlaying) {
            player!!.playWhenReady = false
            isPlaying = false
        } else {
            player!!.playWhenReady = true
            isPlaying = true
        }
    }

    fun playNextAudio(file: MyFile, context: Context) {
        playAudio(file, selectedProject!!, context)
    }



    fun seekTo(newPosition: Long) {
        player!!.seekTo(newPosition)
    }

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    private fun playAudio(audio: MyFile, project: Project, context: Context) {
        player?.release()
        player = ExoPlayer.Builder(context).build()
        player!!.addListener(this)

        when(selectedProject!!.projectStorageType){
            CreateProject.StorageType.LOCAL.value -> {
                player!!.setMediaItem(MediaItem.fromUri(audio.fileUrl))
            }
            CreateProject.StorageType.CLOUD.value -> {
                if (project.fileEncryptionPassword.isEmpty()){
                    player!!.setMediaItem(MediaItem.fromUri(audio.fileUrl))
                }else{
                    val password = EncryptionAndDecryption.decryptPassword(project.fileEncryptionPassword)
                    val dsf = DefaultDataSourceFactory(context, Util.getUserAgent(context, "ExoPlayerInfo"))
                    val mediaSource = ProgressiveMediaSource.Factory(EncryptedFileDataSourceFactory(dsf.createDataSource(), password)).createMediaSource(MediaItem.fromUri(audio.fileUrl))
                    player!!.addMediaSource(mediaSource)
                }
            }
            CreateProject.StorageType.DRIVE.value -> { }
        }

        player!!.prepare()
        player!!.playWhenReady = true
        isPlaying = true
        updateProgress()
        selectedAudioFile = audio
    }

    private fun updateProgress(){
        playbackStateJob?.cancel()
        playbackStateJob =  viewModelScope.launch {
            do {
                _playbackState.emit(
                    PlaybackState(
                        currentPlaybackPosition = if (player!!.currentPosition > 0) player!!.currentPosition else 0L,
                        currentTrackDuration = if (player!!.duration > 0) player!!.duration else 0L
                    )
                )
                delay(1000) // delay for 1 second
            } while (isPlaying)
        }
    }

    fun releasePlayer(){
        player?.release()
        selectedAudioFile = null
    }

    override fun onCleared() {
        super.onCleared()
        player?.release()
    }

    override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
        if (player!!.playbackState == Player.STATE_READY) {
            isPlaying = playWhenReady
        }
    }


    override fun onPlaybackStateChanged(state: Int) {
        println("fdsfsdfsd")
        when (state) {
            Player.STATE_IDLE -> {
                playerState = PlayerStates.STATE_IDLE
            }

            Player.STATE_BUFFERING -> {
                playerState = PlayerStates.STATE_BUFFERING
            }

            Player.STATE_READY -> {
                playerState = if (player!!.playWhenReady) {
                    PlayerStates.STATE_PLAYING
                } else {
                    PlayerStates.STATE_PAUSE
                }
            }

            Player.STATE_ENDED -> {
                playerState = PlayerStates.STATE_END
            }
        }
    }




}







