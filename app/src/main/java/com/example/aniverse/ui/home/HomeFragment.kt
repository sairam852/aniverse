package com.example.aniverse.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aniverse.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Home screen displaying the top anime list with pull-to-refresh
 * and paging support.
 */
@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    private lateinit var adapter: AnimePagingAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSwipeRefresh()
        observePagingData()
        observeLoadStates()
        setupRetry()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerView() {
        adapter = AnimePagingAdapter { anime ->
            findNavController().navigate(
                com.example.aniverse.R.id.action_homeFragment_to_animeDetailFragment,
                Bundle().apply { putInt("animeId", anime.id) }
            )
        }
        binding.recyclerAnime.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@HomeFragment.adapter.withLoadStateFooter(
                footer = AnimeLoadStateAdapter { this@HomeFragment.adapter.retry() }
            )
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            // Only refresh the adapter (RemoteMediator handles network fetch)
            // viewModel.refresh() clears DB which is bad for offline support
            adapter.refresh()
        }
    }

    private fun observePagingData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.animePagingFlow.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }
    }

    private fun observeLoadStates() {
        viewLifecycleOwner.lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest { loadState ->
                val isRefreshing = loadState.refresh is LoadState.Loading
                
                // Only show pull-to-refresh spinner if refreshing AND not empty (if empty, we use center progress)
                binding.swipeRefresh.isRefreshing = isRefreshing && adapter.itemCount > 0

                val isError = loadState.refresh is LoadState.Error
                val isEmpty = loadState.refresh is LoadState.NotLoading &&
                        loadState.append.endOfPaginationReached &&
                        adapter.itemCount == 0
                
                // Prioritize showing the list if we have any data (cached or new)
                val hasData = adapter.itemCount > 0
                binding.recyclerAnime.isVisible = hasData
                
                // Show loading spinner in center ONLY if we have NO data and are refreshing
                binding.progressLoading.isVisible = isRefreshing && !hasData
                
                // Empty view only if we are truly empty and not loading/error
                binding.textEmpty.isVisible = isEmpty
                
                // Full screen error ONLY if we have NO data and there is an error
                binding.errorContainer.isVisible = isError && !hasData

                if (isError) {
                    val errorState = loadState.refresh as LoadState.Error
                    val errorMessage = errorState.error.localizedMessage 
                        ?: getString(com.example.aniverse.R.string.error_loading)
                    
                    if (hasData) {
                        // We have data, but refresh failed -> Show Snackbar
                        com.google.android.material.snackbar.Snackbar.make(
                            binding.root,
                            "Offline: Showing cached data.",
                            com.google.android.material.snackbar.Snackbar.LENGTH_LONG
                        ).setAction("Retry") { adapter.retry() }.show()
                    } else {
                        // Full screen error text
                        binding.textError.text = errorMessage
                    }
                }
            }
        }
    }

    private fun setupRetry() {
        binding.buttonRetry.setOnClickListener {
            adapter.retry()
        }
    }
}
