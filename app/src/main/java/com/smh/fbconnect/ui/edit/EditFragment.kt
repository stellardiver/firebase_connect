package com.smh.fbconnect.ui.edit

import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.URLUtil
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.smh.fbconnect.R
import com.smh.fbconnect.databinding.FragmentEditBinding
import com.smh.fbconnect.ui.MainViewModel
import com.smh.fbconnect.ui.edit.adapters.CountryAdapter
import com.smh.fbconnect.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class EditFragment: Fragment(R.layout.fragment_edit) {

    private var countryAdapter: CountryAdapter? = null
    private val args: EditFragmentArgs by navArgs()
    private val viewModel: EditViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private val binding by viewBinding(
        vbFactory = FragmentEditBinding::bind,
        onViewDestroyed = { binding: FragmentEditBinding ->
            binding.apply {
                countriesRecyclerView.adapter = null
            }
        }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
        getConfigs()

        binding.apply {

            updateAppButton.setOnClickListener {

                val appLink = appLinkEditText.text.toString()

                if (URLUtil.isValidUrl(appLink)) {
                    updatingLayout.visibility = View.VISIBLE
                    updateConfigs(appLink = appLink)
                } else {
                    viewLifecycleOwner.lifecycleScope.launch {
                        appLinkTextInputLayout.error = "Некорректная ссылка"
                        delay(2000)
                        appLinkTextInputLayout.error = null
                    }
                }
            }

            deselectAllGeosButton.setOnClickListener {

                deselectAllGeosButton.visibility = View.GONE

                countryAdapter?.updateAllItems(
                    countryList = viewModel.deselectAllChecks()
                )
            }
        }
    }

    private fun initAdapter() {

        countryAdapter = CountryAdapter(
            onCheckedChanged = { position, isChecked ->

                if (isChecked && !binding.deselectAllGeosButton.isVisible)
                    binding.deselectAllGeosButton.visibility = View.VISIBLE

                viewModel.updateCheckStatus(
                    index = position,
                    checkStatus = isChecked
                )
            }
        ).apply { setHasStableIds(true) }

        binding.countriesRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = countryAdapter
        }

        viewModel
            .countriesVisibleList
            .onEach {
                it?.let { countriesList ->
                    binding.selectedCountriesTextView.text = countriesList
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun getConfigs() {

        mainViewModel
            .getAppConfigs(appId = args.appId)
            .onEach { remoteConfigs ->

                binding.apply {

                    with(remoteConfigs) {

                        progressLayout.visibility = View.GONE
                        innerLayout.visibility = View.VISIBLE

                        app_name?.let { name ->
                            appNameTitleTextView.text = name
                        }
                        path?.let { path ->
                            appLinkEditText.setText(path, TextView.BufferType.EDITABLE)
                        }

                        if (geos.isNotEmpty()) deselectAllGeosButton.visibility = View.VISIBLE

                        countryAdapter?.updateAdapter(
                            countryList = viewModel.updateCountryList(geosStringList = geos)
                        )
                    }
                }
            }
            .take(1)
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun updateConfigs(appLink: String) {
        viewModel
            .updateAppConfigs(
                appLink = appLink,
                appId = args.appId
            ).onEach { resource ->

                resource.message?.let { message ->

                    Toast.makeText(
                        context,
                        message,
                        Toast.LENGTH_LONG
                    ).show()
                }

                findNavController().popBackStack()
            }
            .launchIn(
                viewLifecycleOwner.lifecycleScope
            )
    }
}