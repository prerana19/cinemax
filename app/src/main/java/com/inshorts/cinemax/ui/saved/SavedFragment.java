package com.inshorts.cinemax.ui.saved;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.inshorts.cinemax.databinding.FragmentSavedBinding;
import com.inshorts.cinemax.repository.MoviesRepository;

public class SavedFragment extends Fragment {

    private FragmentSavedBinding binding;

    private SavedViewModel savedViewModel;

    private RecyclerView savedRecyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSavedBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        savedRecyclerView = binding.savedMoviesRecyclerView;
        savedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        MoviesRepository moviesRepository = new MoviesRepository(this.getContext());
        savedViewModel =
                new ViewModelProvider(this,new SavedViewModelFactory(moviesRepository))
                        .get(SavedViewModel.class);

        observeSavedMovies();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Check if MovieDialogFragment is open
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        Fragment dialogFragment = fragmentManager.findFragmentByTag("MovieDialog");

        if (dialogFragment == null) {
            observeSavedMovies();  // Observe only if the dialog is NOT open
        } else {
            Log.d("SavedFragment", "Dialog is open. Skipping LiveData updates.");
        }
    }

    private void observeSavedMovies() {
        savedViewModel.getSavedMovies()
                .observe(this.getViewLifecycleOwner(),movies -> {
                    {
                        if (movies != null && !movies.isEmpty()) {
                            savedRecyclerView.setAdapter(new SavedMoviesAdapter(getContext(),movies,savedViewModel));
                        }
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}