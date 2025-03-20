package com.inshorts.cinemax.client;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import java.io.IOException;

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
                Request modifiedRequest = originalRequest.newBuilder()
                        .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIzODc3ZWYyYjZkZDhlYjQ1N2U1N2E2ODRkMWIyYjJhZSIsIm5iZiI6MTc0MjQ5MTY1OC4zMzc5OTk4LCJzdWIiOiI2N2RjNTAwYTc4OTFhNzY4MmI3ZmEwMjAiLCJzY29wZXMiOlsiYXBpX3JlYWQiXSwidmVyc2lvbiI6MX0.poa24S5lF_p4zL3bzKbyabpHFD8qcJG_G37vVwwSR7A")  // Replace with actual token
                        .header("Accept", "application/json")  // Accept JSON responses
                        .build();
                return chain.proceed(modifiedRequest);
            }
        };

        return new OkHttpClient.Builder()
                .addInterceptor(headerInterceptor)  // Add custom headers
                .addInterceptor(loggingInterceptor)  // Add logging
                .retryOnConnectionFailure(true)  // Retry failed requests
                .build();
    }
}
