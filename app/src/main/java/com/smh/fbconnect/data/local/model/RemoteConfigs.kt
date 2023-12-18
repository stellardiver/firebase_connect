package com.smh.fbconnect.data.local.model

data class RemoteConfigs(
    var id: Int = 0,
    var appName: String? = null,
    var path: String? = null,
    var geos: ArrayList<String> = arrayListOf()
)