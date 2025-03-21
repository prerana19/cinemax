package com.inshorts.cinemax.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.inshorts.cinemax.client.RetrofitClient;
import com.inshorts.cinemax.dao.MovieDao;
import com.inshorts.cinemax.database.MoviesDatabase;
import com.inshorts.cinemax.model.Movie;
import com.inshorts.cinemax.service.MoviesService;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MoviesRepository {
//    LiveData<Movies> getTrendingMovies();
    private final MovieDao movieDao;
    private final MoviesService moviesService;

    public MoviesRepository(Context context) {
        MoviesDatabase moviesDatabase = MoviesDatabase.getInstance(context);
        movieDao = moviesDatabase.movieDao();
        moviesService = RetrofitClient.getClient().create(MoviesService.class);
    }

    // Fetch trending movies from API
    public Completable fetchTrendingMoviesfromApi() {
        return moviesService.getTrendingMovies()
                .subscribeOn(Schedulers.io()) // Run API call on background thread
                .observeOn(Schedulers.io()) // Observe result on IO thread
                .flatMapCompletable(movies -> Completable.fromRunnable(() -> {
                    System.out.println("Movies: " + movies.getMovies().get(0).getTitle());
                    movieDao.resetTrendingMovies();
                    for(Movie movie: movies.getMovies()) {
                        movieDao.insertTrendingMovie(movie);
                    }
                }));
    }

    // Fetch now playing movies from API

    public Completable fetchNowPlayingMoviesfromApi() {
        return moviesService.getNowPlayingMovies()
                .subscribeOn(Schedulers.io()) // Run API call on background thread
                .observeOn(Schedulers.io()) // Observe result on IO thread
                .flatMapCompletable(movies -> Completable.fromAction(() -> {
                    movieDao.resetNowPlayingMovies();
                    for(Movie movie: movies.getMovies()) {
                        movieDao.insertNowPlayingMovie(movie);
                    }
                }));
    }

    // Bookmark a movie
    public void bookmarkMovie(int id) {
        movieDao.bookMarkMovie(id);
    }

    // Search movies using API
    public Completable searchMoviesfromApi(String query) {
        return moviesService.searchMovies(query)
                .subscribeOn(Schedulers.io()) // Run API call on background thread
                .observeOn(Schedulers.io()) // Observe result on IO thread
                .flatMapCompletable(movies -> Completable.fromAction(() -> {
                    movieDao.insertMovies(movies.getMovies());
                }));
    }

    // Fetch trending movies from the local database
    public LiveData<List<Movie>> getTrendingMovies() {
        return movieDao.getTrendingMovies();
    }

    // Fetch now playing movies from the local database
    public LiveData<List<Movie>> getNowPlayingMovies() {
        return movieDao.getNowPlayingMovies();
    }

    // Fetch bookmarked movies from the local database
    public LiveData<List<Movie>> getBookmarkedMovies() {
        return movieDao.getBookmarkedMovies();
    }

    // Search movies from the local database
    public LiveData<List<Movie>> searchMovies(String query) {
        return movieDao.searchMovies(query);
    }

}
