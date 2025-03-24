package com.inshorts.cinemax.ui.dialog;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.inshorts.cinemax.repository.MoviesRepository;

public class MovieDialogViewModelFactory implements ViewModelProvider.Factory {
    private final MoviesRepository repository;

    public MovieDialogViewModelFactory(MoviesRepository repository) {
        this.repository = repository;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MovieDialogViewModel.class)) {
            return (T) new MovieDialogViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
