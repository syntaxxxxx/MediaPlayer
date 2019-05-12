package com.syntax.mediaplayer.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import com.syntax.mediaplayer.model.Episode
import com.syntax.mediaplayer.model.Podcast

@Dao
interface PodcastDao {
  @Query("SELECT * FROM Podcast ORDER BY FeedTitle")
  fun loadPodcasts(): LiveData<List<Podcast>>

  @Query("SELECT * FROM Podcast ORDER BY FeedTitle")
  fun loadPodcastsStatic(): List<Podcast>

  @Query("SELECT * FROM Episode WHERE podcastId = :arg0 ORDER BY releaseDate DESC")
  fun loadEpisodes(podcastId: Long): List<Episode>

  @Query("SELECT * FROM Podcast WHERE feedUrl = :arg0")
  fun loadPodcast(url: String): Podcast?

  @Insert(onConflict = REPLACE)
  fun insertPodcast(podcast: Podcast): Long

  @Insert(onConflict = REPLACE)
  fun insertEpisode(episode: Episode): Long

  @Delete
  fun deletePodcast(podcast: Podcast)
}