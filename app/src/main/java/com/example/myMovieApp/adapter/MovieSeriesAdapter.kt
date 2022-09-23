package com.example.myMovieApp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myMovieApp.R
import com.example.myMovieApp.common.Constants
import com.example.myMovieApp.feature_movieApp.data.api.repository.dao.MovieResultModel
import kotlinx.android.synthetic.main.recycler_item_view.view.*

class MovieSeriesAdapter : RecyclerView.Adapter<MovieSeriesAdapter.MoviesSeriesVieHolder>() {

    inner class MoviesSeriesVieHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val differCallback = object : DiffUtil.ItemCallback<MovieResultModel>() {
        override fun areItemsTheSame(
            oldItem: MovieResultModel,
            newItem: MovieResultModel
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: MovieResultModel,
            newItem: MovieResultModel
        ): Boolean {
            return oldItem == newItem
        }
    }
    /* we use AsyncListDiffer to consume the values from a LiveData of List and present the data simply for the adapter.
    * It computes differences in list contents via DiffUtil on a background thread as new Lists are received.
    * we also use differ.currentList to access the current list and present its data object*/
    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoviesSeriesVieHolder {
        return MoviesSeriesVieHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.recycler_item_view,
                parent,
                false
            )
        )
    }
    private var onItemClickListener: ((MovieResultModel) -> Unit)? = null


    override fun onBindViewHolder(holder: MoviesSeriesVieHolder, position: Int) {

        val movieSeries = differ.currentList[position]
        holder.itemView.apply {
            Glide.with(this).load(Constants.BASE_URL_PHOTOS.plus(movieSeries.backdrop_path)).into(imageView)
            text.text = if (movieSeries.title.isNullOrEmpty()) movieSeries.name else movieSeries.title
            rating.text=movieSeries.vote_average.toString()

            setOnClickListener {
                onItemClickListener?.let { it(movieSeries) }
            }
        }
    }
    fun setOnItemClickListener(listener: (MovieResultModel) -> Unit) {
        onItemClickListener = listener
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}