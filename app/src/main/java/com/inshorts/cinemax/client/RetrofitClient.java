package com.inshorts.cinemax.client;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "https://api.themoviedb.org/3/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(OkHttpHelper.getClient()) // Use custom OkHttpClient
                    .addConverterFactory(GsonConverterFactory.create()) // JSON converter
                    .build();
        }
        return retrofit;
    }
}
