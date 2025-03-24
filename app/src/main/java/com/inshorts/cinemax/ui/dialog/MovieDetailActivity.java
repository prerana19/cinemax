package com.inshorts.cinemax.ui.dialog;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.widget.ImageButton;

import androidx.annotation.LongDef;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.inshorts.cinemax.R;
import com.inshorts.cinemax.databinding.ActivityMovieDetailBinding;
import com.inshorts.cinemax.model.Genre;
import com.inshorts.cinemax.model.Movie;
import com.inshorts.cinemax.model.ProductionCompany;
import com.inshorts.cinemax.model.ProductionCountry;
import com.inshorts.cinemax.repository.MoviesRepository;
import com.inshorts.cinemax.util.ImageUtil;

import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MovieDetailActivity extends AppCompatActivity {

    private ActivityMovieDetailBinding binding;
    private MovieDetailViewModel viewModel;
    private RecyclerView genreRecyclerView;
    private int movieId;
    private final CompositeDisposable disposables = new CompositeDisposable(); // Prevent memory leaks

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up ViewBinding
        binding = ActivityMovieDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Inside your activity/fragment
        int spanCount = 4; // Adjust based on available space
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, spanCount);


        // Set up RecyclerView
        genreRecyclerView = binding.genreRecycleView;
        genreRecyclerView.setLayoutManager(gridLayoutManager);

//        genreRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get movieId from Intent
        Intent intent = getIntent();
        Uri data = intent.getData();
        Bundle extras = intent.getExtras();
        if(extras != null) {
            movieId = extras.getInt("movieId", -1);
        }
        else if (data != null) {
            movieId = Integer.parseInt(data.getQueryParameter("id"));
        }

        if (movieId == -1) {
            Log.e("MovieDetailActivity", "No movie ID found!");
            finish(); // Close activity if no movie ID
        }

        Log.d("MovieDetailActivity", "Movie ID: " + movieId);

        // Initialize ViewModel
        MoviesRepository moviesRepository = new MoviesRepository(this);
        viewModel = new ViewModelProvider(this, new MovieDetailViewModelFactory(moviesRepository))
                .get(MovieDetailViewModel.class);

        // Fetch movie details
        fetchMovieDetails();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void fetchMovieDetails() {
        disposables.add(viewModel.getMovieById(movieId)
//                .distinctUntilChanged()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
//                .firstElement()
                .subscribe(movie -> {
                    Log.d("MovieDetailActivity", "Movie: " + movie);
                    if (movie != null) {
                        setMovieDetails(movie);
                        loadMovieBackdrop(movieId, movie.getBackdropPath());
                    }
                }, throwable -> Log.e("MovieDetailActivity", "Error fetching movie details", throwable)));
    }

    private void loadMovieBackdrop(int id, String backdropPath) {
        disposables.add(viewModel.getMovieBackdrop(id, backdropPath)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(backdrop -> {
                    if (backdrop != null) {
                        Bitmap bmp = ImageUtil.loadImageFromInternalStorage(backdrop);
                        binding.movieBackdrop.setImageBitmap(bmp);
                    } else {
                        binding.movieBackdrop.setImageResource(R.drawable.movie_icon); // Fallback if missing
                    }
                    removeBackdropDisposable();
                }, throwable -> binding.movieBackdrop.setImageResource(R.drawable.movie_icon)));
    }

    private void removeBackdropDisposable() {
        if (disposables != null && !disposables.isDisposed()) {
            disposables.dispose();
        }
    }

    private void setMovieDetails(Movie movie) {
        Log.d("MovieDetailActivity", "Setting movie details: " + movie);
        ImageButton shareButton = findViewById(R.id.shareButton);
        shareButton.setOnClickListener(v -> shareMovieDeepLink(movieId));

        binding.bookmarkButton.setOnClickListener(v -> {
            viewModel.toggleBookmark(movie);

            if (movie.isBookmarked()) {
                movie.setBookmarked(false);
            } else {
                movie.setBookmarked(true);
            }
            setBookmark(movie);
        });
        Log.d("Bookmark", "Movie is bookmarked: " + movie.isBookmarked());

        setBookmark(movie);
        binding.movieTitle.setText(movie.getTitle());
        binding.overview.setText(movie.getOverview());
        binding.movieRating.setText(String.valueOf(movie.getVoteAverage()));
        binding.releaseDate.setText(movie.getReleaseDate());
        binding.language.setText(movie.getOriginalLanguage());
        binding.voteCount.setText(String.valueOf(movie.getVoteCount()));
        binding.movieCertification.setText(movie.isAdult() ? "A" : "U/A");

        setProductionCountries(movie.getProductionCountries());
        setProductionCompanies(movie.getProductionCompanies());
        setMovieRating(movie.getVoteAverage());
        setupGenreAdapter(movie.getGenres());
    }

    private void shareMovieDeepLink(int movieId) {
        String deepLink ="cinemax://movie/details?id="+ movieId; // Local Deep Link
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Check out this movie!");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this movie: " + deepLink);

        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }


    private void setBookmark(Movie movie) {
        if (movie.isBookmarked()) {
            binding.bookmarkButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_bookmarked)); // Set bookmarked icon
        } else {
            binding.bookmarkButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_bookmark)); // Set unbookmarked icon
        }
    }

    private void setupGenreAdapter(List<Genre> genres) {
        if (genres != null && !genres.isEmpty()) {
            GenreAdapter adapter = new GenreAdapter(this, genres, viewModel);
            genreRecyclerView.setAdapter(adapter);
        }
    }

    private void setProductionCompanies(List<ProductionCompany> productionCompanies) {
        if (productionCompanies != null && !productionCompanies.isEmpty()) {
            String companyNames = TextUtils.join(", ",
                    productionCompanies.stream().map(ProductionCompany::getName).collect(Collectors.toList()));
            binding.productionCompanies.setText(companyNames);
        }
        else {
            binding.productionCompanies.setVisibility(View.INVISIBLE);
            binding.productionCompaniesLabel.setVisibility(View.INVISIBLE);
        }
    }

    private void setProductionCountries(List<ProductionCountry> productionCountries) {
        if (productionCountries != null && !productionCountries.isEmpty()) {
            String countryNames = TextUtils.join(", ",
                    productionCountries.stream().map(ProductionCountry::getName).collect(Collectors.toList()));
            binding.country.setText(countryNames);
        }
        else {
            binding.country.setVisibility(View.INVISIBLE);
            binding.countryLabel.setVisibility(View.INVISIBLE);
        }
    }

    private void setMovieRating(double rating) {
        String voteAverage = new DecimalFormat("#.0").format(rating);
        binding.movieRating.setText(voteAverage);

        // Change rating color based on value
        Drawable background = binding.movieRating.getBackground().mutate();

        if (rating <= 5) {
            background.setTint(ContextCompat.getColor(this, R.color.red));
            binding.movieRating.setTextColor(ContextCompat.getColor(this, R.color.white));
        } else if (rating >= 7) {
            background.setTint(ContextCompat.getColor(this, R.color.green));
            binding.movieRating.setTextColor(ContextCompat.getColor(this, R.color.white));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.clear();
        binding = null;
    }
}
