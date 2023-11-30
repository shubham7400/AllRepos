package com.example.paginglibrarytest.screens.home

import androidx.lifecycle.ViewModel
import com.example.paginglibrarytest.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(repository: Repository): ViewModel() {
    val getAllImages = repository.getAllImages()
}