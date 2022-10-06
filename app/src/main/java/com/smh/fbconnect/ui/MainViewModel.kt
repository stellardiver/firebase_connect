package com.smh.fbconnect.ui

import androidx.lifecycle.ViewModel
import com.smh.fbconnect.data.local.entity.AppEntity
import com.smh.fbconnect.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

@HiltViewModel
@ExperimentalCoroutinesApi
class MainViewModel @Inject constructor(
    private val repository: Repository
): ViewModel() {

    private val _appsStateFlow = MutableStateFlow<List<AppEntity>?>(null)

    val appsStateFlow get() = _appsStateFlow.asStateFlow()
    val apps: Flow<List<AppEntity>?> get() = repository.apps

    init {
        repository.apps.transform {

            it?.let { appList ->
                _appsStateFlow.value = appList
            }

            emit(it)
        }
    }
}