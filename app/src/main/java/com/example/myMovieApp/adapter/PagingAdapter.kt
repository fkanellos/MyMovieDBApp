package com.example.myMovieApp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.myMovieApp.R
import com.example.myMovieApp.common.Constants
import com.example.myMovieApp.databinding.RecyclerItemViewBinding
import com.example.myMovieApp.feature_movieApp.data.api.repository.dao.MovieResultModel

class PagingAdapter(private val listener: OnItemClickListener) :
    PagingDataAdapter<MovieResultModel, PagingAdapter.MovieViewHolder>(MOVIE_COMPARATOR) {

    inner class MovieViewHolder(private val binding: RecyclerItemViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                // in case I add animations
                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    getItem(bindingAdapterPosition)?.let {
                        listener.itemClicked(it)
                    }
                }
            }
        }

        fun bind(movies: MovieResultModel) {
            binding.apply {
//                text.text = movies.title
                text.text = if (movies.title.isNullOrEmpty()) movies.name else movies.title
                rating.text=movies.vote_average.toString()
                Glide.with(itemView).load(Constants.BASE_URL_PHOTOS.plus(movies.backdrop_path))
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.ic_error).into(imageView)
            }
        }
    }

    interface OnItemClickListener {
        fun itemClicked(movie: MovieResultModel)
    }

    companion object {
        private val MOVIE_COMPARATOR = object : DiffUtil.ItemCallback<MovieResultModel>() {
            override fun areItemsTheSame(oldItem: MovieResultModel, newItem: MovieResultModel): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: MovieResultModel, newItem: MovieResultModel) = oldItem == newItem
        }
    }

    //adapter
    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val currentItem = getItem(position)
        currentItem?.let {
            holder.bind(it)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding =
            RecyclerItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return MovieViewHolder(binding)
    }
}