package com.syntax.mediaplayer.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService
import com.syntax.mediaplayer.R
import com.syntax.mediaplayer.db.PodPlayDatabase
import com.syntax.mediaplayer.repository.PodcastRepo
import com.syntax.mediaplayer.repository.PodcastRepo.PodcastUpdateInfo
import com.syntax.mediaplayer.ui.PodcastActivity
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch


class EpisodeUpdateService : JobService()
{
  override fun onStartJob(jobParameters: JobParameters): Boolean {

    val db = PodPlayDatabase.getInstance(this)
    val repo = PodcastRepo(FeedService.instance, db.podcastDao())
    val context = this

    launch(CommonPool) {
      repo.updatePodcastEpisodes({ podcastUpdates ->

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          createNotificationChannel()
        }

        for (podcastUpdate in podcastUpdates) {
          displayNotification(podcastUpdate)
        }

        jobFinished(jobParameters, false)
      })
    }

    return true
  }


  override fun onStopJob(jobParameters: JobParameters): Boolean {
    return true
  }

  @RequiresApi(Build.VERSION_CODES.O)
  private fun createNotificationChannel()
  {
    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (notificationManager.getNotificationChannel(EPISODE_CHANNEL_ID) == null) {
      val channel = NotificationChannel(EPISODE_CHANNEL_ID, "Episodes", NotificationManager.IMPORTANCE_DEFAULT)
      notificationManager.createNotificationChannel(channel)
    }
  }

  private fun displayNotification(podcastInfo: PodcastUpdateInfo) {

    val contentIntent = Intent(this, PodcastActivity::class.java)
    contentIntent.putExtra("PodcastFeedUrl", podcastInfo.feedUrl)
    val pendingContentIntent = PendingIntent.getActivity(this, 0,
        contentIntent, PendingIntent.FLAG_UPDATE_CURRENT)

    val notification = NotificationCompat.Builder(this, EPISODE_CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_episode_icon)
        .setContentTitle(getString(R.string.EpisodeNotificationTitle))
        .setContentText(getString(R.string.EpisodeNotificationText,
            podcastInfo.newCount, podcastInfo.name))
        .setNumber(podcastInfo.newCount)
        .setAutoCancel(true)
        .setContentIntent(pendingContentIntent)
        .build()

    val notificationManager =
        getSystemService(Context.NOTIFICATION_SERVICE)
            as NotificationManager

    notificationManager.notify(podcastInfo.name, 0, notification)
  }

  companion object {
    val EPISODE_CHANNEL_ID = "podplay_episodes_channel"
  }
}