package com.smh.fbconnect.ui.edit

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import com.smh.fbconnect.data.local.model.Country
import com.smh.fbconnect.data.local.model.RemoteConfigs
import com.smh.fbconnect.data.repository.Repository
import com.smh.fbconnect.utils.DataStorage
import com.smh.fbconnect.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
@ExperimentalCoroutinesApi
class EditViewModel @Inject constructor(
    private val repository: Repository,
    private val dataStorage: DataStorage
): ViewModel() {

    fun updateCountryList(geosStringList: ArrayList<String>): ArrayList<Country> {

        val countryCodeList = dataStorage.countryCodes

        geosStringList.forEach { geo ->
            val pos = countryCodeList.indexOfFirst { it == geo }
            if (pos > -1) {
                _countryList[pos].is_checked = true
                _selectedCountriesList.add(_countryList[pos].name)
            }
        }

        _countriesVisibleList.value =
            dataStorage.generateOpenedGeosText(countriesList = _selectedCountriesList)

        return _countryList
    }

    fun updateCheckStatus(index: Int, checkStatus: Boolean) {
        _countryList[index].is_checked = checkStatus

        if (_countryList[index].is_checked) {
            _selectedCountriesList.add(_countryList[index].name)
        } else {
            _selectedCountriesList.remove(_countryList[index].name)
        }

        _countriesVisibleList.value =
            dataStorage.generateOpenedGeosText(countriesList = _selectedCountriesList)
    }

    fun deselectAllChecks(): ArrayList<Country> {
        _selectedCountriesList.clear()
        _countriesVisibleList.value =
            dataStorage.generateOpenedGeosText(countriesList = _selectedCountriesList)

        _countryList.forEach { country ->
            country.is_checked = false
        }

        return _countryList
    }

    fun updateAppConfigs(appLink: String, appId: Int): Flow<Resource<Unit>> {

        val countryCodesList = arrayListOf<String>()

        for (country in _selectedCountriesList) {
            _countryList.find { it.name == country }?.apply {
                countryCodesList.add(code)
            }
        }

        return repository.updateAppConfigs(
            configs = RemoteConfigs(
                id = appId,
                path = appLink,
                geos = countryCodesList
            )
        )
    }

    private val _countryList = arrayListOf<Country>()
    private val _selectedCountriesList = arrayListOf<String>()
    private val _countriesVisibleList = MutableLiveData<String?>(null)

    val countriesVisibleList get() = _countriesVisibleList.asFlow()

    init {

        val countryCodeList = dataStorage.countryCodes
        val countryList = arrayListOf<Country>()

        for (i in countryCodeList.indices) {
            countryList.add(
                Country(
                    name = Locale(String(), countryCodeList[i]).displayCountry,
                    code = countryCodeList[i]
                )
            )
        }

        _countryList.addAll(countryList)
    }
}