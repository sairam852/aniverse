package com.example.aniverse.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.aniverse.R
import com.example.aniverse.databinding.FragmentAnimeDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Detail screen displaying full anime information including
 * poster, trailer (WebView), synopsis, genres, and metadata.
 */
@AndroidEntryPoint
class AnimeDetailFragment : Fragment() {

    private var _binding: FragmentAnimeDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AnimeDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnimeDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupWebView()
        observeUiState()
    }

    override fun onDestroyView() {
        binding.webViewTrailer.destroy()
        _binding = null
        super.onDestroyView()
    }

    private fun setupWebView() {
        binding.webViewTrailer.apply {
            settings.javaScriptEnabled = true
            webViewClient = WebViewClient()
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is com.example.aniverse.util.UiState.Loading -> showLoading()
                    is com.example.aniverse.util.UiState.Success -> showContent(state.data)
                    is com.example.aniverse.util.UiState.Error -> showError(state.message)
                    is com.example.aniverse.util.UiState.Empty -> { /* Handle empty if needed */ }
                }
            }
        }
    }

    private fun showLoading() {
        binding.progressLoading.isVisible = true
        binding.errorContainer.isVisible = false
        binding.contentContainer.isVisible = false
    }

    private fun showError(message: String) {
        binding.progressLoading.isVisible = false
        binding.errorContainer.isVisible = true
        binding.contentContainer.isVisible = false
        binding.textError.text = message
        binding.buttonRetry.setOnClickListener { viewModel.retry() }
    }

    private fun isOnline(): Boolean {
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun showContent(anime: com.example.aniverse.domain.model.AnimeDetails) {
        binding.progressLoading.isVisible = false
        binding.errorContainer.isVisible = false
        binding.contentContainer.isVisible = true

        binding.textTitle.text = anime.displayTitle
        binding.textRating.text = getString(R.string.rating_format, anime.formattedScore)
        binding.textEpisodes.text = anime.formattedEpisodes
        binding.textType.text = anime.type ?: "—"
        binding.textGenres.text = anime.formattedGenres
        binding.textAired.text = anime.formattedAiringPeriod
        binding.textSynopsis.text = anime.synopsis ?: getString(R.string.no_synopsis)

        // Only show trailer if URL exists AND we are online
        if (anime.trailerUrl != null && isOnline()) {
            binding.webViewTrailer.isVisible = true
            binding.imagePoster.isVisible = false
            binding.webViewTrailer.loadUrl(anime.trailerUrl)
        } else {
            // Fallback to poster imagem (offline or no trailer)
            binding.webViewTrailer.isVisible = false
            binding.imagePoster.isVisible = true
            
            if (com.example.aniverse.config.AppConfig.showImages) {
                Glide.with(binding.imagePoster)
                    .load(anime.largeImageUrl ?: anime.imageUrl)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .centerCrop()
                    .into(binding.imagePoster)
            } else {
                Glide.with(binding.imagePoster).clear(binding.imagePoster)
                binding.imagePoster.setImageResource(R.drawable.ic_launcher_foreground)
            }
        }
    }
}
