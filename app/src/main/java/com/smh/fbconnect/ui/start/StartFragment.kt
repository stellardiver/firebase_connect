package com.smh.fbconnect.ui.start

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
import com.smh.fbconnect.databinding.FragmentStartBinding
import com.smh.fbconnect.ui.MainViewModel
import com.smh.fbconnect.ui.start.adapters.AppsAdapter
import com.smh.fbconnect.utils.EndlessRecyclerViewScrollListener
import com.smh.fbconnect.utils.extensions.setBottomDivider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class StartFragment: Fragment(R.layout.fragment_start) {

    private var scrollListener: EndlessRecyclerViewScrollListener? = null
    private var appsAdapter: AppsAdapter? = null
    private val action = StartFragmentDirections
    private val viewModel: MainViewModel by activityViewModels()
    private val binding by viewBinding(
        vbFactory = FragmentStartBinding::bind,
        onViewDestroyed = { binding: FragmentStartBinding ->
            binding.apply {
                searchResultRecyclerView.adapter = null
            }
        }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
        getApps()

        binding.apply {

            addAppButton.setOnClickListener {
                findNavController().navigate(
                    action.toCreateDialog()
                )
            }
        }
    }

    private fun initAdapter() {
        appsAdapter = AppsAdapter(
            onItemClick = { appId ->
                findNavController().navigate(
                    action.toEditFragment(
                        appId = appId
                    )
                )
            }
        ).apply {
            this.setHasStableIds(true)
        }
        binding.searchResultRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = appsAdapter

            setBottomDivider(
                context = requireContext(),
                drawableId = R.drawable.bg_stroke_didivder
            )
            itemAnimator = null

            scrollListener = object: EndlessRecyclerViewScrollListener(
                this.layoutManager as LinearLayoutManager,
                LoadOnScrollDirection.TOP
            ) {

                override fun onLoadMore(page: Int, totalItemsCount: Int) {
                    binding.progressBar.visibility = View.VISIBLE
                    loadMoreApps(page)
                }
            }

        }
    }

    private fun getApps() {

        viewModel
            .getApps(page = 1)
            .onEach { appList ->

                takeIf { appList.isNotEmpty() }?.also {
                    appsAdapter?.updateAppList(
                        viewModel.appsList,
                        appList.size
                    )
                }?: run {
                    binding.noAppsAvailableTextView.visibility = View.VISIBLE
                }
            }
            .take(1)
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel
            .newCreatedApp
            .onEach {
                it?.let {

                    binding.noAppsAvailableTextView.visibility = View.GONE

                    appsAdapter?.updateAppList(
                        apps = viewModel.appsList,
                        1
                    )
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun loadMoreApps(page: Int) {

        viewModel
            .getApps(page = page)
            .onEach { appList ->
                if (appList.isEmpty()) scrollListener?.let { it.currentPage-- }

                binding.progressBar.visibility = View.INVISIBLE

                appsAdapter?.updateAppList(
                    viewModel.appsList,
                    appList.size
                )
            }
            .take(1)
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }
}