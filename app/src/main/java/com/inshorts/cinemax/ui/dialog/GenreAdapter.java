package com.inshorts.cinemax.ui.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.inshorts.cinemax.R;
import com.inshorts.cinemax.model.Genre;

import java.util.List;

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.GenreViewHolder> {
    private final MovieDetailViewModel viewModel;
    private final List<Genre> genres;
    private final Context context;

    public GenreAdapter(Context context, List<Genre> genres, MovieDetailViewModel viewModel) {
        this.context = context;
        this.genres = genres;
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public GenreAdapter.GenreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_material_tag, parent, false);
        return new GenreAdapter.GenreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GenreAdapter.GenreViewHolder holder, int position) {
        Genre genre = genres.get(position);
        holder.genreTextView.setText(genre.getName());
    }

    @Override
    public int getItemCount() {
        return genres.size();
    }

    public static class GenreViewHolder extends RecyclerView.ViewHolder {
        TextView genreTextView;

        public GenreViewHolder(@NonNull View itemView) {
            super(itemView);
            genreTextView = itemView.findViewById(R.id.tagText);
        }
    }
}
