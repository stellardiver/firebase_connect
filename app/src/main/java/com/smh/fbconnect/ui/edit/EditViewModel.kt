package com.smh.fbconnect.ui.edit

import androidx.lifecycle.ViewModel
import com.smh.fbconnect.utils.DataStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class EditViewModel @Inject constructor(
    private val dataStorage: DataStorage
): ViewModel() {

    private val _countryCodeList get() = dataStorage.countryCodes

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
}