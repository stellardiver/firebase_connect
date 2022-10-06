package com.smh.fbconnect.ui.create

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.smh.fbconnect.R
import com.smh.fbconnect.databinding.DialogCreateBinding
import com.smh.fbconnect.ui.MainViewModel
import com.smh.fbconnect.utils.FileManager
import com.smh.fbconnect.utils.extensions.getFileName
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class CreateAppDialog: BottomSheetDialogFragment() {

    private var fileManager: FileManager? = null
    private val binding by viewBinding(DialogCreateBinding::bind)
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.dialog_create, container, false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fileManager = FileManager(fragment = this)

        binding.apply {

            chooseFileButton.setOnClickListener {
                fileManager
                    ?.permissionForFiles
                    ?.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }

            fileManager
                ?.storagePermission
                ?.onEach {
                    Log.d("DebugTest", "storagestateflow value: $it")
                }
                ?.launchIn(viewLifecycleOwner.lifecycleScope)

            fileManager
                ?.uriFileFromCache
                ?.onEach {
                    it?.let { uriFromCache ->
                        val fileName = uriFromCache.getFileName(requireContext())
                        chosenFileTextView.text = fileName
                    }
                }
                ?.launchIn(viewLifecycleOwner.lifecycleScope)
        }
    }
}