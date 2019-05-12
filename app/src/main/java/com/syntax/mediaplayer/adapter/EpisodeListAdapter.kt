package com.syntax.mediaplayer.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.syntax.mediaplayer.R
import com.syntax.mediaplayer.util.DateUtils
import com.syntax.mediaplayer.util.HtmlUtils
import com.syntax.mediaplayer.viewmodel.PodcastViewModel.EpisodeViewData

class EpisodeListAdapter(private var episodeViewList: List<EpisodeViewData>?,
                         private val episodeListAdapterListener: EpisodeListAdapterListener) :
    RecyclerView.Adapter<EpisodeListAdapter.ViewHolder>() {

  interface EpisodeListAdapterListener {
    fun onSelectedEpisode(episodeViewData: EpisodeViewData)
  }

  class ViewHolder(v: View, private val episodeListAdapterListener: EpisodeListAdapterListener) :
      RecyclerView.ViewHolder(v) {

    init {
      v.setOnClickListener {
        episodeViewData?.let {
          episodeListAdapterListener.onSelectedEpisode(it)
        }
      }
    }

    var episodeViewData: EpisodeViewData? = null
    val titleTextView: TextView = v.findViewById(R.id.titleView)
    val descTextView: TextView = v.findViewById(R.id.descView)
    val durationTextView: TextView = v.findViewById(R.id.durationView)
    val releaseDateTextView: TextView = v.findViewById(R.id.releaseDateView)
  }

  fun setViewData(episodeList: List<EpisodeViewData>) {
    episodeViewList = episodeList
    this.notifyDataSetChanged()
  }

  override fun onCreateViewHolder(parent: ViewGroup,
                                  viewType: Int): EpisodeListAdapter.ViewHolder {
    return ViewHolder(LayoutInflater.from(parent.context)
        .inflate(R.layout.episode_item, parent, false), episodeListAdapterListener)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val episodeViewList = episodeViewList ?: return
    val episodeView = episodeViewList[position]

    holder.episodeViewData = episodeView
    holder.titleTextView.text = episodeView.title
    holder.descTextView.text =  HtmlUtils.htmlToSpannable(episodeView.description ?: "")
    holder.durationTextView.text = episodeView.duration
    holder.releaseDateTextView.text = episodeView.releaseDate?.let { DateUtils.dateToShortDate(it) }
  }

  override fun getItemCount(): Int {
    return episodeViewList?.size ?: 0
  }
}