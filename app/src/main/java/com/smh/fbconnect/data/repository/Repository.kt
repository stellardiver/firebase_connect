package com.smh.fbconnect.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.Template
import com.smh.fbconnect.data.local.db.DatabaseHelper
import com.smh.fbconnect.data.local.entity.AppEntity
import com.smh.fbconnect.utils.dispatchers.DispatcherProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.transform
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

    val apps: Flow<List<AppEntity>?> = flow {
        emit(dbHelper.getApps())
    }.flowOn(dispatcherProvider.io)

    fun initApp(appId: Int) {

        //context.assets.open("configs.json")

        flow<Unit> {

            dbHelper.getAppById(appId = appId)?.let { app ->

                val inputStream = context.contentResolver.openInputStream(app.credentialsPath)

                val options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(inputStream))
                    .build()
                FirebaseApp.initializeApp(options)

                val template: Template = FirebaseRemoteConfig.getInstance().templateAsync.get()

                Log.d("DebugTest", template.toJSON())
            }

        }.flowOn(dispatcherProvider.io)



    }
}