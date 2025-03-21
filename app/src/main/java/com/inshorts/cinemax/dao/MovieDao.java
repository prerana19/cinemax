package com.inshorts.cinemax.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.inshorts.cinemax.model.Movie;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;

@Dao
public interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Completable insertMovie(Movie movie); // Completable for async insert

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Completable insertMovies(List<Movie> movies); // Completable for async insert

    @Query("SELECT * FROM movies")
    LiveData<List<Movie>> getAllMovies();

    // Get movies that are trending
    @Query("SELECT * FROM movies WHERE trending=1")
    LiveData<List<Movie>> getTrendingMovies();

    // Get movies that are now playing
    @Query("SELECT * FROM movies WHERE now_playing=1")
    LiveData<List<Movie>> getNowPlayingMovies();

    // Get movies that are bookmarked
    @Query("SELECT * FROM movies WHERE bookmarked=1")
    LiveData<List<Movie>> getBookmarkedMovies();

    // Set all movies as not trending
    @Query("UPDATE movies SET trending=0")
    Completable resetTrendingMovies();

    // Set all movies as not now playing
    @Query("UPDATE movies SET now_playing=0")
    Completable resetNowPlayingMovies();

    // Set movie as trending
    @Query("UPDATE movies SET trending = 1 WHERE id = :id")
    Completable updateTrending(int id);

    // Set movie as now playing
    @Query("UPDATE movies SET now_playing = 1 WHERE id = :id")
    Completable updateNowPlaying(int id);

    @Query("UPDATE movies SET bookmarked = 1 WHERE id = :id")
    Completable bookMarkMovie(int id);


    // Search movies by title or original title
    @Query("SELECT * FROM movies WHERE title LIKE :query OR original_title LIKE :query")
    LiveData<List<Movie>> searchMovies(String query);

    // Insert trending movie and update trending flag
    @Transaction
    default void  insertTrendingMovie(Movie movie) {
        System.out.println("Inserting trending movie: " + movie.getTitle());
        insertMovie(movie)
                .andThen(updateTrending(movie.getId()));
    }

    // Insert now playing movie and update now playing flag
    @Transaction
    default void insertNowPlayingMovie(Movie movie) {
         insertMovie(movie)
                .andThen(updateNowPlaying(movie.getId()));
    }
}
