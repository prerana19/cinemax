package com.inshorts.cinemax.service;

import com.inshorts.cinemax.model.Movies;

import io.reactivex.rxjava3.core.Single;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ImagesService {

    @GET("{image_size}/{file_path}")
    Single<ResponseBody> getImage(@Path("image_size") String imageSize, @Path("file_path") String filePath);

}
