package com.inshorts.cinemax.ui.dialog;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.inshorts.cinemax.R;
import com.inshorts.cinemax.databinding.FragmentMovieDialogBinding;
import com.inshorts.cinemax.model.Genre;
import com.inshorts.cinemax.model.Movie;
import com.inshorts.cinemax.model.ProductionCompany;
import com.inshorts.cinemax.model.ProductionCountry;
import com.inshorts.cinemax.repository.MoviesRepository;
import com.inshorts.cinemax.util.ImageUtil;

import java.net.URI;
import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MovieDialogFragment extends DialogFragment {

    private FragmentMovieDialogBinding binding;
    private RecyclerView genreRecyclerView;
    private MovieDialogViewModel viewModel;
    private int movieId;
    private Disposable disposable; // Prevent memory leaks
    private Disposable backdropDisposable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentMovieDialogBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        genreRecyclerView = binding.genreRecycleView;
        genreRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        MoviesRepository moviesRepository = new MoviesRepository(this.getContext());

        if (getArguments() != null) {
            movieId = getArguments().getInt("movieId");
            Log.d("MovieDialogFragment", "Movie ID: " + movieId);
        }

        viewModel = new ViewModelProvider(this, new MovieDialogViewModelFactory(moviesRepository))
                .get(MovieDialogViewModel.class);

        fetchMovieDetails();

/*        viewModel.getMovieById(movieId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(movie -> {
                    if (movie != null) {
                        getMovieBackdrop(movieId, movie.getBackdropPath());
                        setMovieDetails(movie);
                    }
                });*/

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Make it full-screen
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    private void fetchMovieDetails() {
        disposable = viewModel.getMovieById(movieId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(movie -> {
                    Log.d("MovieDialogFragment", "Movie: " + movie.toString());
                    if (movie != null) {
                        setMovieDetails(movie);
                        getMovieBackdrop(movieId,movie.getBackdropPath());
                        closeMovieSubscription();
                    }
                }, throwable -> Log.e("MovieDialogFragment", "Error fetching movie details", throwable));
    }

    private void closeMovieSubscription() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }


    private void getMovieBackdrop(int id, String backdropPath) {
        backdropDisposable = viewModel.getMovieBackdrop(id, backdropPath)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(backdrop -> {
                    if (backdrop != null) {
                        Bitmap bmp = ImageUtil.loadImageFromInternalStorage(backdrop);
                        binding.movieBackdrop.setImageBitmap(bmp);
                        disposeBackdropSubscription();
                    } else {
                        binding.movieBackdrop.setImageResource(R.drawable.movie_icon); // Fallback if missing
                    }
                        },
                        throwable -> {
                            binding.movieBackdrop.setImageResource(R.drawable.movie_icon); // Show error image on failure
                        });
    }

    private void disposeBackdropSubscription() {
        if (backdropDisposable != null && !backdropDisposable.isDisposed()) {
            backdropDisposable.dispose();
        }
    }

    private void setMovieDetails(Movie movie) {
        Log.d("MovieDialogFragment", "Setting movie details: " + movie.toString());
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

    private void setupGenreAdapter(List<Genre> genres) {
        if (genres != null && !genres.isEmpty()) {
            GenreAdapter adapter = new GenreAdapter(getContext(), genres, viewModel);
            genreRecyclerView.setAdapter(adapter);
        }
    }

    private void setProductionCompanies(List<ProductionCompany> productionCompanies) {
        if (productionCompanies != null && !productionCompanies.isEmpty()) {
            String companyNames = TextUtils.join(", ",
                    productionCompanies.stream().map(ProductionCompany::getName).collect(Collectors.toList()));
            binding.productionCompanies.setText(companyNames);
        }
    }

    private void setProductionCountries(List<ProductionCountry> productionCountries) {
        if (productionCountries != null && !productionCountries.isEmpty()) {
            String countryNames = TextUtils.join(", ",
                    productionCountries.stream().map(ProductionCountry::getName).collect(Collectors.toList()));
            binding.country.setText(countryNames);
        }
    }


    private void setMovieRating(double rating) {
        String voteAverage = new DecimalFormat("#.0").format(rating);
        binding.movieRating.setText(voteAverage);
        // change color of averageVotesTextView based on value
        Drawable background = binding.movieRating.getBackground().mutate(); // Ensure independent drawable instance

        if (Double.valueOf(rating) <= 5) {
            background.setTint(ContextCompat.getColor(getContext(), R.color.red));
            binding.movieRating.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        } else if (Double.valueOf(voteAverage)  >= 7) {
            background.setTint(ContextCompat.getColor(getContext(), R.color.green));
            binding.movieRating.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        binding = null;
    }
}