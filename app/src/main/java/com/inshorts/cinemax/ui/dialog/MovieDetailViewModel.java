package com.inshorts.cinemax.ui.dialog;

import androidx.lifecycle.ViewModel;

import com.inshorts.cinemax.model.Movie;
import com.inshorts.cinemax.repository.MoviesRepository;

import java.lang.reflect.Field;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

public class MovieDetailViewModel extends ViewModel {

    private final MoviesRepository repository;

    public MovieDetailViewModel(MoviesRepository repository) {
        this.repository = repository;
    }

    private void fillNullFields(Movie existingMovie, Movie newMovie) {
        try {
            for (Field field : Movie.class.getDeclaredFields()) {
                field.setAccessible(true);
                Object newValue = field.get(newMovie);
                Object existingValue = field.get(existingMovie);

                // Update only if existing value is null
                if (existingValue == null && newValue != null) {
                    field.set(existingMovie, newValue);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public Single<String> getMovieBackdrop(int id, String backdropPath){
        return repository.getMovieBackdrop(id, backdropPath);
    }

    public Observable<Movie> getMovieById(int movieId) {
        return repository.getMovieDetailsById(movieId);
    }
}
