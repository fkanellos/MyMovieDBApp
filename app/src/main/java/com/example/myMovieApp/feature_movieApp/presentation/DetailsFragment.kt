package com.example.myMovieApp.feature_movieApp.presentation

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.myMovieApp.R
import com.example.myMovieApp.common.Constants
import com.example.myMovieApp.databinding.DetailsFragmentBinding
import com.example.myMovieApp.feature_movieApp.data.api.repository.dao.MovieResultModel
import com.example.myMovieApp.feature_movieApp.domain.model.MovieAppViewModel
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.android.synthetic.main.details_fragment.*
import kotlinx.android.synthetic.main.details_fragment.view.*
import kotlinx.android.synthetic.main.video_view.*

class DetailsFragment : Fragment(R.layout.details_fragment) {

    private lateinit var youtubePlayerView: YouTubePlayerView

    private lateinit var viewModel: MovieAppViewModel
    private val args by navArgs<DetailsFragmentArgs>()
    var videoID: String=""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel
        val model = args.model
        youtubePlayerView = youTubePlayer

        val binding = DetailsFragmentBinding.bind(view)

        viewModel.movieSeriesGenre.observe(viewLifecycleOwner, Observer {
            model.genre = it.data?.genres?.get(0)?.name
            model.videos = it.data?.videos?.results?.get(0)?.key
            genre.text = model.genre
        })

        binding.apply {

            Glide.with(this@DetailsFragment)
                .load(Constants.BASE_URL_PHOTOS.plus(model.backdrop_path))
                .error(R.drawable.ic_error)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        progressBar.isVisible = false
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        progressBar.isVisible = false
                        title.isVisible = true
                        rating.isVisible = true
                        text.isVisible = true
                        genre.isVisible = true
                        return false
                    }
                })
                .into(imageView)
            title.text = if (model.title.isNullOrEmpty()) model.name else model.title
            rating.text = model.vote_average.toString()
            text.text = model.overview


            // on below line we are adding listener
            // for our youtube player view.
            youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {

                override fun onReady(youTubePlayer: YouTubePlayer) {
                    if (!model.videos.isNullOrEmpty()) {
                        videoID = model.videos!!
                    }
                    // loading the selected video
                    // into the YouTube Player
                    videoID.let { youTubePlayer.loadVideo(it, 0f) }
                }

                override fun onStateChange(
                    youTubePlayer: YouTubePlayer,
                    state: PlayerConstants.PlayerState
                ) {
                    // this method is called if video has ended,
                    super.onStateChange(youTubePlayer, state)
                }
            })
        }
        // Initialize button text
        model.id?.let {
            val exists = viewModel.isMovieInDB(it)
            adjustFavButtonText(exists)
        }
        favBtn.setOnClickListener {
            model.id?.let {
                val exists = viewModel.isMovieInDB(it)
                handleButtonAction(exists, model)
            }
        }
    }

    /* handle favourite button depending on whether it is clicked or not */
    private fun adjustFavButtonText(itemExistsInDb: Boolean) {
        if (itemExistsInDb) {
            view?.favBtn?.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_fav_clicked)
        } else {
            view?.favBtn?.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_fav)
        }
    }

    private fun handleButtonAction(itemExistsInDb: Boolean, model: MovieResultModel) {
        if (itemExistsInDb) {
            viewModel.deleteSavedMovieSeries(model)
        } else {
            viewModel.saveMovieSeries(model)
        }
        adjustFavButtonText(!itemExistsInDb)
    }
}