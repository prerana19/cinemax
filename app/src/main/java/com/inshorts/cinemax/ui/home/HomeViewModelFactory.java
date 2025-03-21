package com.inshorts.cinemax.ui.home;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.inshorts.cinemax.repository.MoviesRepository;

public class HomeViewModelFactory implements ViewModelProvider.Factory {
    private final MoviesRepository repository;

    public HomeViewModelFactory(MoviesRepository repository) {
        this.repository = repository;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(HomeViewModel.class)) {
            return (T) new HomeViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
