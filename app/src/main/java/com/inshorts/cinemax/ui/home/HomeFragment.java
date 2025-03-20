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
import com.inshorts.cinemax.model.Movies;
import com.inshorts.cinemax.service.MoviesService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

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
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}