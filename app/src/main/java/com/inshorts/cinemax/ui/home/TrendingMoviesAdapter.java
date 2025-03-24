package com.inshorts.cinemax.ui.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.inshorts.cinemax.R;
import com.inshorts.cinemax.model.Movie;
import com.inshorts.cinemax.ui.dialog.MovieDetailActivity;
import com.inshorts.cinemax.ui.saved.SavedMoviesAdapter;
import com.inshorts.cinemax.util.ImageUtil;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
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

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), MovieDetailActivity.class);
            intent.putExtra("movieId", movie.getId());
            v.getContext().startActivity(intent);
        });

        observeLiveData(movie,holder);

//    Load image from local storage
//        homeViewModel.getMoviePoster(movie)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new SingleObserver<String>() {
//                    private Disposable disposable;
//
//                    @Override
//                    public void onSubscribe(@NonNull Disposable d) {
//                        disposable = d;
//                    }
//
//                    @Override
//                    public void onSuccess(@NonNull String path) {
//                        if (holder.getAdapterPosition() == position) { // Ensure it's the right ViewHolder
//                            Bitmap bmp = ImageUtil.loadImageFromInternalStorage(path);
//                            if (bmp != null) {
//                                holder.imageView.setImageBitmap(bmp);
//                            } else {
//                                holder.imageView.setImageResource(R.drawable.movie_icon);
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onError(@NonNull Throwable e) {
//                        holder.imageView.setImageResource(R.drawable.movie_icon);
//                    }
//                });
        // Load image from local storage
//        homeViewModel.getMoviePoster(movie)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(
//                        path -> {
//                            Bitmap bmp = ImageUtil.loadImageFromInternalStorage(path);
//                            if (bmp != null) {
//                                holder.imageView.setImageBitmap(bmp);
//                            } else {
//                                holder.imageView.setImageResource(R.drawable.movie_icon);
//                            }
//                        },
//                        throwable -> holder.imageView.setImageResource(R.drawable.movie_icon)
//                );
    }

    private Disposable  observeLiveData(Movie movie, TrendingViewHolder holder) {
        return homeViewModel.getMoviePoster(movie)
                .subscribeOn(Schedulers.io())  // Load on background thread
                .observeOn(AndroidSchedulers.mainThread())  // Update UI on main thread
                .subscribe(
                        path -> {
                            Bitmap bmp = ImageUtil.loadImageFromInternalStorage(path);
                            if (bmp != null) {
                                holder.imageView.setImageBitmap(bmp);
                            } else {
                                holder.imageView.setImageResource(R.drawable.movie_icon);
                            }
                        },
                        throwable -> {
                            holder.imageView.setImageResource(R.drawable.movie_icon); // Show error image on failure
                        }
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
