package com.inshorts.cinemax.ui.search;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.inshorts.cinemax.repository.MoviesRepository;
import com.inshorts.cinemax.ui.home.HomeViewModel;

public class SearchViewModelFactory  implements ViewModelProvider.Factory {
    private final MoviesRepository respository;

    public SearchViewModelFactory(MoviesRepository moviesRepository) {
        this.respository = moviesRepository;
    }

    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SearchViewModel.class)) {
            return (T) new SearchViewModel(respository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
