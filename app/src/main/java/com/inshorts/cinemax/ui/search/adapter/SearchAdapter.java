package com.inshorts.cinemax.ui.search.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.inshorts.cinemax.R;
import com.inshorts.cinemax.model.Movie;
import com.inshorts.cinemax.ui.search.SearchViewModel;

import java.util.List;


public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MovieViewHolder> {

    private final Context context;
    private final List<Movie> movies;
    private final SearchViewModel searchViewModel;

    public SearchAdapter(Context context, List<Movie> movies, SearchViewModel searchViewModel) {
        this.context = context;
        this.movies = movies;
        this.searchViewModel = searchViewModel;
        Log.d("SearchAdapter", "SearchAdapter: " + movies.size());
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_search, parent, false);
        Log.d("SearchAdapter", "Creating view holder ");
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.MovieViewHolder holder, int position) {
        Movie movie = movies.get(position);
        Log.d("SearchAdapter", "Binding view holder" );
        holder.titleTextView.setText(movie.getTitle());
        if(movie.isAdult()) holder.ratingTextView.setText("A");
        holder.movieLanguageTextView.setText(movie.getOriginalLanguage());
        holder.yearTextView.setText(movie.getReleaseDate().split("-")[0]);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

        public static class MovieViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, ratingTextView, movieLanguageTextView, yearTextView;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.movieTitle);
            ratingTextView = itemView.findViewById(R.id.movieRating);
            yearTextView = itemView.findViewById(R.id.movieYear);
            movieLanguageTextView = itemView.findViewById(R.id.movieLanguage);
        }
    }
}
