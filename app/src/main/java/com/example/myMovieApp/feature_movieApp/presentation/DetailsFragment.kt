package com.example.myMovieApp.feature_movieApp.presentation

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.MediaController
import android.widget.VideoView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.myMovieApp.R
import com.example.myMovieApp.common.Constants
import com.example.myMovieApp.databinding.DetailsFragmentBinding
import com.example.myMovieApp.feature_movieApp.domain.model.MovieAppViewModel

class DetailsFragment : Fragment(R.layout.details_fragment) {
    private val viewModel by viewModels<MovieAppViewModel>()

    private val args by navArgs<DetailsFragmentArgs>()
    private var videoView: VideoView? = null
    private var mediaController: MediaController? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        val videoAdapter = VideoAdapter()

//        if (mediaController == null) {
//            mediaController = MediaController(requireContext())
//            mediaController!!.setAnchorView(this.videoView)
//        }
//        videoView!!.setMediaController(mediaController)

        val binding = DetailsFragmentBinding.bind(view)

        binding.apply {
            val model = args.model
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
                        return false
                    }
                })
                .into(imageView)
            title.text = if (model.title.isNullOrEmpty()) model.name else model.title
            rating.text = model.vote_average.toString()
            text.text = model.overview

//            if (!model.videos?.results?.get(0)?.key.isNullOrEmpty()){
//                videoID = model.videos!!.results!![0].key
//
//
//            }

//            favBtn.setOnClickListener {
//                model.id?.let{
//                    val exists: Boolean = viewModel.isMovieInDB(id).observe(viewLifecycleOwner, Observer { exist ->
//                            if (exists){
//                                viewModel.deleteSavedMovieSeries(model)
//                                handleFavBtn(model, exists)
//                            } else {
//                                viewModel.saveMovieSeries(model)
//                                handleFavBtn(model, exists)
//                            }
//                        })}
//
//                }

//            viewModel.searchMovies(title.text).observe(viewLifecycleOwner, Observer {
//                model.id?.let {
//                    addFavBtn = viewModel.hasDbItems()
//                    if (addFavBtn) {
//                        viewModel.deleteSavedMovieSeries(model)
//                        handleFavBtn(model, addFavBtn)
//                    } else {
//                        viewModel.saveMovieSeries(model)
//                        handleFavBtn(model, addFavBtn)
//
//                    }
//
//                }
//            })
            }
//        // on below line we are adding listener
//        // for our youtube player view.
//        youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
//
//            override fun onReady(youTubePlayer: YouTubePlayer) {
//                // loading the selected video
//                // into the YouTube Player
//                videoID?.let { youTubePlayer.loadVideo(it, 0f) }
//            }
//
//            override fun onStateChange(
//                youTubePlayer: YouTubePlayer,
//                state: PlayerConstants.PlayerState
//            ) {
//                // this method is called if video has ended,
//                super.onStateChange(youTubePlayer, state)
//            }
//        })



        }
    }


//    private fun handleFavBtn(movieSeries: MovieResultModel, clicked: Boolean) {
//        addFavBtn = clicked
//        if (clicked) {
//            favBtn.setTextColor(resources.getColor(R.color.white))
//            favBtn.text = "added"
//            favBtn.setBackgroundColor(resources.getColor(R.color.purple_700))
//        } else {
//            favBtn.setTextColor(resources.getColor(R.color.black))
//            favBtn.text = "add"
//            favBtn.setBackgroundColor(resources.getColor(R.color.white))
//        }
//    }
