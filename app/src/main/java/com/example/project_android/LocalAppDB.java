package com.example.project_android;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import com.example.project_android.dao.DateConverter;
import com.example.project_android.dao.VideosDao;
import com.example.project_android.model.Video;

@Database(entities = {Video.class}, version = 1)
@TypeConverters({DateConverter.class})
public abstract class LocalAppDB extends RoomDatabase {
    public abstract VideosDao VideoDao();
    public static final String DATABASE_NAME = "ViewTube.db";

    private static volatile LocalAppDB INSTANCE;

    public static LocalAppDB getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (LocalAppDB.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    LocalAppDB.class, DATABASE_NAME).build();
                }
            }
        }
        return INSTANCE;
    }
}