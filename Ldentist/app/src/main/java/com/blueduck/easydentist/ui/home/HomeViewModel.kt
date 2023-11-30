package com.blueduck.easydentist.ui.home

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blueduck.easydentist.model.AppUser
import com.blueduck.easydentist.model.Post
import com.blueduck.easydentist.network.CreatePostResponse
import com.blueduck.easydentist.network.GetAllPostResponse
import com.blueduck.easydentist.network.NetworkRepository
import com.blueduck.easydentist.util.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: NetworkRepository) : ViewModel() {

    // using this variable to save the state of tab
    var beforeAfterTabState = true

    // using this variable to store the selected images uri
    val imageUris = arrayListOf<Uri>()

    var getAllPostResponse = MutableLiveData<GetAllPostResponse>()
    var createPostResponse = MutableLiveData<CreatePostResponse>()


    fun getAllPost(userId: String, date: String = "", appUser: AppUser) = viewModelScope.launch {
        repository.getAllPost(userId, date, appUser) {
            getAllPostResponse.value = it
        }
    }

    fun createPost(post: Post)  = viewModelScope.launch {
        repository.createPost(post) {
            createPostResponse.value = it
        }
    }

}