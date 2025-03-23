package com.inshorts.cinemax.ui.search;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.inshorts.cinemax.R;
import com.inshorts.cinemax.model.Movie;
import com.inshorts.cinemax.repository.MoviesRepository;
import com.inshorts.cinemax.ui.search.adapter.SearchAdapter;

import java.util.List;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SearchFragment extends Fragment {

    private EditText searchEditText;
    private RecyclerView searchRecyclerView;
    private LinearLayout searchContainer;

    private SearchViewModel searchViewModel;

    private Runnable workRunnable;

    private Handler handler = new Handler(Looper.getMainLooper());

    private Disposable searchDisposable;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        MoviesRepository moviesRepository = new MoviesRepository(this.getContext());
        searchViewModel =
                new ViewModelProvider(this, new SearchViewModelFactory(moviesRepository))
                        .get(SearchViewModel.class);
        searchEditText = view.findViewById(R.id.searchEditText);
        searchRecyclerView = view.findViewById(R.id.recyclerView);
        searchContainer = view.findViewById(R.id.searchContainer);

        searchEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                Log.d("SearchText", "Search triggered");
                moveSearchToTop();
            }
        });

        searchEditText.setOnClickListener(v -> moveSearchToTop());

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (workRunnable != null) {
                    handler.removeCallbacks(workRunnable); // Cancel previous task
                }

                workRunnable = new Runnable() {
                    @Override
                    public void run() {
                        performSearch(s.toString()); // Call search function after delay
                    }
                };

                handler.postDelayed(workRunnable, 2000); // 2 seconds delay
            }
        });

        return view;
    }

    private void performSearch(String query) {
        Log.d("Search", "Searching for: " + query);

        if (query.length() != 0) {
            // Dispose previous search request if exists
            if (searchDisposable != null && !searchDisposable.isDisposed()) {
                searchDisposable.dispose();
            }

            searchDisposable = searchViewModel.getSearchResults(query)
                    .observeOn(Schedulers.io())
                    .subscribeOn(Schedulers.io())
                    .subscribe(movies -> {
                        Log.d("Search", "Search results: " + movies);
                        Log.d("Search", "Found  " + movies.size() + " movies");
                        displaySearchResults(movies);
                    },
                            throwable -> Log.e("SearchError", "Error fetching search results", throwable) // Handle errors
                    );
        }
    }

    private void displaySearchResults(List<Movie> movies) {
        if (movies == null || movies.isEmpty()) {
            Log.d("Search Listener", "No movies found for search.");
            return;
        }

        requireActivity().runOnUiThread(() -> {
            SearchAdapter adapter = new SearchAdapter(getContext(), movies, searchViewModel);
            searchRecyclerView.setAdapter(adapter);
        });
    }

    private void moveSearchToTop() {
        TransitionManager.beginDelayedTransition((ViewGroup) searchContainer.getParent());

        Log.d("SearchText", "Moving Search triggered");
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) searchContainer.getLayoutParams();
        params.topToBottom = ConstraintLayout.LayoutParams.UNSET;
        params.bottomToBottom = ConstraintLayout.LayoutParams.UNSET;
        params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        params.verticalBias = ConstraintLayout.LayoutParams.UNSET;
        params.topMargin = 20;
        searchContainer.setLayoutParams(params);
        searchContainer.requestLayout(); // Force layout update
        searchRecyclerView.setVisibility(View.VISIBLE);
    }
}
