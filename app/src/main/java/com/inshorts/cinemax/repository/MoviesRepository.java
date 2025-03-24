package com.inshorts.cinemax.repository;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

import com.google.gson.Gson;
import com.inshorts.cinemax.client.RetrofitClient;
import com.inshorts.cinemax.dao.MovieDao;
import com.inshorts.cinemax.dao.MovieImagesDao;
import com.inshorts.cinemax.database.MoviesDatabase;
import com.inshorts.cinemax.model.Configuration;
import com.inshorts.cinemax.model.Movie;
import com.inshorts.cinemax.model.MovieImages;
import com.inshorts.cinemax.service.ImagesService;
import com.inshorts.cinemax.service.MoviesService;
import com.inshorts.cinemax.util.ConfigurationManager;
import com.inshorts.cinemax.util.ImageUtil;
import com.inshorts.cinemax.util.NetworkUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.CookieManager;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Flow;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleSource;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class MoviesRepository {
//    LiveData<Movies> getTrendingMovies();
    private final MovieDao movieDao;
    private final MoviesService moviesService;

    private final MovieImagesDao imagesDao;
    private ImagesService imagesService;

    private ConfigurationManager configurationManager;
    private Context context;
    List<String> posterSize = List.of("w92", "w154", "w185", "w342", "w500", "w780", "original");
    private List<String> backdropSize = List.of("w300", "w780", "w1280", "original");

    public MoviesRepository(Context context) {
        this.context = context;
        MoviesDatabase moviesDatabase = MoviesDatabase.getInstance(context);
        movieDao = moviesDatabase.movieDao();
        moviesService = RetrofitClient.getMoviesClient().create(MoviesService.class);
        imagesDao = moviesDatabase.movieImagesDao();
        imagesService = RetrofitClient.getImagesClient().create(ImagesService.class);
        // Access the configuration data
        configurationManager = ConfigurationManager.getInstance();

        // Fetch from API if network is available
        if (NetworkUtils.isNetworkAvailable(context)) {
            System.out.println("Network is available");
            if (configurationManager.getConfiguration() == null) {
                this.getConfiguration();
            }
            else {
               initImagesService();;
            }
        }
        else {
           initImagesService();
        }
    }

    private void initImagesService() {
        imagesService = RetrofitClient.getImagesClient(configurationManager.getConfiguration()).create(ImagesService.class);
        posterSize = configurationManager.getConfiguration().getImages().getPosterSizes();
        backdropSize = configurationManager.getConfiguration().getImages().getBackdropSizes();
    }

    public Single<Configuration> getConfiguration() {
        return moviesService.getConfiguration()
                .doOnSuccess(configuration -> {
                    ConfigurationManager.getInstance().setConfiguration(configuration);
                    this.initImagesService();
                });
    }

    // Fetch trending movies from API
    public Completable fetchTrendingMoviesfromApi() {
        return moviesService.getTrendingMovies()
                .subscribeOn(Schedulers.io()) // Run API call on background thread
                .observeOn(Schedulers.io()) // Observe result on IO thread
                .flatMapCompletable(movies -> Completable.fromRunnable(() -> {
                    System.out.println("Movies: " + movies.getMovies().get(0).toString());
                    movieDao.resetTrendingMovies()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(() -> {
                                System.out.println("Trending movies reset successfully");
                                for(Movie movie: movies.getMovies()) {
                                    this.insertTrendingMovie(movie);
                                }
                            }, throwable -> {
                                System.out.println("Failed to reset trending movies");
                            });
                }));
    }

    private void insertTrendingMovie(Movie movie) {
        movieDao.insertMovie(movie)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {
                            Log.d("DB_INSERT", "Movie inserted successfully: " + movie.toString());
                            this.updateTrendingMovie(movie);
                        }, // Success
                        throwable -> Log.e("DB_INSERT", "Failed to insert movie", throwable) // Failure
                );
    }

    private void updateTrendingMovie(Movie movie) {
        movieDao.updateTrending(movie.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> Log.d("DB_UPDATE", "Movie updated successfully: " + movie.toString()), // Success
                        throwable -> Log.e("DB_UPDATE", "Failed to update movie", throwable) // Failure
                );
    }


    // Fetch now playing movies from API

    public Completable fetchNowPlayingMoviesfromApi() {
        return moviesService.getNowPlayingMovies()
                .subscribeOn(Schedulers.io()) // Run API call on background thread
                .observeOn(Schedulers.io()) // Observe result on IO thread
                .flatMapCompletable(movies -> Completable.fromAction(() -> {
                    movieDao.resetNowPlayingMovies()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(() -> {
                                System.out.println("Now playing movies reset successfully");
                                for(Movie movie: movies.getMovies()) {
                                    this.insertNowPlayingMovie(movie);
                                }
                            }, throwable -> {
                                System.out.println("Failed to reset now playing movies");
                            });
                }));
    }

    public Completable fetchPostersFromApi() {

        return movieDao.getTrendingAndNowPlayingMovies()
                .flatMapIterable(movies -> movies)
                .flatMapSingle(movie -> getMoviePoster(movie))
                .toList()
                .ignoreElement();
    }

    public Single<String> getMoviePoster(Movie movie) {
        Log.d("MOVIE_POSTER", "getMoviePoster: " + movie.getTitle());
       return  imagesDao.getMovieImagesById(movie.getId())
               .defaultIfEmpty(new MovieImages())
               .flatMap(movieImages -> {
                   if (movieImages == null || movieImages.getPoster() == null) {
                       Log.d("MOVIE_IMAGES1", "Poster is NULL for movie ID: " + movie.getId());
                       return downloadPoster(movie);
                   } else {
                       Log.d("MOVIE_IMAGES2", "Poster path found: " + movieImages.getPoster());
                       if (imageExistsInStorage(movieImages.getPoster())) {
                           return Single.just(movieImages.getPoster());
                       } else {
                           return downloadPoster(movie);
                       }
                   }
                });
    }

    private boolean imageExistsInStorage(String path) {
        ImageUtil imageUtil = new ImageUtil();
        return imageUtil.imageExistsInInternalStorage(path);
    }

    private Single<String> downloadPoster(Movie movie) {
        String posterSize = this.posterSize.get(1);
        return imagesService.getImage(posterSize, movie.getPosterPath())
                .flatMap(responseBody -> {
                    String path = saveImageToFile(responseBody, movie.getId(),posterSize, "p");
                    this.insertPosterPath(movie.getId(), path)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(() -> {
                                Log.d("DB_INSERT", "Poster path inserted successfully: " + path);
                            }, throwable -> {
                                Log.e("DB_INSERT", "Failed to insert poster path", throwable);
                            });
                    return Single.just(path);
                })
                .doOnError(throwable ->
                        {
                            if (throwable instanceof SocketTimeoutException) {
                                Log.e("ImageService", "Network timeout! Please check your connection.");
                            } else if (throwable instanceof IOException) {
                                Log.e("ImageService", "Network error: " + throwable.getMessage());
                            } else {
                                Log.e("ImageService", "Unknown error: " + throwable.getMessage());
                            }
                        });
    }

    private Completable insertPosterPath(int movieId, String path) {
        return imagesDao.getMovieImagesById(movieId)
                .defaultIfEmpty(new MovieImages()) // Ensures a non-null MovieImages instance
                .flatMapCompletable(movieImages -> {
                    Log.d("MOVIE_IMAGES3", "movieImages id: " + movieImages.getId());

                    // Ensure the correct movieId is set
                    if (movieImages.getId() == 0) {
                        movieImages.setId(movieId);
                    }

                    movieImages.setPoster(path);
                    return imagesDao.insert(movieImages);
                });
    }

    private Completable insertBackdropPath(int movieId, String path) {
        return imagesDao.getMovieImagesById(movieId)
                .defaultIfEmpty(new MovieImages()) // Ensures a non-null MovieImages instance
                .flatMapCompletable(movieImages -> {
                    Log.d("MOVIE_IMAGES3", "movieImages id: " + movieImages.getId());
                    // Ensure the correct movieId is set
                    if (movieImages.getId() == 0) {
                        movieImages.setId(movieId);
                    }
                    movieImages.setBackdrop(path);
                    return imagesDao.insert(movieImages);
                });
    }

    private String saveImageToFile(ResponseBody responseBody, int movieId,String size,String type) {
        // Implement the logic to save the image file to app data storage
        ImageUtil imageUtil = new ImageUtil();
        //create filname as movieId + current date time
        String fileName = movieId + "_" + type +"_" + size + "_"+ new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        return imageUtil.saveImageToInternalStorage(responseBody.byteStream(), fileName, this.context);
        // and return the file path as a Single<String>
    }

    private void insertNowPlayingMovie(Movie movie) {
        movieDao.insertMovie(movie)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {
                            Log.d("DB_INSERT", "Movie inserted successfully: " + movie.toString());
                            this.updateNowPlayingMovie(movie);
                        }, // Success
                        throwable -> Log.e("DB_INSERT", "Failed to insert movie", throwable) // Failure
                );
    }

    private void updateNowPlayingMovie(Movie movie) {
        movieDao.updateNowPlaying(movie.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> Log.d("DB_UPDATE", "Movie updated successfully: " + movie.toString()), // Success
                        throwable -> Log.e("DB_UPDATE", "Failed to update movie", throwable) // Failure
                );
    }

    // Search movies using API
    public Completable searchMoviesFromApi(String query) {
        Log.d("Searching from API", "Searching for: " + query);
        return moviesService.searchMovies(query)
                .subscribeOn(Schedulers.io()) // Run API call on background thread
                .observeOn(Schedulers.io()) // Observe result on IO thread
                .flatMapCompletable(movies -> Completable.fromAction(() -> movieDao.insertMovies(movies.getMovies())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> {
                                    Log.d("DB_INSERT", "Movies inserted successfully: " + movies.getMovies().size());
                                }, // Success
                                throwable -> Log.e("DB_INSERT", "Failed to insert movie", throwable) // Failure
                        )));
    }

    // Fetch trending movies from the local database
    public Flowable<List<Movie>> getTrendingMovies() {
        return movieDao.getTrendingMovies()
                .subscribeOn(Schedulers.io())  // Run on background thread
                .observeOn(AndroidSchedulers.mainThread()); // Observe on main thread
    }

    public Flowable<List<Movie>> getNowPlayingMovies() {
        return movieDao.getNowPlayingMovies()
                .subscribeOn(Schedulers.io())  // Run on background thread
                .observeOn(AndroidSchedulers.mainThread()); // Observe on main thread
    }

    // Fetch now playing movies from the local database
//    public LiveData<List<Movie>> getNowPlayingMovies() {
//        return movieDao.getNowPlayingMovies();
//    }

    // Fetch bookmarked movies from the local database
    public Flowable<List<Movie>> getBookmarkedMovies() {
        return movieDao.getBookmarkedMovies()
                .subscribeOn(Schedulers.io())  // Run on background thread
                .observeOn(AndroidSchedulers.mainThread()); // Observe on UI thread
    }

    // Search movies from the local database
    /*public LiveData<List<Movie>> searchMovies(String query) {
        // Get initial results from local DB
        MediatorLiveData<List<Movie>> liveData = new MediatorLiveData<>();

        // Observe local database changes
        LiveData<List<Movie>> localResults = movieDao.searchMovies(query);
        liveData.addSource(localResults, liveData::setValue);

        // Fetch new data from API and update DB
        searchMoviesFromApi(query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()) // Ensure UI gets updates
                .subscribe(() -> Log.d("Search", "API results inserted"));

        return liveData; // Always return LiveData so UI updates automatically
    }*/

    public Observable<List<Movie>> searchMovies(String query) {
        return movieDao.searchMovies(query)
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(movies -> Log.d("Search", "Local DB results: " + movies.size()))
                .flatMap(localMovies ->
                        searchMoviesFromApi(query) // Force API execution before next DB query
                                .subscribeOn(Schedulers.io())
                                .andThen(movieDao.searchMovies(query).toObservable()) // Fetch updated DB results
                )
                .doOnNext(updatedMovies -> Log.d("Search", "Updated DB results: " + updatedMovies.size()))
                .doOnError(error -> Log.e("SearchError", "Error in search: " + error.getMessage())); // Handle errors
    }

    public void toggleBookmark(Movie movie) {
//        movie.setBookmarked(!movie.isBookmarked());
        movieDao.toggleBookmark(movie.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> Log.d("DB_UPDATE", "Movie bookmark toggled successfully: " + movie.toString()), // Success
                        throwable -> Log.e("DB_UPDATE", "Failed to update movie", throwable) // Failure
                );
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
            Log.d( "MovieDialogViewModel", "Existing Movie After Update: " + existingMovie.toString());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public Single<String> getMovieBackdrop(int id, String backdropPath) {
        return imagesDao.getMovieImagesById(id)
                .defaultIfEmpty(new MovieImages())
                .flatMap(movieImages -> {
                    if (movieImages == null || movieImages.getBackdrop() == null) {
                        return downloadBackdrop(id, backdropPath);
                    } else {
                        if (imageExistsInStorage(movieImages.getBackdrop())) {
                            return Single.just(movieImages.getBackdrop());
                        } else {
                            return downloadBackdrop(id, backdropPath);
                        }
                    }
                });
    }

    private Single<String> downloadBackdrop(int id, String backdropPath) {
        String backdropSize = this.backdropSize.get(0);
        return imagesService.getImage(backdropSize, backdropPath)
                .flatMap(responseBody -> {
                    String path = saveImageToFile(responseBody, id, backdropSize, "bd");
                    this.insertBackdropPath(id, path)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(() -> {
                                Log.d("DB_INSERT", "Backdrop path inserted successfully: " + path);
                            }, throwable -> {
                                Log.e("DB_INSERT", "Failed to insert backdrop path", throwable);
                            });
                    return Single.just(path);
                })
                .doOnError(throwable ->
                {
                    if (throwable instanceof SocketTimeoutException) {
                        Log.e("ImageService", "Network timeout! Please check your connection.");
                    } else if (throwable instanceof IOException) {
                        Log.e("ImageService", "Network error: " + throwable.getMessage());
                    } else {
                        Log.e("ImageService", "Unknown error: " + throwable.getMessage());
                    }
                });
    }

    public Observable<Movie> getMovieDetailsById(int id) {
        return movieDao.getMovieById(id)
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(localMovie -> {
                    Log.d("MovieDetails", "Local Movie Data: " + localMovie.toString());
                    if (localMovie != null && localMovie.getImdbId() != null) {
                        // If movie exists and has an IMDb ID, return it directly
                        return Observable.just(localMovie);
                    } else {
                        // API call should execute only once, then insert into DB and return the new movie
                        return fetchMovieDetailsFromApi(id)
                                .andThen(movieDao.getMovieById(id).toObservable())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread());
                    }
                })
                .doOnNext(movie -> Log.d("MovieDetails", "Final Movie Data: " + movie.getTitle()))
                .doOnError(error -> Log.e("MovieDetailsError", "Error fetching movie: " + error.getMessage()));
    }

    private Completable fetchMovieDetailsFromApi(int id) {
        return moviesService.getMovieDetails(id)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMapCompletable(movie ->
                        Completable.fromAction(() -> insertOrUpdate(movie)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        () -> {
                                            Log.d("DB_INSERT", "Movie inserted successfully: imdbId: " + movie.getImdbId());
                                        }, // Success
                                        throwable -> Log.e("DB_INSERT", "Failed to insert movie", throwable) // Failure
                                )))
                                .subscribeOn(Schedulers.io()) // Ensure DB operation is in background
                                .doOnComplete(() -> Log.d("DB_INSERT", "Movie inserted successfully" ))
                                .doOnError(throwable -> Log.e("DB_INSERT", "Failed to insert movie", throwable));
    }

    public Completable insertOrUpdate(Movie newMovie) {
        return movieDao.getMovieById(newMovie.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMapSingle(existingMovie -> {
                    if (existingMovie == null) {
                        // Movie doesn't exist, insert it
                        return movieDao.insertMovie(newMovie)
                                .andThen(Single.just(newMovie)); // Return inserted movie
                    } else {
                        // Update only null fields dynamically
                        fillNullFields(existingMovie, newMovie);
                        return movieDao.update(existingMovie)
                                .andThen(Single.just(existingMovie)); // Return updated movie
                    }
                }).ignoreElements(); // Convert Single<Movie> to Completable


    }


}
