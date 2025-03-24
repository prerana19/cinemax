package com.inshorts.cinemax.client;

import com.inshorts.cinemax.model.Configuration;

import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "https://api.themoviedb.org/3/";
    private static String IMAGES_BASE_URL = "https://image.tmdb.org/t/p/";
    private static Retrofit retrofitMoviesClient = null;
    private static Retrofit retrofitImagesClient = null;

    public static Retrofit getMoviesClient() {
        if (retrofitMoviesClient == null) {
            retrofitMoviesClient = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(OkHttpHelper.getClient()) // Use custom OkHttpClient
                    .addConverterFactory(GsonConverterFactory.create()) // JSON converter
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.createWithScheduler(Schedulers.io()))
                    .build();
        }
        return retrofitMoviesClient;
    }
    
    public static Retrofit getImagesClient() {
        if (retrofitImagesClient == null) {
            retrofitImagesClient = new Retrofit.Builder()
                    .baseUrl(IMAGES_BASE_URL)
                    .client(OkHttpHelper.getClient()) // Use custom OkHttpClient
                    .addConverterFactory(GsonConverterFactory.create()) // JSON converter
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.createWithScheduler(Schedulers.io()))
                    .build();
        }
        return retrofitImagesClient;
    }

    public static Retrofit getImagesClient(Configuration configuration) {
        if (retrofitImagesClient == null) {
            if(configuration != null) {
                IMAGES_BASE_URL = configuration.getImages().getSecureBaseUrl();
            }
        }
        return getImagesClient();
    }
}
