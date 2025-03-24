package com.inshorts.cinemax.ui.home;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.inshorts.cinemax.model.Movie;
import com.inshorts.cinemax.repository.MoviesRepository;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HomeViewModel extends ViewModel {

    private final MoviesRepository moviesRepository;
    private final MutableLiveData<List<Movie>> trendingMovies = new MutableLiveData<>();
    private final MutableLiveData<List<Movie>> nowPlayingMovies = new MutableLiveData<>();

    private Disposable trendingDisposable;
    private Disposable nowPlayingDisposable;

    /*private final MutableLiveData<String> mText;*/

    public HomeViewModel(MoviesRepository moviesRepository) {
        this.moviesRepository = moviesRepository;
        fetchTrendingMovies();
        fetchNowPlayingMovies();
    }

    private void fetchTrendingMovies() {
        trendingDisposable = moviesRepository.getTrendingMovies()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged() // Prevents duplicate emissions
                .subscribe(movies -> {
                    if (!movies.equals(trendingMovies.getValue())) {  // Only update if different
                        trendingMovies.setValue(movies);
                    }
                }, throwable -> Log.e("HomeViewModel", "Error fetching trending movies", throwable));
    }

    private void fetchNowPlayingMovies() {
        nowPlayingDisposable = moviesRepository.getNowPlayingMovies()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged() // Prevents duplicate emissions
                .subscribe(movies -> {
                    if (!movies.equals(nowPlayingMovies.getValue())) {  // Only update if different
                        nowPlayingMovies.setValue(movies);
                    }
                }, throwable -> Log.e("HomeViewModel", "Error fetching now playing movies", throwable));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (trendingDisposable != null && !trendingDisposable.isDisposed()) {
            trendingDisposable.dispose();
        }
        if (nowPlayingDisposable != null && !nowPlayingDisposable.isDisposed()) {
            nowPlayingDisposable.dispose();
        }
    }

    public LiveData<List<Movie>> getTrendingMovies() {
        return trendingMovies;
    }

    public LiveData<List<Movie>> getNowPlayingMovies() {
        return nowPlayingMovies;
    }

    public void fetchPosters() {
        moviesRepository.fetchPostersFromApi();
    }

    public void fetchTrendingMoviesFromApi() {
        moviesRepository.fetchTrendingMoviesfromApi()
                .subscribe(() ->{
                            System.out.println("Trending API call successful");
//                            fetchPostersFromApi();
                        },
                        throwable -> System.out.println("API error: " + throwable.getMessage()));
    }

    public void fetchNowPlayingMoviesFromApi() {
        moviesRepository.fetchNowPlayingMoviesfromApi()
                .subscribe(() -> {
                            System.out.println("Now Playing API call successful");
//                            fetchPostersFromApi();
                        },
                        throwable -> System.out.println("API error: " + throwable.getMessage()));
    }

    public void fetchPostersFromApi() {
        moviesRepository.fetchPostersFromApi()
                .subscribe(() -> System.out.println("Posters API call successful"),
                        throwable -> System.out.println("API error: " + throwable.getMessage()));
    }

    public Single<String> getMoviePoster(Movie movie) {
        return moviesRepository.getMoviePoster(movie);
    }

    public void toggleBookmark(Movie movie) {
        moviesRepository.toggleBookmark(movie);
    }
}