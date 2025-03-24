package com.inshorts.cinemax.ui.search;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.inshorts.cinemax.model.Movie;
import com.inshorts.cinemax.model.Movies;
import com.inshorts.cinemax.repository.MoviesRepository;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

public class SearchViewModel extends ViewModel {

    private final MoviesRepository moviesRepository;

    public SearchViewModel(MoviesRepository moviesRepository) {
       this.moviesRepository = moviesRepository;
    }

    public Observable<List<Movie>> getSearchResults(String query) {
        return moviesRepository.searchMovies(query);
    }

}