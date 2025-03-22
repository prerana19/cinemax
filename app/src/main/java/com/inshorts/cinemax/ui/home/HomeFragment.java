package com.inshorts.cinemax.ui.home;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.inshorts.cinemax.R;
import com.inshorts.cinemax.databinding.FragmentHomeBinding;
import com.inshorts.cinemax.model.Movie;
import com.inshorts.cinemax.repository.MoviesRepository;
import com.inshorts.cinemax.util.ImageUtil;
import com.inshorts.cinemax.util.NetworkUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private LinearLayout trendingMoviesLayout;
    private LinearLayout nowPlayingMoviesLayout;

    private CompositeDisposable disposables = new CompositeDisposable(); // Manages RxJava subscriptions


    private final String posterSize = "w92";


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        trendingMoviesLayout = binding.trendingLayout;
        nowPlayingMoviesLayout = binding.nowPlayingLayout;

        MoviesRepository moviesRepository = new MoviesRepository(this.getContext());
        homeViewModel =
                new ViewModelProvider(this,new HomeViewModelFactory(moviesRepository))
                        .get(HomeViewModel.class);


        // Fetch from API if network is available
        if (NetworkUtils.isNetworkAvailable(this.getContext())) {
            System.out.println("Network is available");
            homeViewModel.fetchTrendingMoviesFromApi();
            homeViewModel.fetchNowPlayingMoviesFromApi();
        }

        // Observe LiveData for automatic UI updates
        homeViewModel.getTrendingMovies().observe(this.getViewLifecycleOwner(),movies -> {
            {
                if (movies != null && !movies.isEmpty()) {
                    System.out.println("Movies found in database! " + movies.size());
                    System.out.println("First Movie Title: " + movies.get(0).getTitle());
                    homeViewModel.fetchPostersFromApi();
                } else {
                    System.out.println("No movies found in database!");
                }
                this.displayTrendingMovies(movies);
            }

        });

        homeViewModel.getNowPlayingMovies().observe(this.getViewLifecycleOwner(),movies -> {
            {
                if (movies != null && !movies.isEmpty()) {
                    System.out.println("No movies found in database! " + movies.size());
                    System.out.println("First Movie Title: " + movies.get(0).getTitle());
                } else {
                    System.out.println("No movies found in database!");
                }
                this.displayNowPlayingMovies(movies);
            }
        });

        return root;
    }

    private void displayTrendingMovies(List<Movie> movies) {
        if (movies == null || movies.isEmpty()) {
            Log.d("HomeFragment", "No trending movies found.");
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(getContext());

        // Track already displayed movies to avoid duplicates
        Set<Integer> displayedMovieIds = new HashSet<>();
        for (int i = 0; i < trendingMoviesLayout.getChildCount(); i++) {
            View child = trendingMoviesLayout.getChildAt(i);
            TextView titleTextView = child.findViewById(R.id.movie_title);
            if (titleTextView != null) {
                String title = titleTextView.getText().toString();
                for (Movie movie : movies) {
                    if (movie.getTitle().equals(title)) {
                        displayedMovieIds.add(movie.getId());
                        break;
                    }
                }
            }
        }

        for (Movie movie : movies) {
            if (displayedMovieIds.contains(movie.getId())) {
                continue; // Skip already displayed movies
            }

            homeViewModel.getMoviePoster(movie)
                    .subscribeOn(Schedulers.io()) // Perform network operation on IO thread
                    .observeOn(AndroidSchedulers.mainThread()) // Update UI on main thread
                    .subscribe(path -> {
                        Bitmap bmp = ImageUtil.loadImageFromInternalStorage(path);
                        View cardView = inflater.inflate(R.layout.card_trending_movie, trendingMoviesLayout, false);
                        TextView titleTextView = cardView.findViewById(R.id.movie_title);
                        ImageView imageView = cardView.findViewById(R.id.movie_image);
                        titleTextView.setText(movie.getTitle());
                        imageView.setImageBitmap(bmp);

                        trendingMoviesLayout.addView(cardView);
                    }, throwable -> Log.e("HomeFragment", "Error loading image: " + throwable.getMessage()));
        }
    }

    /*private Bitmap getMoviePoster(Movie movie) {
        return ImageUtil.loadImageFromInternalStorage(homeViewModel.getMoviePoster(movie), getContext());
    }*/

    private void displayNowPlayingMovies(List<Movie> movies) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}