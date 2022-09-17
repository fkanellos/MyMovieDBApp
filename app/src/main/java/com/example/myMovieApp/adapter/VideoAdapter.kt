package com.example.myMovieApp.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.myMovieApp.R
import com.example.myMovieApp.common.Constants
import com.example.myMovieApp.feature_movieApp.data.api.repository.dao.Result

class VideoAdapter(private val context: Context, private val namelist: List<Result>, private val check: Boolean) :
    RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {

    class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val inflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val itemView = inflater.inflate(R.layout.details_fragment, parent, false)

        return VideoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val video = this.namelist[position]
        val videoId = video.key

        val playButton = holder.itemView.findViewById<View>(R.id.playButton)
        playButton.setOnClickListener{

            val i = Intent()
            i.action = Intent.ACTION_VIEW
            i.data = Uri.parse(Constants.VIDEO_URL + videoId)
            ContextCompat.startActivity(context, i, null)
        }
    }

    override fun getItemCount(): Int {
        if (!check)
            return namelist.size

        return 0
    }


}