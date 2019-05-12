package com.syntax.mediaplayer.db;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.db.SupportSQLiteOpenHelper.Callback;
import android.arch.persistence.db.SupportSQLiteOpenHelper.Configuration;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.RoomOpenHelper;
import android.arch.persistence.room.RoomOpenHelper.Delegate;
import android.arch.persistence.room.util.TableInfo;
import android.arch.persistence.room.util.TableInfo.Column;
import android.arch.persistence.room.util.TableInfo.ForeignKey;
import android.arch.persistence.room.util.TableInfo.Index;
import java.lang.IllegalStateException;
import java.lang.Override;
import java.lang.String;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class PodPlayDatabase_Impl extends PodPlayDatabase {
  private volatile PodcastDao _podcastDao;

  protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration configuration) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(configuration, new RoomOpenHelper.Delegate(1) {
      public void createAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("CREATE TABLE IF NOT EXISTS `Podcast` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `feedUrl` TEXT NOT NULL, `feedTitle` TEXT NOT NULL, `feedDesc` TEXT NOT NULL, `imageUrl` TEXT NOT NULL, `lastUpdated` INTEGER NOT NULL)");
        _db.execSQL("CREATE TABLE IF NOT EXISTS `Episode` (`guid` TEXT NOT NULL, `podcastId` INTEGER, `title` TEXT NOT NULL, `description` TEXT NOT NULL, `mediaUrl` TEXT NOT NULL, `mimeType` TEXT NOT NULL, `releaseDate` INTEGER NOT NULL, `duration` TEXT NOT NULL, PRIMARY KEY(`guid`), FOREIGN KEY(`podcastId`) REFERENCES `Podcast`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        _db.execSQL("CREATE  INDEX `index_Episode_podcastId` ON `Episode` (`podcastId`)");
        _db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        _db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, \"82072405b6999342b9d224307f0dfc33\")");
      }

      public void dropAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("DROP TABLE IF EXISTS `Podcast`");
        _db.execSQL("DROP TABLE IF EXISTS `Episode`");
      }

      protected void onCreate(SupportSQLiteDatabase _db) {
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onCreate(_db);
          }
        }
      }

      public void onOpen(SupportSQLiteDatabase _db) {
        mDatabase = _db;
        _db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(_db);
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onOpen(_db);
          }
        }
      }

      protected void validateMigration(SupportSQLiteDatabase _db) {
        final HashMap<String, TableInfo.Column> _columnsPodcast = new HashMap<String, TableInfo.Column>(6);
        _columnsPodcast.put("id", new TableInfo.Column("id", "INTEGER", false, 1));
        _columnsPodcast.put("feedUrl", new TableInfo.Column("feedUrl", "TEXT", true, 0));
        _columnsPodcast.put("feedTitle", new TableInfo.Column("feedTitle", "TEXT", true, 0));
        _columnsPodcast.put("feedDesc", new TableInfo.Column("feedDesc", "TEXT", true, 0));
        _columnsPodcast.put("imageUrl", new TableInfo.Column("imageUrl", "TEXT", true, 0));
        _columnsPodcast.put("lastUpdated", new TableInfo.Column("lastUpdated", "INTEGER", true, 0));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPodcast = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesPodcast = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoPodcast = new TableInfo("Podcast", _columnsPodcast, _foreignKeysPodcast, _indicesPodcast);
        final TableInfo _existingPodcast = TableInfo.read(_db, "Podcast");
        if (! _infoPodcast.equals(_existingPodcast)) {
          throw new IllegalStateException("Migration didn't properly handle Podcast(com.syntax.mediaplayer.model.Podcast).\n"
                  + " Expected:\n" + _infoPodcast + "\n"
                  + " Found:\n" + _existingPodcast);
        }
        final HashMap<String, TableInfo.Column> _columnsEpisode = new HashMap<String, TableInfo.Column>(8);
        _columnsEpisode.put("guid", new TableInfo.Column("guid", "TEXT", true, 1));
        _columnsEpisode.put("podcastId", new TableInfo.Column("podcastId", "INTEGER", false, 0));
        _columnsEpisode.put("title", new TableInfo.Column("title", "TEXT", true, 0));
        _columnsEpisode.put("description", new TableInfo.Column("description", "TEXT", true, 0));
        _columnsEpisode.put("mediaUrl", new TableInfo.Column("mediaUrl", "TEXT", true, 0));
        _columnsEpisode.put("mimeType", new TableInfo.Column("mimeType", "TEXT", true, 0));
        _columnsEpisode.put("releaseDate", new TableInfo.Column("releaseDate", "INTEGER", true, 0));
        _columnsEpisode.put("duration", new TableInfo.Column("duration", "TEXT", true, 0));
        final HashSet<TableInfo.ForeignKey> _foreignKeysEpisode = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysEpisode.add(new TableInfo.ForeignKey("Podcast", "CASCADE", "NO ACTION",Arrays.asList("podcastId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesEpisode = new HashSet<TableInfo.Index>(1);
        _indicesEpisode.add(new TableInfo.Index("index_Episode_podcastId", false, Arrays.asList("podcastId")));
        final TableInfo _infoEpisode = new TableInfo("Episode", _columnsEpisode, _foreignKeysEpisode, _indicesEpisode);
        final TableInfo _existingEpisode = TableInfo.read(_db, "Episode");
        if (! _infoEpisode.equals(_existingEpisode)) {
          throw new IllegalStateException("Migration didn't properly handle Episode(com.syntax.mediaplayer.model.Episode).\n"
                  + " Expected:\n" + _infoEpisode + "\n"
                  + " Found:\n" + _existingEpisode);
        }
      }
    }, "82072405b6999342b9d224307f0dfc33");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(configuration.context)
        .name(configuration.name)
        .callback(_openCallback)
        .build();
    final SupportSQLiteOpenHelper _helper = configuration.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  protected InvalidationTracker createInvalidationTracker() {
    return new InvalidationTracker(this, "Podcast","Episode");
  }

  @Override
  public PodcastDao podcastDao() {
    if (_podcastDao != null) {
      return _podcastDao;
    } else {
      synchronized(this) {
        if(_podcastDao == null) {
          _podcastDao = new PodcastDao_Impl(this);
        }
        return _podcastDao;
      }
    }
  }
}
