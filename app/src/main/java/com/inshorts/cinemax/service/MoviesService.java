package com.inshorts.cinemax.service;

import com.inshorts.cinemax.model.Configuration;
import com.inshorts.cinemax.model.Movies;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MoviesService {
    @GET("trending/movie/day")
    Single<Movies> getTrendingMovies();

    @GET("movie/now_playing")
    Single<Movies> getNowPlayingMovies();

    @GET("search/movie")
    Single<Movies> searchMovies(@Query("query") String query);

    @GET("configuration")
    Single<Configuration> getConfiguration();


}
