package com.example.aniverse.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.aniverse.R
import com.example.aniverse.databinding.FragmentAnimeDetailBinding
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import android.content.Intent
import com.example.aniverse.config.AppConfig
import com.example.aniverse.util.UiState
import com.google.android.material.snackbar.Snackbar
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
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
        setupVideoPlayer()
        observeUiState()
    }

    override fun onDestroyView() {
        binding.youtubePlayerView.release()
        _binding = null
        super.onDestroyView()
    }

    // Hold a reference to the YouTubePlayer instance
    private var youTubePlayer: YouTubePlayer? = null
    private var currentVideoId: String? = null

    private fun setupVideoPlayer() {
        lifecycle.addObserver(binding.youtubePlayerView)

        val options = IFramePlayerOptions.Builder()
            .controls(1)
            .fullscreen(1)
            .rel(0)
            .build()

        try {
            binding.youtubePlayerView.initialize(object : AbstractYouTubePlayerListener() {
                override fun onReady(player: YouTubePlayer) {
                    // Player is ready, store the instance
                    this@AnimeDetailFragment.youTubePlayer = player
                    
                    // If we already have a video ID from our data, load it
                    currentVideoId?.let {
                        player.loadVideo(it, 0f)
                    }
                }
                
                override fun onError(youTubePlayer: YouTubePlayer, error: com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants.PlayerError) {
                    // If video can't be played, show the poster as a fallback
                    binding.youtubePlayerView.isVisible = false
                    binding.imagePoster.isVisible = true
                    Log.e("AnimeDetailFragment", "YouTube Player Error: $error")

                    // Handle embedded playback restriction
                    if (error == com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants.PlayerError.VIDEO_NOT_PLAYABLE_IN_EMBEDDED_PLAYER) {
                       binding.fabPlayTrailer.isVisible = true
                    }
                }
            }, options)

            // Setup FAB listener for external playback
            binding.fabPlayTrailer.setOnClickListener {
                currentVideoId?.let { videoId ->
                    openInYouTube(videoId)
                }
            }

        } catch (e: Exception) {
            Log.e("AnimeDetailFragment", "Error initializing YouTube player", e)
            binding.youtubePlayerView.isVisible = false
            binding.imagePoster.isVisible = true
        }
    }

    private fun openInYouTube(videoId: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=$videoId"))
            startActivity(intent)
        } catch (e: Exception) {
            Snackbar.make(binding.root, "Could not open YouTube", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is UiState.Loading -> showLoading()
                    is UiState.Success -> showContent(state.data)
                    is UiState.Error -> showError(state.message)
                    is UiState.Empty -> { }
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

        // Always load the poster image so it's ready if we fallback
        loadPosterImage(anime)

        // Only show trailer if URL exists AND we are online
        if (anime.trailerUrl != null && isOnline()) {
            val videoId = extractYouTubeVideoId(anime.trailerUrl)
            if (videoId != null) {
                binding.youtubePlayerView.isVisible = true
                binding.imagePoster.isVisible = false
                
                // Store the videoId. The onReady callback will handle loading it
                // if the player wasn't ready yet.
                currentVideoId = videoId
                
                // If the player is already ready, load the video immediately.
                youTubePlayer?.loadVideo(videoId, 0f)

            } else {
                binding.youtubePlayerView.isVisible = false
                binding.imagePoster.isVisible = true
            }
        } else {
            // Fallback to poster (offline or no trailer)
            binding.youtubePlayerView.isVisible = false
            binding.imagePoster.isVisible = true
        }
    }

    private fun loadPosterImage(anime: com.example.aniverse.domain.model.AnimeDetails) {
        if (AppConfig.showImages) {
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

    private fun extractYouTubeVideoId(url: String): String? {
        Log.d("AnimeDetail", "Extracting ID from URL: $url")
        return try {
            val uri = Uri.parse(url)
            val videoId = when {
                uri.host?.contains("youtube.com") == true && uri.path?.startsWith("/embed/") == true -> {
                    uri.lastPathSegment
                }
                uri.host?.contains("youtube.com") == true && uri.path?.contains("watch") == true -> {
                    uri.getQueryParameter("v")
                }
                uri.host?.contains("youtu.be") == true -> {
                    uri.lastPathSegment
                }
                uri.host?.contains("youtube-nocookie.com") == true && uri.path?.startsWith("/embed/") == true -> {
                    uri.lastPathSegment
                }
                else -> null
            }
            Log.d("AnimeDetail", "Extracted Video ID: $videoId")
            videoId
        } catch (e: Exception) {
            Log.e("AnimeDetail", "Error extracting video ID", e)
            null
        }
    }
}
