package com.smh.fbconnect.ui.start

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.smh.fbconnect.R
import com.smh.fbconnect.databinding.DialogDeleteBinding
import com.smh.fbconnect.ui.MainViewModel
import com.smh.fbconnect.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class DeleteAppDialog: BottomSheetDialogFragment() {

    private val binding by viewBinding(DialogDeleteBinding::bind)
    private val args: DeleteAppDialogArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.dialog_delete, container, false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            confirmButton.setOnClickListener {

                requireActivity().supportFragmentManager.setFragmentResult(
                    Constants.APP_DELETION_KEY,
                    bundleOf(
                        Constants.APP_ID to args.appId,
                        Constants.APP_ADAPTER_POSITION to args.appAdapterPosition
                    )
                )

                findNavController().popBackStack()
            }
            discardButton.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }
}