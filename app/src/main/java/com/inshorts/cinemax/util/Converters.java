package com.inshorts.cinemax.util;

import androidx.room.TypeConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Converters {
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
}

