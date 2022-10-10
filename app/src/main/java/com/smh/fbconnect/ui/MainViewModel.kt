package com.smh.fbconnect.ui

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.smh.fbconnect.data.local.entity.AppEntity
import com.smh.fbconnect.data.local.model.RemoteConfigs
import com.smh.fbconnect.data.repository.Repository
import com.smh.fbconnect.utils.DataStorage
import com.smh.fbconnect.utils.dispatchers.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
@ExperimentalCoroutinesApi
class MainViewModel @Inject constructor(
    private val repository: Repository
): ViewModel() {

    private val _newCreatedApp = MutableLiveData<AppEntity?>(null)
    private val _appName = MutableStateFlow("")
    private val _appCredentials = MutableStateFlow<Uri>(Uri.parse(""))
    private val _appList = arrayListOf<AppEntity>()

    val newCreatedApp get() = _newCreatedApp.asFlow()
    val appsList get() = _appList


    fun updateAppName(appName: String) {
        _appName.value = appName
    }

    fun updateAppUriCredentials(uri: Uri) {
        _appCredentials.value = uri
    }

    fun saveApp() {
        viewModelScope.launch {
            repository
                .saveApp(
                    appName = _appName.value,
                    appCredentials = _appCredentials.value
                )
                .collect {
                    it?.let { app ->
                        _appList.add(0, app)
                        _newCreatedApp.value = app
                    }
                }
        }
    }

    fun deleteApp(appId: Int): Flow<ArrayList<AppEntity>> {
        return repository
            .deleteApp(
                appId = appId
            ).transform { app ->
                _appList.remove(app)
                emit(_appList)
            }
    }

    fun getApps(page: Int): Flow<MutableList<AppEntity>> {

        return repository.getApps(page = page).transform { appsFromDb ->
            if (page == 1) _appList.removeAll(_appList.toSet())
            _appList.addAll(0, appsFromDb.sortedByDescending { it.id })
            emit(appsFromDb)
        }
    }

    fun getAppConfigs(appId: Int): Flow<RemoteConfigs> {

        return repository.getAppConfigs(appId = appId)
    }
}