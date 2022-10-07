package com.smh.fbconnect.utils

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.smh.fbconnect.data.local.model.Configs
import com.smh.fbconnect.utils.extensions.appCacheDir
import com.smh.fbconnect.utils.extensions.getFileExt
import com.smh.fbconnect.utils.extensions.getFileName
import java.io.*
import java.nio.channels.FileChannel
import javax.inject.Inject


class FileManager @Inject constructor(
    private val fragment: Fragment
) {

    private fun manageFileFromActivityResult(uri: Uri) {

        val fileExt = uri.getFileExt(fragment.requireContext())

        if (fileExt == "json") {

            val fileFromCache = validateJsonAndWriteFile(uri = uri)

            fileFromCache?.let { file ->
                val finalUri = Uri.fromFile(file)
                _uriFileFromCache.value = finalUri
            }?: Toast.makeText(
                fragment.requireContext(),
                "Некорректный json-файл",
                Toast.LENGTH_LONG
            ).show()

        } else Toast.makeText(
            fragment.requireContext(),
            "Необходимо выбрать файл с расширением .json",
            Toast.LENGTH_LONG
        ).show()

    }

    private fun validateJsonAndWriteFile(uri: Uri): File? {

        val inputStream = fragment.requireContext().contentResolver.openInputStream(uri)
        val jsonString = inputStream?.bufferedReader().use { it?.readText() }

        inputStream?.close()
        jsonString?.let { string ->
            val typeToken = object : TypeToken<Configs>() {}.type
            val json: Configs? = Gson().fromJson(string, typeToken)

            json?.let { configs ->

                with(configs) {

                    if (
                        type.isNullOrBlank()
                        || project_id.isNullOrBlank()
                        || private_key_id.isNullOrBlank()
                        || private_key.isNullOrBlank()
                        || client_email.isNullOrBlank()
                        || client_id.isNullOrBlank()
                        || auth_uri.isNullOrBlank()
                        || token_uri.isNullOrBlank()
                        || auth_provider_x509_cert_url.isNullOrBlank()
                        || client_x509_cert_url.isNullOrBlank()
                    ) {
                        return null
                    }
                    else {
                        val fileFromCache = File(
                            fragment.requireContext().appCacheDir,
                            uri.getFileName(fragment.requireContext())
                        )

                        runCatching {
                            PrintWriter(
                                FileWriter(fileFromCache.path)
                            ).use {
                                it.write(jsonString)
                            }

                        }.onFailure {
                            return null
                        }

                        return fileFromCache
                    }
                }

            }?: return null
        }?: return null
    }

    private fun copy(src: File?, dst: File?) {
        val inStream = FileInputStream(src)
        val outStream = FileOutputStream(dst)
        val inChannel: FileChannel = inStream.channel
        val outChannel: FileChannel = outStream.channel
        inChannel.transferTo(0, inChannel.size(), outChannel)
        inStream.close()
        outStream.close()
    }

    private val externalStorageRequestForDocs = fragment.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) {


        it.data?.let { intent ->

            val selectedFileUri = intent.data
            val selectedFilesClipData = intent.clipData

            selectedFileUri?.let { uri ->
                manageFileFromActivityResult(uri = uri)
            }

            selectedFilesClipData?.let { clipData ->

                for (index in 0 until clipData.itemCount) {

                    val uri = clipData.getItemAt(index).uri

                    manageFileFromActivityResult(uri = uri)
                }
            }
        }

    }

  private val _storagePermission = MutableLiveData(false)
  private val _uriFileFromCache = MutableLiveData<Uri?>(null)

  val permissionForFiles = fragment.registerForActivityResult(
      ActivityResultContracts.RequestPermission()) { isGranted ->
      when {
          isGranted -> {
              externalStorageRequestForDocs.launch(
                  Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                      putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
                      type = Constants.INTENT_TYPE_FILE
                  }
              )

              _storagePermission.value = true
          }
          else -> {
              _storagePermission.value = false
              // TODO: user declined permission request
          }
      }
  }

  val storagePermission get() = _storagePermission.asFlow()
  val uriFileFromCache get() = _uriFileFromCache.asFlow()
}