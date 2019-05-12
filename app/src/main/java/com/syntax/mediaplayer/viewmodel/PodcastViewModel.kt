package com.syntax.mediaplayer.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import com.syntax.mediaplayer.model.Episode
import com.syntax.mediaplayer.model.Podcast
import com.syntax.mediaplayer.repository.PodcastRepo
import com.syntax.mediaplayer.util.DateUtils
import com.syntax.mediaplayer.viewmodel.SearchViewModel.PodcastSummaryViewData
import java.util.*

class PodcastViewModel(application: Application) : AndroidViewModel
(application) {

  var podcastRepo: PodcastRepo? = null
  var activePodcastViewData: PodcastViewData? = null
  var activeEpisodeViewData: EpisodeViewData? = null
  private var livePodcastData: LiveData<List<PodcastSummaryViewData>>? = null

  private var activePodcast: Podcast? = null

  fun setActivePodcast(feedUrl: String, callback: (PodcastSummaryViewData?) -> Unit) {

    val repo = podcastRepo ?: return

    repo.getPodcast(feedUrl, {
      if (it == null) {
        callback(null)
      } else {
        activePodcastViewData = podcastToPodcastView(it)
        activePodcast = it
        callback(podcastToSummaryView(it))
      }
    })
  }

  fun getPodcast(podcastSummaryViewData: PodcastSummaryViewData, callback: (PodcastViewData?) ->
  Unit) {

    val repo = podcastRepo ?: return
    val feedUrl = podcastSummaryViewData.feedUrl ?: return

    repo.getPodcast(feedUrl, {
      it?.let {

        it.feedTitle = podcastSummaryViewData.name ?: ""
        it.imageUrl = podcastSummaryViewData.imageUrl ?: ""
        activePodcastViewData = podcastToPodcastView(it)
        activePodcast = it
        callback(activePodcastViewData)
      }
    })
  }

  fun getPodcasts(): LiveData<List<PodcastSummaryViewData>>? {

    val repo = podcastRepo ?: return null

    if (livePodcastData == null) {

      val liveData = repo.getAll()

      livePodcastData = Transformations.map(liveData) { podcastList ->
        podcastList.map { podcast ->
          podcastToSummaryView(podcast)
        }
      }
    }

    return livePodcastData
  }

  fun saveActivePodcast() {
    val repo = podcastRepo ?: return
    activePodcast?.let {
      repo.save(it)
    }
  }

  fun deleteActivePodcast() {
    val repo = podcastRepo ?: return
    activePodcast?.let {
      repo.delete(it)
    }
  }

  private fun podcastToSummaryView(podcast: Podcast): PodcastSummaryViewData {
    return PodcastSummaryViewData(
        podcast.feedTitle,
        DateUtils.dateToShortDate(podcast.lastUpdated),
        podcast.imageUrl,
        podcast.feedUrl)
  }

  private fun podcastToPodcastView(podcast: Podcast): PodcastViewData {
    return PodcastViewData(
        podcast.id != null,
        podcast.feedTitle,
        podcast.feedUrl,
        podcast.feedDesc,
        podcast.imageUrl,
        episodesToEpisodesView(podcast.episodes)
    )
  }

  private fun episodesToEpisodesView(episodes: List<Episode>): List<EpisodeViewData> {
    return episodes.map {
      val isVideo = it.mimeType.startsWith("video")
      EpisodeViewData(it.guid, it.title, it.description, it.mediaUrl,
          it.releaseDate, it.duration, isVideo)
    }
  }

  data class PodcastViewData(
      var subscribed: Boolean = false,
      var feedTitle: String? = "",
      var feedUrl: String? = "",
      var feedDesc: String? = "",
      var imageUrl: String? = "",
      var episodes: List<EpisodeViewData>
  )

  data class EpisodeViewData (
      var guid: String? = "",
      var title: String? = "",
      var description: String? = "",
      var mediaUrl: String? = "",
      var releaseDate: Date? = null,
      var duration: String? = "",
      var isVideo: Boolean = false
  )
}