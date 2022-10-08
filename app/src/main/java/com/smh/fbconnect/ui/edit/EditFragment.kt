package com.smh.fbconnect.ui.edit

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.smh.fbconnect.R
import com.smh.fbconnect.databinding.FragmentEditBinding
import com.smh.fbconnect.ui.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class EditFragment: Fragment(R.layout.fragment_edit) {

    private val args: EditFragmentArgs by navArgs()
    private val viewModel: EditViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private val binding by viewBinding(
        vbFactory = FragmentEditBinding::bind,
        onViewDestroyed = { binding: FragmentEditBinding ->
            binding.apply {

            }
        }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initApp()

        with(binding) {

            multiSelectSpinner
                .buildCheckedSpinner(
                    viewModel.countryNameList
                ) { selectedPositionList, displayString ->
                Log.d("DebugTest", "Selected position:  $selectedPositionList, Display String:  $displayString")
            }
        }
    }

    private fun initApp() {

        mainViewModel
            .getAppConfigs(appId = args.appId)
            .onEach { remoteConfigs ->

                binding.apply {
                    with(remoteConfigs) {
                        app_name?.let { name ->
                            appNameTitleTextView.text = name
                        }
                        path?.let { path ->
                            appLinkEditText.setText(path, TextView.BufferType.EDITABLE)
                        }
                        geos?.let { geos ->

                        }
                    }
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }
}