package com.inshorts.cinemax.ui.dialog;

import androidx.lifecycle.ViewModel;

import com.inshorts.cinemax.model.Movie;
import com.inshorts.cinemax.repository.MoviesRepository;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

public class MovieDetailViewModel extends ViewModel {

    private final MoviesRepository repository;
    private final Map<Integer, BehaviorSubject<Movie>> cachedMovies = new HashMap<>();


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

    public void toggleBookmark(Movie movie) {
        repository.toggleBookmark(movie);
    }

    public Single<String> getMovieBackdrop(int id, String backdropPath){
        return repository.getMovieBackdrop(id, backdropPath);
    }

    /*public Observable<Movie> getMovieById(int movieId) {
        if (!cachedMovies.containsKey(movieId)) {
            cachedMovies.put(movieId, BehaviorSubject.create());
            repository.getMovieDetailsById(movieId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(movie -> cachedMovies.get(movieId).onNext(movie),
                            throwable -> cachedMovies.get(movieId).onError(throwable));
        }
        return cachedMovies.get(movieId).hide();
    }*/

    public Observable<Movie> getMovieById(int movieId) {
        return repository.getMovieDetailsById(movieId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
