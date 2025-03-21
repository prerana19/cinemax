package com.inshorts.cinemax.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.inshorts.cinemax.model.Movie;
import com.inshorts.cinemax.repository.MoviesRepository;

import java.util.List;

public class HomeViewModel extends ViewModel {

    private final MoviesRepository moviesRepository;
    private final LiveData<List<Movie>> trendingMovies;
    private final LiveData<List<Movie>> nowPlayingMovies;
    /*private final MutableLiveData<String> mText;*/

    public HomeViewModel(MoviesRepository moviesRepository) {
        this.moviesRepository = moviesRepository;
        this.trendingMovies = moviesRepository.getTrendingMovies();
        this.nowPlayingMovies = moviesRepository.getNowPlayingMovies();

        /*mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");*/
    }

    public LiveData<List<Movie>> getTrendingMovies() {
        return trendingMovies;
    }

    public LiveData<List<Movie>> getNowPlayingMovies() {
        return nowPlayingMovies;
    }

    public void fetchTrendingMoviesFromApi() {
        moviesRepository.fetchTrendingMoviesfromApi()
                .subscribe(() -> System.out.println("API call successful"),
                        throwable -> System.out.println("API error: " + throwable.getMessage()));
    }

    public void fetchNowPlayingMoviesFromApi() {
        moviesRepository.fetchNowPlayingMoviesfromApi();
    }
}