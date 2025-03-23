package com.inshorts.cinemax.ui.home;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.inshorts.cinemax.R;
import com.inshorts.cinemax.model.Movie;
import com.inshorts.cinemax.util.ImageUtil;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class TrendingMoviesAdapter extends RecyclerView.Adapter<TrendingMoviesAdapter.TrendingViewHolder> {

    private final Context context;
    private final List<Movie> movies;
    private final HomeViewModel homeViewModel;

    public TrendingMoviesAdapter(Context context, List<Movie> movies, HomeViewModel homeViewModel) {
        this.context = context;
        this.movies = movies;
        this.homeViewModel = homeViewModel;
    }

    @NonNull
    @Override
    public TrendingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_trending_movie, parent, false);
        return new TrendingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrendingViewHolder holder, int position) {
        Movie movie = movies.get(position);
        holder.titleTextView.setText(movie.getTitle());

        // Load image from local storage
        homeViewModel.getMoviePoster(movie)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        path -> {
                            Bitmap bmp = ImageUtil.loadImageFromInternalStorage(path);
                            if (bmp != null) {
                                holder.imageView.setImageBitmap(bmp);
                            } else {
                                holder.imageView.setImageResource(R.drawable.movie_icon);
                            }
                        },
                        throwable -> holder.imageView.setImageResource(R.drawable.movie_icon)
                );
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public static class TrendingViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        ImageView imageView;

        public TrendingViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.movie_title);
            imageView = itemView.findViewById(R.id.movie_image);
        }
    }
}
