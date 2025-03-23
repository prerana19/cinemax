package com.inshorts.cinemax.ui.saved;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.inshorts.cinemax.R;
import com.inshorts.cinemax.model.Movie;
import com.inshorts.cinemax.ui.home.HomeViewModel;
import com.inshorts.cinemax.ui.home.NowPlayingAdapter;
import com.inshorts.cinemax.util.ImageUtil;

import java.text.DecimalFormat;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SavedMoviesAdapter extends RecyclerView.Adapter<SavedMoviesAdapter.MovieViewHolder> {

    private final Context context;
    private final List<Movie> movies;
    private final SavedViewModel savedViewModel;
    public SavedMoviesAdapter(Context context, List<Movie> movies, SavedViewModel savedViewModel) {
        this.context = context;
        this.movies = movies;
        this.savedViewModel = savedViewModel;
    }

    @NonNull
    @Override
    public SavedMoviesAdapter.MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_movie_detailed, parent, false);
        return new SavedMoviesAdapter.MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SavedMoviesAdapter.MovieViewHolder holder, int position) {
        Movie movie = movies.get(position);

        holder.titleTextView.setText(movie.getTitle());
        String voteAverage = new DecimalFormat("#.0").format(movie.getVoteAverage());
        holder.averageVotesTextView.setText(voteAverage);

        // change color of averageVotesTextView based on value
        Drawable background = holder.averageVotesTextView.getBackground().mutate(); // Ensure independent drawable instance

        if (Double.valueOf(voteAverage) <= 5) {
            background.setTint(ContextCompat.getColor(context, R.color.red));
            holder.averageVotesTextView.setTextColor(ContextCompat.getColor(context, R.color.white));
        } else if (Double.valueOf(voteAverage)  >= 7) {
            background.setTint(ContextCompat.getColor(context, R.color.green));
            holder.averageVotesTextView.setTextColor(ContextCompat.getColor(context, R.color.white));
        }
        holder.voteCountTextView.setText(String.valueOf(movie.getVoteCount()));
        holder.movieLanguageTextView.setText(movie.getOriginalLanguage());

        holder.bookmarkButton.setOnClickListener(v -> {
            savedViewModel.toggleBookmark(movie);
        });

        if (movie.isBookmarked()) {
            holder.bookmarkButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_bookmarked)); // Set bookmarked icon
        } else {
            holder.bookmarkButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_bookmark)); // Set unbookmarked icon
        }

        if(movie.isAdult()) holder.ratingTextView.setText("A");

        // Load image from local storage using ViewModel
        savedViewModel.getMoviePoster(movie)
                .subscribeOn(Schedulers.io())  // Load on background thread
                .observeOn(AndroidSchedulers.mainThread())  // Update UI on main thread
                .subscribe(
                        path -> {
                            Bitmap bmp = ImageUtil.loadImageFromInternalStorage(path);
                            if (bmp != null) {
                                Bitmap resizedBmp = Bitmap.createScaledBitmap(bmp, 80, 80, true);

                                Log.d("ImageDebug", "Bitmap width: " + bmp.getWidth() + ", height: " + bmp.getHeight());

                                holder.imageView.setImageBitmap(resizedBmp);
                            } else {
                                holder.imageView.setImageResource(R.drawable.movie_icon); // Fallback if missing
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

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, ratingTextView, averageVotesTextView, voteCountTextView, movieLanguageTextView;
        ImageView imageView;

        ImageButton bookmarkButton;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.movieTitle);
            imageView = itemView.findViewById(R.id.moviePoster);
            ratingTextView = itemView.findViewById(R.id.movieRating);
            averageVotesTextView = itemView.findViewById(R.id.averageVotes);
            voteCountTextView = itemView.findViewById(R.id.voteCount);
            movieLanguageTextView = itemView.findViewById(R.id.movieLanguages);
            bookmarkButton = itemView.findViewById(R.id.bookmarkIcon);
        }
    }


}
