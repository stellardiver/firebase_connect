package com.smh.fbconnect.ui.search

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.smh.fbconnect.R
import com.smh.fbconnect.databinding.FragmentSearchBinding
import com.smh.fbconnect.ui.MainViewModel
import com.smh.fbconnect.ui.search.adapters.AppsAdapter
import com.smh.fbconnect.utils.FileManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class SearchFragment: Fragment(R.layout.fragment_search) {

    private var appsAdapter: AppsAdapter? = null
    private val action = SearchFragmentDirections
    private val viewModel: MainViewModel by activityViewModels()
    private val binding by viewBinding(
        vbFactory = FragmentSearchBinding::bind,
        onViewDestroyed = { binding: FragmentSearchBinding ->
            binding.apply {
                searchResultRecyclerView.adapter = null
            }
        }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        initAdapter()

        binding.apply {
            addAppButton.setOnClickListener {

                findNavController().navigate(
                    action.toCreateDialog()
                )


            }
        }

    }

    private fun initAdapter() {
        appsAdapter = AppsAdapter()
        binding.searchResultRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = appsAdapter
        }
    }
}