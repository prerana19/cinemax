package com.inshorts.cinemax.model;

import java.util.List;

public class Movies {
    private int page;
    private List<Movie> results;
    private int totalPages;
    private int totalResults;

    // Getters and Setters
    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public List<Movie> getMovies() {
        return results;
    }

    public void setMovies(List<Movie> movies) {
        this.results = movies;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    // toString
    @Override
    public String toString() {
        return "Movies{" +
                "page=" + page +
                ", movies=" + results.toString() +
                ", totalPages=" + totalPages +
                ", totalResults=" + totalResults +
                '}';
    }
}