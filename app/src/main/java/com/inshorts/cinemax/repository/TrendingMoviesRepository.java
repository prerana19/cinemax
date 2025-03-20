package com.inshorts.cinemax.repository;

import androidx.lifecycle.LiveData;

import com.inshorts.cinemax.model.Movies;

public interface TrendingMoviesRepository {
    LiveData<Movies> getTrendingMovies();
}
