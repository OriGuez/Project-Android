package com.example.project_android.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.project_android.model.Video;

import java.util.List;

@Dao
public interface VideosDao {
    //notice that the changes made here (deleting/changing etc) are for room and not in remote DB.

    @Query("SELECT * FROM Video")
    List<Video> index();
    @Query("SELECT * FROM Video WHERE vidID = :id")
    Video get(String id);

//    @Query("SELECT * FROM Video WHERE id = :id")
//    Video get(int id);

    @Insert
    void insert(Video... videos);

    @Update
    void update(Video... videos);

    @Delete
    void delete(Video... videos);
    @Query("DELETE FROM Video WHERE vidID = :videoId")
    int deleteById(String videoId);
    @Insert
    void insert(List<Video> videos);  // For inserting a list of videos
    @Query("DELETE FROM Video")
    void clearAll();
}
