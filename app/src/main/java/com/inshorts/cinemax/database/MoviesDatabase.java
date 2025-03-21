package com.inshorts.cinemax.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.inshorts.cinemax.dao.MovieDao;
import com.inshorts.cinemax.model.Movie;
import com.inshorts.cinemax.util.Converters;

@Database(entities = {Movie.class}, version = 1)
@TypeConverters({Converters.class}) // Add Converters
public abstract class MoviesDatabase extends RoomDatabase {
    private static volatile MoviesDatabase INSTANCE;

    public abstract MovieDao movieDao();

    public static MoviesDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (MoviesDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    MoviesDatabase.class, "movie_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
