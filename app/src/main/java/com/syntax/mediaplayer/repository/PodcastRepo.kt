package com.syntax.mediaplayer.repository

import android.arch.lifecycle.LiveData
import com.syntax.mediaplayer.db.PodcastDao
import com.syntax.mediaplayer.model.Episode
import com.syntax.mediaplayer.model.Podcast
import com.syntax.mediaplayer.service.FeedService
import com.syntax.mediaplayer.service.RssFeedResponse
import com.syntax.mediaplayer.util.DateUtils
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

class PodcastRepo(private var feedService: FeedService,
                  private var podcastDao: PodcastDao) {

  fun getPodcast(feedUrl: String, callback: (Podcast?) -> Unit) {

    launch(CommonPool) {

      val podcast = podcastDao.loadPodcast(feedUrl)

      if (podcast != null) {
        podcast.id?.let {
          podcast.episodes = podcastDao.loadEpisodes(it)
          launch(UI){
            callback(podcast)
          }
        }
      } else {

        feedService.getFeed(feedUrl, { feedResponse ->

          if (feedResponse == null) {
            launch(UI) {
              callback(null)
            }
          } else {
            val podcast = rssResponseToPodcast(feedUrl, "", feedResponse)
            launch(UI) {
              callback(podcast)
            }
          }
        })
      }
    }

  }

  fun getAll(): LiveData<List<Podcast>>
  {
    return podcastDao.loadPodcasts()
  }

  fun updatePodcastEpisodes(callback: (List<PodcastUpdateInfo>) -> Unit) {

    val updatedPodcasts: MutableList<PodcastUpdateInfo> = mutableListOf()

    val podcasts = podcastDao.loadPodcastsStatic()

    var processCount = podcasts.count()
    for (podcast in podcasts) {
      getNewEpisodes(podcast, { newEpisodes ->

        if (newEpisodes.count() > 0) {
          saveNewEpisodes(podcast.id!!, newEpisodes)
          updatedPodcasts.add(PodcastUpdateInfo(podcast.feedUrl, podcast.feedTitle, newEpisodes.count
          ()))
        }
        processCount--

        if (processCount == 0) {
          callback(updatedPodcasts)
        }
      })
    }
  }

  private fun getNewEpisodes(localPodcast: Podcast, callBack: (List<Episode>) -> Unit) {

    feedService.getFeed(localPodcast.feedUrl, { response ->

      if (response != null) {
        val remotePodcast = rssResponseToPodcast(localPodcast.feedUrl, localPodcast.imageUrl, response)
        remotePodcast?.let {

          val localEpisodes = podcastDao.loadEpisodes(localPodcast.id!!)

          val newEpisodes = remotePodcast.episodes.filter { episode ->
            localEpisodes.find { episode.guid == it.guid } == null
          }

          callBack(newEpisodes)
        }
      } else {
        callBack(listOf())
      }
    })
  }

  private fun saveNewEpisodes(podcastId: Long, episodes: List<Episode>) {
    launch(CommonPool) {
      for (episode in episodes) {
        episode.podcastId = podcastId
        podcastDao.insertEpisode(episode)
      }
    }
  }

  fun save(podcast: Podcast) {
    launch(CommonPool) {
      val podcastId = podcastDao.insertPodcast(podcast)
      for (episode in podcast.episodes) {
        episode.podcastId = podcastId
        podcastDao.insertEpisode(episode)
      }
    }
  }

  fun delete(podcast: Podcast) {
    launch(CommonPool) {
      podcastDao.deletePodcast(podcast)
    }
  }

  private fun rssResponseToPodcast(feedUrl: String, imageUrl: String, rssResponse:
  RssFeedResponse): Podcast? {

    val items = rssResponse.episodes ?: return null
    val description = if (rssResponse.description == "") rssResponse.summary else rssResponse.description

    return Podcast(null, feedUrl, rssResponse.title, description, imageUrl,
        rssResponse.lastUpdated, episodes = rssItemsToEpisodes(items))
  }

  private fun rssItemsToEpisodes(episodeResponses: List<RssFeedResponse.EpisodeResponse>): List<Episode> {
    return episodeResponses.map {
      Episode(
          it.guid ?: "",
          null,
          it.title ?: "",
          it.description ?: "",
          it.url ?: "",
          it.type ?: "",
          DateUtils.xmlDateToDate(it.pubDate),
          it.duration ?: ""
      )
    }
  }

  class PodcastUpdateInfo (val feedUrl: String, val name: String, val newCount: Int)
}