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
import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Completable insertMovie(Movie movie); // Completable for async insert

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Completable insertMovies(List<Movie> movies); // Completable for async insert

    @Query("SELECT * FROM movies")
    LiveData<List<Movie>> getAllMovies();

    @Query("SELECT * FROM movies WHERE id = :id")
    Flowable<Movie> getMovieById(int id);

    // Get movies that are trending
    @Query("SELECT * FROM movies WHERE trending=1")
    Flowable<List<Movie>> getTrendingMovies();

    // Get movies that are now playing
    @Query("SELECT * FROM movies WHERE now_playing=1")
    Flowable<List<Movie>> getNowPlayingMovies();

    @Query("SELECT * FROM movies WHERE now_playing=1 OR trending=1")
    Flowable<List<Movie>> getTrendingAndNowPlayingMovies();

    // Get movies that are bookmarked
    @Query("SELECT * FROM movies WHERE bookmarked=1")
    Flowable<List<Movie>> getBookmarkedMovies();

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

    // Search movies by title or original title
    @Query("SELECT * FROM movies " +
            "WHERE LOWER(title) LIKE LOWER('%' || :query || '%')" +
            " OR LOWER(original_title) LIKE LOWER('%' || :query || '%')" +
            " ORDER BY vote_average DESC"
    )
    Flowable<List<Movie>> searchMovies(String query);

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

    @Query("UPDATE movies " +
            "SET bookmarked = CASE " +
            "WHEN bookmarked = 1 THEN 0 ELSE 1 " +
            "END " +
            "WHERE id = :movieId")
    Completable toggleBookmark(int movieId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable update(Movie existingMovie);
}
