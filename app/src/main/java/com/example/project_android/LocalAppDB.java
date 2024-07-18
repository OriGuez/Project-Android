//package com.example.project_android;
//
//import android.content.Context;
//
//import androidx.room.Database;
//import androidx.room.Room;
//import androidx.room.RoomDatabase;
//
//import com.example.project_android.dao.VideosDao;
//import com.example.project_android.model.Video;
//
//@Database(entities = {Video.class}, version = 1)
//public abstract class LocalAppDB extends RoomDatabase {
//    public abstract VideosDao VideosDao();
//
//    private static volatile LocalAppDB INSTANCE;
//
//    public static LocalAppDB getDatabase(final Context context) {
//        if (INSTANCE == null) {
//            synchronized (LocalAppDB.class) {
//                if (INSTANCE == null) {
//                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
//                                    LocalAppDB.class, "video_database").build();
//                }
//            }
//        }
//        return INSTANCE;
//    }
//}