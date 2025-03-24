package com.inshorts.cinemax.ui.saved;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.inshorts.cinemax.repository.MoviesRepository;
import com.inshorts.cinemax.ui.home.HomeViewModel;

public class SavedViewModelFactory implements ViewModelProvider.Factory {

    private final MoviesRepository repository;
    public SavedViewModelFactory(MoviesRepository moviesRepository) {
        this.repository = moviesRepository;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SavedViewModel.class)) {
            return (T) new SavedViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
