package com.lock.blueduck.applock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lock.blueduck.applock.model.AppInfo
import com.lock.blueduck.applock.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AppListViewModel @Inject constructor(private val repository: Repository): ViewModel() {

    fun updateAppInfo(app: AppInfo) = viewModelScope.launch {
        repository.updateAppInfo(app)
    }

    fun getAllApps() = repository.getAppList()
}