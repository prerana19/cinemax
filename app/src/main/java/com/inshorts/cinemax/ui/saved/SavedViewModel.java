package com.inshorts.cinemax.ui.saved;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.inshorts.cinemax.model.Movie;
import com.inshorts.cinemax.repository.MoviesRepository;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public class SavedViewModel extends ViewModel {

    private final MoviesRepository moviesRepository;

    private final LiveData<List<Movie>> savedMovies;

    public SavedViewModel(MoviesRepository moviesRepository) {
        this.moviesRepository = moviesRepository;
        this.savedMovies = LiveDataReactiveStreams.fromPublisher(  moviesRepository.getBookmarkedMovies());
    }

    public LiveData<List<Movie>> getSavedMovies() {
        return savedMovies;
    }


    public Single<String> getMoviePoster(Movie movie) {
        return moviesRepository.getMoviePoster(movie);
    }

    public void toggleBookmark(Movie movie) {
        moviesRepository.toggleBookmark(movie);
    }
}