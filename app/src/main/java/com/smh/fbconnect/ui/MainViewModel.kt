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
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
@ExperimentalCoroutinesApi
class MainViewModel @Inject constructor(
    private val repository: Repository,
    private val dispatcher: DispatcherProvider,
    private val dataStorage: DataStorage,
    ): ViewModel() {

    private val _countryCodeList get() = dataStorage.countryCodes
    private val _appsStateFlow = MutableStateFlow<ArrayList<AppEntity>?>(null)
    private val _newCreatedApp = MutableLiveData<AppEntity?>(null)
    private val _appName = MutableStateFlow("")
    private val _appCredentials = MutableStateFlow<Uri>(Uri.parse(""))
    private val _appList = arrayListOf<AppEntity>()

    val newCreatedApp get() = _newCreatedApp.asFlow()
    val appsStateFlow get() = _appsStateFlow.asStateFlow()
    val appsList get() = _appList
    val countryNameList: ArrayList<String> get() {

        val countryCodeList = dataStorage.countryCodes
        val countryNameList = arrayListOf<String>()

        for (i in countryCodeList.indices) {
            countryNameList.add(
                Locale(String(), countryCodeList[i]).displayCountry
            )
        }

        return countryNameList
    }

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

    fun getApps(page: Int): Flow<MutableList<AppEntity>> {

        return repository.getApps(page = page).transform { appsFromDb ->
            if (page == 1) _appList.removeAll(_appList.toSet())
            _appList.addAll(0, appsFromDb.sortedByDescending { it.id })
            emit(appsFromDb)
        }
    }

    fun initApp(appId: Int): Flow<RemoteConfigs> {

        return repository.initApp(appId = appId)
    }
}