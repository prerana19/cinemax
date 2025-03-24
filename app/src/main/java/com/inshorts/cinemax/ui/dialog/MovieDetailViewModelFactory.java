package com.inshorts.cinemax.ui.dialog;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.inshorts.cinemax.repository.MoviesRepository;

public class MovieDetailViewModelFactory implements ViewModelProvider.Factory {
    private final MoviesRepository repository;

    public MovieDetailViewModelFactory(MoviesRepository repository) {
        this.repository = repository;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MovieDetailViewModel.class)) {
            return (T) new MovieDetailViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
