package com.inshorts.cinemax.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.inshorts.cinemax.databinding.FragmentHomeBinding;
import com.inshorts.cinemax.model.Movie;
import com.inshorts.cinemax.repository.MoviesRepository;
import com.inshorts.cinemax.ui.home.adapter.NowPlayingAdapter;
import com.inshorts.cinemax.ui.home.adapter.TrendingMoviesAdapter;
import com.inshorts.cinemax.util.NetworkUtils;

import java.util.List;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private RecyclerView trendingRecyclerView;
    private RecyclerView nowPlayingRecyclerView;

    private CompositeDisposable disposables = new CompositeDisposable(); // Manages RxJava subscriptions


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        trendingRecyclerView = binding.trendingRecyclerView;
        nowPlayingRecyclerView = binding.nowPlayingRecyclerView;

        LinearLayoutManager trendingLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        trendingRecyclerView.setLayoutManager(trendingLayoutManager);
        nowPlayingRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

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

        TrendingMoviesAdapter adapter = new TrendingMoviesAdapter(getContext(), movies, homeViewModel);
        trendingRecyclerView.setAdapter(adapter);
    }

    private void displayNowPlayingMovies(List<Movie> movies) {
        if (movies == null || movies.isEmpty()) {
            Log.d("HomeFragment", "No now playing movies found.");
            return;
        }

        NowPlayingAdapter adapter = new NowPlayingAdapter(getContext(), movies, homeViewModel);
        nowPlayingRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}