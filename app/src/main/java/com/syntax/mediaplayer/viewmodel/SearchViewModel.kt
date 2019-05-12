package com.syntax.mediaplayer.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.syntax.mediaplayer.repository.ItunesRepo
import com.syntax.mediaplayer.service.PodcastResponse
import com.syntax.mediaplayer.util.DateUtils

class SearchViewModel(application: Application) : AndroidViewModel(application) {

  var iTunesRepo: ItunesRepo? = null
  
  data class PodcastSummaryViewData(
      var name: String? = "",
      var lastUpdated: String? = "",
      var imageUrl: String? = "",
      var feedUrl: String? = "")
  
  private fun itunesPodcastToPodcastSummaryView(
      itunesPodcast: PodcastResponse.ItunesPodcast):
      PodcastSummaryViewData {
    return PodcastSummaryViewData(
        itunesPodcast.collectionCensoredName,
        DateUtils.jsonDateToShortDate(itunesPodcast.releaseDate),
        itunesPodcast.artworkUrl100,
        itunesPodcast.feedUrl)
  }

  fun searchPodcasts(term: String, callback: (List<PodcastSummaryViewData>) -> Unit) {
    iTunesRepo?.searchByTerm(term, { results ->
      if (results == null) {
        callback(emptyList())
      } else {
        val searchViews = results.map { podcast ->
          itunesPodcastToPodcastSummaryView(podcast)
        }
        searchViews.let { it -> callback(it) }
      }
    })
  }
}
