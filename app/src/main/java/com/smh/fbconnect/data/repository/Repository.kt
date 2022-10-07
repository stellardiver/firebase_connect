package com.smh.fbconnect.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.remoteconfig.*
import com.google.firebase.remoteconfig.internal.TemplateResponse.ConditionResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.smh.fbconnect.data.local.db.DatabaseHelper
import com.smh.fbconnect.data.local.entity.AppEntity
import com.smh.fbconnect.data.local.model.Configs
import com.smh.fbconnect.data.local.model.RemoteConfigs
import com.smh.fbconnect.utils.dispatchers.DispatcherProvider
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

    private suspend fun forUse() {

        coroutineScope {
            launch(dispatcherProvider.io) {
                //            val httpTransport: HttpTransport = GoogleNetHttpTransport.newTrustedTransport()
//            val credential = GoogleCredentials
//                .fromStream(assets.open("configs.json"))
//                .createScoped(AnalyticsScopes.all())
//
//            val analytics = Analytics.Builder(httpTransport, GsonFactory.getDefaultInstance(), HttpCredentialsAdapter(credential))
//                .setApplicationName("Way Of Empire").build()
//
//            val body = CustomDimension()
//            body.name = "test_dimension"
//            body.scope = "USER"
//            body.active = true
//
//            try {
//                val accounts: Accounts = analytics.management().accounts().list().execute()
//                val firstAccountId: String = accounts.items[0].id
//                val properties: Webproperties = analytics.management().webproperties()
//                    .list(firstAccountId).execute()
//                val firstWebPropertyId = properties.items[0].id
//                analytics.management().customDimensions()
//                    .insert(firstAccountId, firstWebPropertyId, body).execute()
//            } catch (e: GoogleJsonResponseException) {
//                System.err.println(
//                    "There was a service error: "
//                            + e.details.code + " : "
//                            + e.details.message
//                )
//            }

//            template.parameters["settings"] = Parameter()
//                .setDefaultValue(ParameterValue.of("{\"approve\":false}\"}"))
//                .setValueType(ParameterValueType.JSON)
//                .setDescription("Test via Firebase Admin")
//
//            try {
//                val publishedTemplate = FirebaseRemoteConfig.getInstance()
//                    .publishTemplateAsync(template).get()
//                Log.d("DebugTest", "Template has been published")
//                // See the ETag of the published template.
//            } catch (e: ExecutionException) {
//                if (e.cause is FirebaseRemoteConfigException) {
//                    Log.d("DebugTest", e.cause?.message.toString())
//                }
//            }
            }
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

    fun initApp(appId: Int): Flow<RemoteConfigs> {

        return flow {

            dbHelper.getAppById(appId = appId)?.let { app ->

                val inputStream = context.contentResolver.openInputStream(app.credentialsPath)

                val options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(inputStream))
                    .build()

                val firebaseApps = FirebaseApp.getApps()

                val initializedApp = firebaseApps.find { it.name == app.name } ?: run {
                    FirebaseApp.initializeApp(options, app.name)
                }

                val template: Template = FirebaseRemoteConfig
                    .getInstance(initializedApp)
                    .templateAsync
                    .get()

                val arrayList = arrayOf("IT")

                Log.d("DebugTest", arrayList.joinToString(
                    prefix = "[",
                    postfix = "]",
                    separator = ", ",
                    transform = { "'$it'" }
                ))

                template.conditions = mutableListOf(
                    Condition("non_organic", "app.userProperty['non_organic'].exactlyMatches(['true'])"),
                    Condition("geo", "device.country in ${arrayList.joinToString(
                        prefix = "[",
                        postfix = "]",
                        separator = ", ",
                        transform = { "'$it'" }
                    )}"),
                )

                template.parameters["settings"] = Parameter()
                    .setDefaultValue(ParameterValue.of("{\"approve\":false}"))
                    .setConditionalValues(
                        mapOf(
                            "non_organic" to ParameterValue.of("{\"approve\":true,\"path\":\"https://trackerlab.org/56bdjdnj\",\"params\":[{\"key\":\"type\",\"value\":\"email\"},{\"key\":\"type\",\"value\":\"mail\"},{\"key\":\"name\",\"value\":\"mail\"},{\"key\":\"name\",\"value\":\"login\"}]}"),
                            "geo" to ParameterValue.of("{\"approve\":true,\"path\":\"https://trackerlab.org/56bdjdnj\",\"params\":[{\"key\":\"type\",\"value\":\"email\"},{\"key\":\"type\",\"value\":\"mail\"},{\"key\":\"name\",\"value\":\"mail\"},{\"key\":\"name\",\"value\":\"login\"}]}")
                        )
                    )
                    .setValueType(ParameterValueType.JSON)
                    .setDescription("set by Firebase Connect")

                publishTemplate(
                    template = template,
                    firebaseApp = initializedApp
                ).collect {
                    emit(RemoteConfigs(1))
                }
            }

        }.flowOn(dispatcherProvider.io)

    }

}