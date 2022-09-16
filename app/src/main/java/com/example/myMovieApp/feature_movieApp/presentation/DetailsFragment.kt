package com.example.myMovieApp.feature_movieApp.presentation

import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.MediaController
import android.widget.Toast
import android.widget.VideoView
import androidx.compose.runtime.key
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewmodel.compose.viewModel
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
//        viewModel.searchMovieTrailer()





        videoView = view.findViewById(R.id.videoView) as VideoView?

        if (mediaController == null) {
            mediaController = MediaController(requireContext())
            mediaController!!.setAnchorView(this.videoView)
        }
        videoView!!.setMediaController(mediaController)

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
//            title.text = model.title
            rating.text = model.vote_average.toString()
            text.text = model.overview
            if (!model.videos?.results?.get(0)?.key.isNullOrEmpty()){
                val videoKey = model.videos!!.results!![0].key
                val videoUrl = String.format(Constants.VIDEO_URL,videoKey)
                val videoUri = Uri.parse(videoUrl)
                videoView!!.setVideoURI(videoUri)
                videoView!!.requestFocus()
                videoView!!.start()

            }


        }
    }
}