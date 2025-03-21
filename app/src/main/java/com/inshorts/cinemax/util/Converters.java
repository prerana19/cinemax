package com.inshorts.cinemax.util;

import androidx.room.TypeConverter;
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
        return data == null ? null : Arrays.stream(data.split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }
}

