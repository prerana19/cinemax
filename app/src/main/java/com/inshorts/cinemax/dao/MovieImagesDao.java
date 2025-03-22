package com.inshorts.cinemax.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.inshorts.cinemax.model.MovieImages;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface MovieImagesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(MovieImages movieImages);

    @Update
    Completable update(MovieImages movieImages);

    @Query("SELECT * FROM movie_images WHERE id = :id")
    Maybe<MovieImages> getMovieImagesById(int id);

    @Query("SELECT poster FROM movie_images WHERE id = :id")
    String getMoviePosterById(int id);

    @Query("SELECT backdrop FROM movie_images WHERE id = :id")
    String getMovieBackdropById(int id);

    @Query("SELECT * FROM movie_images")
    List<MovieImages> getAllMovieImages();
}