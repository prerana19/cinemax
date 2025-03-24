package com.inshorts.cinemax.util;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.inshorts.cinemax.model.Genre;
import com.inshorts.cinemax.model.ProductionCompany;
import com.inshorts.cinemax.model.ProductionCountry;
import com.inshorts.cinemax.model.SpokenLanguage;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Converters {

    private static final Gson gson = new Gson();

    @TypeConverter
    public static String fromIntegerList(List<Integer> list) {
        return list == null ? null : list.stream().map(String::valueOf).collect(Collectors.joining(","));
    }

    @TypeConverter
    public static List<Integer> toIntegerList(String data) {
        if (data == null || data.isEmpty()) {
            return new ArrayList<>(); // Return empty list instead of throwing an error
        }

        return Arrays.stream(data.split(","))
                .filter(s -> !s.isEmpty()) // Skip empty values
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }


    @TypeConverter
    public static String fromProductionCompanies(List<ProductionCompany> companies) {
        return companies == null ? null : gson.toJson(companies);
    }

    @TypeConverter
    public static List<ProductionCompany> toProductionCompanies(String json) {
        if (json == null) return null;
        Type type = new TypeToken <List<ProductionCompany>>() {}.getType();
        return gson.fromJson(json, type);
    }

    @TypeConverter
    public static String fromProductionCountries(List<ProductionCountry> countries) {
        return countries == null ? null : gson.toJson(countries);
    }

    @TypeConverter
    public static List<ProductionCountry> toProductionCountries(String json) {
        if (json == null) return null;
        Type type = new TypeToken<List<ProductionCountry>>() {}.getType();
        return gson.fromJson(json, type);
    }

    @TypeConverter
    public static String fromSpokenLanguages(List<SpokenLanguage> languages) {
        return languages == null ? null : gson.toJson(languages);
    }

    @TypeConverter
    public static List<SpokenLanguage> toSpokenLanguages(String json) {
        if (json == null) return null;
        Type type = new TypeToken<List<SpokenLanguage>>() {}.getType();
        return gson.fromJson(json, type);
    }

    @TypeConverter
    public static String fromGenres(List<Genre> genres) {
        return genres == null ? null : gson.toJson(genres);
    }

    @TypeConverter
    public static List<Genre> toGenres(String json) {
        if (json == null) return null;
        Type type = new TypeToken<List<Genre>>() {}.getType();
        return gson.fromJson(json, type);
    }
}

