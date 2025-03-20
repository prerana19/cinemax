package com.inshorts.cinemax.service;

import com.inshorts.cinemax.model.Movies;

import retrofit2.Call;
import retrofit2.http.GET;

public interface MoviesService {
    @GET("trending/movie/day")  // Replace with your API endpoint
    Call<Movies> getTrendingMovies();

    @GET("movie/now_playing")  // Replace with your API endpoint
    Call<Movies> getNowPlayingMovies();
}
