package com.inshorts.cinemax.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.inshorts.cinemax.client.RetrofitClient;
import com.inshorts.cinemax.databinding.FragmentHomeBinding;
import com.inshorts.cinemax.model.Movie;
import com.inshorts.cinemax.model.Movies;
import com.inshorts.cinemax.repository.MoviesRepository;
import com.inshorts.cinemax.service.MoviesService;
import com.inshorts.cinemax.util.NetworkUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private TextView textView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        textView = binding.textHome;

        MoviesRepository moviesRepository = new MoviesRepository(this.getContext());
        homeViewModel =
                new ViewModelProvider(this,new HomeViewModelFactory(moviesRepository))
                        .get(HomeViewModel.class);

        // Fetch from API if network is available
        if (NetworkUtils.isNetworkAvailable(this.getContext())) {
            System.out.println("Network is available");
            homeViewModel.fetchTrendingMoviesFromApi();
        }

        // Observe LiveData for automatic UI updates
        homeViewModel.getTrendingMovies().observe(this.getViewLifecycleOwner(),movies -> {
            {
                if (movies != null && !movies.isEmpty()) {
                    System.out.println("No movies found in database! " + movies.size());
                    System.out.println("First Movie Title: " + movies.get(0).getTitle());
                } else {
                    System.out.println("No movies found in database!");
                }
            }
            this.displayTrendingMovies(movies);
        });

        /*homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        // Get Retrofit instance and create MoviesService
        MoviesService moviesService = RetrofitClient.getClient().create(MoviesService.class);

        // Make API request to fetch all trending movies
        Call<Movies> call = moviesService.getTrendingMovies();
        call.enqueue(new Callback<Movies>() {
            @Override
            public void onResponse(Call<Movies> call, Response<Movies> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Movies movies = response.body();
                    Log.d("Retrofit", "Movies: " + movies.getMovies().get(0).getTitle());
                } else {
                    Log.e("Retrofit", "Response not successful");
                }
            }

            @Override
            public void onFailure(Call<Movies> call, Throwable t) {
                Log.e("Retrofit", "Error: " + t.getMessage());
            }
        });*/

        return root;
    }

    private void displayTrendingMovies(List<Movie> movies) {
        StringBuilder movieData = new StringBuilder();
        for (Movie movie : movies) {
            movieData.append(movie.getTitle()).append(" - ").append("\n");
        }
        System.out.println("Movies::" + movieData);
        textView.setText(movieData.toString());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}