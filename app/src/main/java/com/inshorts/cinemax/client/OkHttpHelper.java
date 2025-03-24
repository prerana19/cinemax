package com.inshorts.cinemax.client;

import com.inshorts.cinemax.BuildConfig;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class OkHttpHelper {
    public static OkHttpClient getClient() {
        // Logging Interceptor for debugging API calls
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Custom Header Interceptor
        Interceptor headerInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();
                String token = BuildConfig.BEARER_TOKEN;
                Request modifiedRequest = originalRequest.newBuilder()
                        .header("Authorization", "Bearer " + token)  // Replace with actual token
                        .header("Accept", "application/json")  // Accept JSON responses
                        .build();
                return chain.proceed(modifiedRequest);
            }
        };

        return new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS) // Increase connection timeout
                .readTimeout(30, TimeUnit.SECONDS)   // Increase read timeout
                .writeTimeout(30, TimeUnit.SECONDS)  // Increase write timeout
                .addInterceptor(headerInterceptor)  // Add custom headers
                .addInterceptor(loggingInterceptor)  // Add logging
                .retryOnConnectionFailure(true)  // Retry failed requests
                .build();
    }
}
