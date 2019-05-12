package com.syntax.mediaplayer.db;

import android.arch.lifecycle.ComputableLiveData;
import android.arch.lifecycle.LiveData;
import android.arch.persistence.db.SupportSQLiteStatement;
import android.arch.persistence.room.EntityDeletionOrUpdateAdapter;
import android.arch.persistence.room.EntityInsertionAdapter;
import android.arch.persistence.room.InvalidationTracker.Observer;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.RoomSQLiteQuery;
import android.database.Cursor;
import android.support.annotation.NonNull;
import com.syntax.mediaplayer.model.Episode;
import com.syntax.mediaplayer.model.Podcast;
import java.lang.Long;
import java.lang.Override;
import java.lang.String;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class PodcastDao_Impl implements PodcastDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter __insertionAdapterOfPodcast;

  private final Converters __converters = new Converters();

  private final EntityInsertionAdapter __insertionAdapterOfEpisode;

  private final EntityDeletionOrUpdateAdapter __deletionAdapterOfPodcast;

  public PodcastDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfPodcast = new EntityInsertionAdapter<Podcast>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR REPLACE INTO `Podcast`(`id`,`feedUrl`,`feedTitle`,`feedDesc`,`imageUrl`,`lastUpdated`) VALUES (?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Podcast value) {
        if (value.getId() == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindLong(1, value.getId());
        }
        if (value.getFeedUrl() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getFeedUrl());
        }
        if (value.getFeedTitle() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getFeedTitle());
        }
        if (value.getFeedDesc() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getFeedDesc());
        }
        if (value.getImageUrl() == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.getImageUrl());
        }
        final Long _tmp;
        _tmp = __converters.toTimestamp(value.getLastUpdated());
        if (_tmp == null) {
          stmt.bindNull(6);
        } else {
          stmt.bindLong(6, _tmp);
        }
      }
    };
    this.__insertionAdapterOfEpisode = new EntityInsertionAdapter<Episode>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR REPLACE INTO `Episode`(`guid`,`podcastId`,`title`,`description`,`mediaUrl`,`mimeType`,`releaseDate`,`duration`) VALUES (?,?,?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Episode value) {
        if (value.getGuid() == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.getGuid());
        }
        if (value.getPodcastId() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindLong(2, value.getPodcastId());
        }
        if (value.getTitle() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getTitle());
        }
        if (value.getDescription() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getDescription());
        }
        if (value.getMediaUrl() == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.getMediaUrl());
        }
        if (value.getMimeType() == null) {
          stmt.bindNull(6);
        } else {
          stmt.bindString(6, value.getMimeType());
        }
        final Long _tmp;
        _tmp = __converters.toTimestamp(value.getReleaseDate());
        if (_tmp == null) {
          stmt.bindNull(7);
        } else {
          stmt.bindLong(7, _tmp);
        }
        if (value.getDuration() == null) {
          stmt.bindNull(8);
        } else {
          stmt.bindString(8, value.getDuration());
        }
      }
    };
    this.__deletionAdapterOfPodcast = new EntityDeletionOrUpdateAdapter<Podcast>(__db) {
      @Override
      public String createQuery() {
        return "DELETE FROM `Podcast` WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Podcast value) {
        if (value.getId() == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindLong(1, value.getId());
        }
      }
    };
  }

  @Override
  public long insertPodcast(Podcast arg0) {
    __db.beginTransaction();
    try {
      long _result = __insertionAdapterOfPodcast.insertAndReturnId(arg0);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public long insertEpisode(Episode arg0) {
    __db.beginTransaction();
    try {
      long _result = __insertionAdapterOfEpisode.insertAndReturnId(arg0);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deletePodcast(Podcast arg0) {
    __db.beginTransaction();
    try {
      __deletionAdapterOfPodcast.handle(arg0);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public LiveData<List<Podcast>> loadPodcasts() {
    final String _sql = "SELECT * FROM Podcast ORDER BY FeedTitle";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return new ComputableLiveData<List<Podcast>>() {
      private Observer _observer;

      @Override
      protected List<Podcast> compute() {
        if (_observer == null) {
          _observer = new Observer("Podcast") {
            @Override
            public void onInvalidated(@NonNull Set<String> tables) {
              invalidate();
            }
          };
          __db.getInvalidationTracker().addWeakObserver(_observer);
        }
        final Cursor _cursor = __db.query(_statement);
        try {
          final int _cursorIndexOfId = _cursor.getColumnIndexOrThrow("id");
          final int _cursorIndexOfFeedUrl = _cursor.getColumnIndexOrThrow("feedUrl");
          final int _cursorIndexOfFeedTitle = _cursor.getColumnIndexOrThrow("feedTitle");
          final int _cursorIndexOfFeedDesc = _cursor.getColumnIndexOrThrow("feedDesc");
          final int _cursorIndexOfImageUrl = _cursor.getColumnIndexOrThrow("imageUrl");
          final int _cursorIndexOfLastUpdated = _cursor.getColumnIndexOrThrow("lastUpdated");
          final List<Podcast> _result = new ArrayList<Podcast>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final Podcast _item;
            _item = new Podcast();
            final Long _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getLong(_cursorIndexOfId);
            }
            _item.setId(_tmpId);
            final String _tmpFeedUrl;
            _tmpFeedUrl = _cursor.getString(_cursorIndexOfFeedUrl);
            _item.setFeedUrl(_tmpFeedUrl);
            final String _tmpFeedTitle;
            _tmpFeedTitle = _cursor.getString(_cursorIndexOfFeedTitle);
            _item.setFeedTitle(_tmpFeedTitle);
            final String _tmpFeedDesc;
            _tmpFeedDesc = _cursor.getString(_cursorIndexOfFeedDesc);
            _item.setFeedDesc(_tmpFeedDesc);
            final String _tmpImageUrl;
            _tmpImageUrl = _cursor.getString(_cursorIndexOfImageUrl);
            _item.setImageUrl(_tmpImageUrl);
            final Date _tmpLastUpdated;
            final Long _tmp;
            if (_cursor.isNull(_cursorIndexOfLastUpdated)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getLong(_cursorIndexOfLastUpdated);
            }
            _tmpLastUpdated = __converters.fromTimestamp(_tmp);
            _item.setLastUpdated(_tmpLastUpdated);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    }.getLiveData();
  }

  @Override
  public List<Podcast> loadPodcastsStatic() {
    final String _sql = "SELECT * FROM Podcast ORDER BY FeedTitle";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final Cursor _cursor = __db.query(_statement);
    try {
      final int _cursorIndexOfId = _cursor.getColumnIndexOrThrow("id");
      final int _cursorIndexOfFeedUrl = _cursor.getColumnIndexOrThrow("feedUrl");
      final int _cursorIndexOfFeedTitle = _cursor.getColumnIndexOrThrow("feedTitle");
      final int _cursorIndexOfFeedDesc = _cursor.getColumnIndexOrThrow("feedDesc");
      final int _cursorIndexOfImageUrl = _cursor.getColumnIndexOrThrow("imageUrl");
      final int _cursorIndexOfLastUpdated = _cursor.getColumnIndexOrThrow("lastUpdated");
      final List<Podcast> _result = new ArrayList<Podcast>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final Podcast _item;
        _item = new Podcast();
        final Long _tmpId;
        if (_cursor.isNull(_cursorIndexOfId)) {
          _tmpId = null;
        } else {
          _tmpId = _cursor.getLong(_cursorIndexOfId);
        }
        _item.setId(_tmpId);
        final String _tmpFeedUrl;
        _tmpFeedUrl = _cursor.getString(_cursorIndexOfFeedUrl);
        _item.setFeedUrl(_tmpFeedUrl);
        final String _tmpFeedTitle;
        _tmpFeedTitle = _cursor.getString(_cursorIndexOfFeedTitle);
        _item.setFeedTitle(_tmpFeedTitle);
        final String _tmpFeedDesc;
        _tmpFeedDesc = _cursor.getString(_cursorIndexOfFeedDesc);
        _item.setFeedDesc(_tmpFeedDesc);
        final String _tmpImageUrl;
        _tmpImageUrl = _cursor.getString(_cursorIndexOfImageUrl);
        _item.setImageUrl(_tmpImageUrl);
        final Date _tmpLastUpdated;
        final Long _tmp;
        if (_cursor.isNull(_cursorIndexOfLastUpdated)) {
          _tmp = null;
        } else {
          _tmp = _cursor.getLong(_cursorIndexOfLastUpdated);
        }
        _tmpLastUpdated = __converters.fromTimestamp(_tmp);
        _item.setLastUpdated(_tmpLastUpdated);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<Episode> loadEpisodes(long arg0) {
    final String _sql = "SELECT * FROM Episode WHERE podcastId = ? ORDER BY releaseDate DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, arg0);
    final Cursor _cursor = __db.query(_statement);
    try {
      final int _cursorIndexOfGuid = _cursor.getColumnIndexOrThrow("guid");
      final int _cursorIndexOfPodcastId = _cursor.getColumnIndexOrThrow("podcastId");
      final int _cursorIndexOfTitle = _cursor.getColumnIndexOrThrow("title");
      final int _cursorIndexOfDescription = _cursor.getColumnIndexOrThrow("description");
      final int _cursorIndexOfMediaUrl = _cursor.getColumnIndexOrThrow("mediaUrl");
      final int _cursorIndexOfMimeType = _cursor.getColumnIndexOrThrow("mimeType");
      final int _cursorIndexOfReleaseDate = _cursor.getColumnIndexOrThrow("releaseDate");
      final int _cursorIndexOfDuration = _cursor.getColumnIndexOrThrow("duration");
      final List<Episode> _result = new ArrayList<Episode>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final Episode _item;
        _item = new Episode();
        final String _tmpGuid;
        _tmpGuid = _cursor.getString(_cursorIndexOfGuid);
        _item.setGuid(_tmpGuid);
        final Long _tmpPodcastId;
        if (_cursor.isNull(_cursorIndexOfPodcastId)) {
          _tmpPodcastId = null;
        } else {
          _tmpPodcastId = _cursor.getLong(_cursorIndexOfPodcastId);
        }
        _item.setPodcastId(_tmpPodcastId);
        final String _tmpTitle;
        _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
        _item.setTitle(_tmpTitle);
        final String _tmpDescription;
        _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
        _item.setDescription(_tmpDescription);
        final String _tmpMediaUrl;
        _tmpMediaUrl = _cursor.getString(_cursorIndexOfMediaUrl);
        _item.setMediaUrl(_tmpMediaUrl);
        final String _tmpMimeType;
        _tmpMimeType = _cursor.getString(_cursorIndexOfMimeType);
        _item.setMimeType(_tmpMimeType);
        final Date _tmpReleaseDate;
        final Long _tmp;
        if (_cursor.isNull(_cursorIndexOfReleaseDate)) {
          _tmp = null;
        } else {
          _tmp = _cursor.getLong(_cursorIndexOfReleaseDate);
        }
        _tmpReleaseDate = __converters.fromTimestamp(_tmp);
        _item.setReleaseDate(_tmpReleaseDate);
        final String _tmpDuration;
        _tmpDuration = _cursor.getString(_cursorIndexOfDuration);
        _item.setDuration(_tmpDuration);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public Podcast loadPodcast(String arg0) {
    final String _sql = "SELECT * FROM Podcast WHERE feedUrl = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (arg0 == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, arg0);
    }
    final Cursor _cursor = __db.query(_statement);
    try {
      final int _cursorIndexOfId = _cursor.getColumnIndexOrThrow("id");
      final int _cursorIndexOfFeedUrl = _cursor.getColumnIndexOrThrow("feedUrl");
      final int _cursorIndexOfFeedTitle = _cursor.getColumnIndexOrThrow("feedTitle");
      final int _cursorIndexOfFeedDesc = _cursor.getColumnIndexOrThrow("feedDesc");
      final int _cursorIndexOfImageUrl = _cursor.getColumnIndexOrThrow("imageUrl");
      final int _cursorIndexOfLastUpdated = _cursor.getColumnIndexOrThrow("lastUpdated");
      final Podcast _result;
      if(_cursor.moveToFirst()) {
        _result = new Podcast();
        final Long _tmpId;
        if (_cursor.isNull(_cursorIndexOfId)) {
          _tmpId = null;
        } else {
          _tmpId = _cursor.getLong(_cursorIndexOfId);
        }
        _result.setId(_tmpId);
        final String _tmpFeedUrl;
        _tmpFeedUrl = _cursor.getString(_cursorIndexOfFeedUrl);
        _result.setFeedUrl(_tmpFeedUrl);
        final String _tmpFeedTitle;
        _tmpFeedTitle = _cursor.getString(_cursorIndexOfFeedTitle);
        _result.setFeedTitle(_tmpFeedTitle);
        final String _tmpFeedDesc;
        _tmpFeedDesc = _cursor.getString(_cursorIndexOfFeedDesc);
        _result.setFeedDesc(_tmpFeedDesc);
        final String _tmpImageUrl;
        _tmpImageUrl = _cursor.getString(_cursorIndexOfImageUrl);
        _result.setImageUrl(_tmpImageUrl);
        final Date _tmpLastUpdated;
        final Long _tmp;
        if (_cursor.isNull(_cursorIndexOfLastUpdated)) {
          _tmp = null;
        } else {
          _tmp = _cursor.getLong(_cursorIndexOfLastUpdated);
        }
        _tmpLastUpdated = __converters.fromTimestamp(_tmp);
        _result.setLastUpdated(_tmpLastUpdated);
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }
}
