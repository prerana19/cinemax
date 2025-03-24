package com.inshorts.cinemax.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@Entity(tableName = "movies")
public class Movie {
    @SerializedName("backdrop_path")
    @ColumnInfo(name = "backdrop_path")
    private String backdropPath;

    @PrimaryKey
    private int id;

    @SerializedName("title")
    @ColumnInfo(name = "title")
    private String title;

    @SerializedName("original_title")
    @ColumnInfo(name = "original_title")
    private String originalTitle;

    @SerializedName("overview")
    @ColumnInfo(name = "overview")
    private String overview;

    @SerializedName("poster_path")
    @ColumnInfo(name = "poster_path")
    private String posterPath;

    @SerializedName("media_type")
    @ColumnInfo(name = "media_type")
    private String mediaType;

    @SerializedName("adult")
    @ColumnInfo(name = "adult")
    private boolean adult;

    @SerializedName("original_language")
    @ColumnInfo(name = "original_language")
    private String originalLanguage;

    @SerializedName("genre_ids")
    @ColumnInfo(name = "genre_ids")
    private List<Integer> genreIds;

    @SerializedName("popularity")
    @ColumnInfo(name = "popularity")
    private double popularity;

    @SerializedName("release_date")
    @ColumnInfo(name = "release_date")
    private String releaseDate;

    @SerializedName("video")
    @ColumnInfo(name = "video")
    private boolean video;

    @SerializedName("vote_average")
    @ColumnInfo(name = "vote_average")
    private double voteAverage;

    @SerializedName("vote_count")
    @ColumnInfo(name = "vote_count")
    private int voteCount;

    //Movie Details Fields
    @SerializedName("runtime")
    @ColumnInfo(name = "runtime")
    private int runtime;

    @SerializedName("status")
    @ColumnInfo(name = "status")
    private String status;

    @SerializedName("tagline")
    @ColumnInfo(name = "tagline")
    private String tagline;

    @SerializedName("budget")
    @ColumnInfo(name = "budget")
    private int budget;

    @SerializedName("revenue")
    @ColumnInfo(name = "revenue")
    private int revenue;

    @SerializedName("homepage")
    @ColumnInfo(name = "homepage")
    private String homepage;

    @SerializedName("imdb_id")
    @ColumnInfo(name = "imdb_id")
    private String imdbId;

    @SerializedName("production_companies")
    @ColumnInfo(name = "production_companies")
    private List<ProductionCompany> productionCompanies;

    @SerializedName("production_countries")
    @ColumnInfo(name = "production_countries")
    private List<ProductionCountry> productionCountries;

    @SerializedName("spoken_languages")
    @ColumnInfo(name = "spoken_languages")
    private List<SpokenLanguage> spokenLanguages;

    @SerializedName("genres")
    @ColumnInfo(name = "genres")
    private List<Genre> genres;

    // Custom fields (not part of API response)
    @ColumnInfo(name = "trending", defaultValue = "0")
    private boolean trending;

    @ColumnInfo(name = "now_playing", defaultValue = "0")
    private boolean nowPlaying;

    @ColumnInfo(name = "bookmarked", defaultValue = "0")
    private boolean bookmarked;

    // Getters and Setters
    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public List<Integer> getGenreIds() {
        return genreIds;
    }

    public void setGenreIds(List<Integer> genreIds) {
        this.genreIds = genreIds;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public boolean isVideo() {
        return video;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public boolean isTrending() {
        return trending;
    }

    public void setTrending(boolean trending) {
        this.trending = trending;
    }

    public boolean isNowPlaying() {
        return nowPlaying;
    }

    public void setNowPlaying(boolean nowPlaying) {
        this.nowPlaying = nowPlaying;
    }

    public boolean isBookmarked() {
        return bookmarked;
    }

    public void setBookmarked(boolean bookmarked) {
        this.bookmarked = bookmarked;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public int getBudget() {
        return budget;
    }

    public void setBudget(int budget) {
        this.budget = budget;
    }

    public int getRevenue() {
        return revenue;
    }

    public void setRevenue(int revenue) {
        this.revenue = revenue;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public String getImdbId() {
        return imdbId;
    }

    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }

    public List<ProductionCompany> getProductionCompanies() {
        return productionCompanies;
    }

    public void setProductionCompanies(List<ProductionCompany> productionCompanies) {
        this.productionCompanies = productionCompanies;
    }

    public List<ProductionCountry> getProductionCountries() {
        return productionCountries;
    }

    public void setProductionCountries(List<ProductionCountry> productionCountries) {
        this.productionCountries = productionCountries;
    }

    public List<SpokenLanguage> getSpokenLanguages() {
        return spokenLanguages;
    }

    public void setSpokenLanguages(List<SpokenLanguage> spokenLanguages) {
        this.spokenLanguages = spokenLanguages;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }


    // Override toString method
    @Override
    public String toString() {
        return "Movie{" +
                "backdropPath='" + backdropPath + '\'' +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", originalTitle='" + originalTitle + '\'' +
                ", overview='" + overview + '\'' +
                ", posterPath='" + posterPath + '\'' +
                ", mediaType='" + mediaType + '\'' +
                ", adult=" + adult +
                ", originalLanguage='" + originalLanguage + '\'' +
                ", genreIds=" + genreIds +
                ", popularity=" + popularity +
                ", releaseDate='" + releaseDate + '\'' +
                ", video=" + video +
                ", voteAverage=" + voteAverage +
                ", voteCount=" + voteCount +
                ", trending=" + trending +
                ", nowPlaying=" + nowPlaying +
                ", bookmarked=" + bookmarked +
                ", runtime=" + runtime +
                ", status='" + status + '\'' +
                ", tagline='" + tagline + '\'' +
                ", budget=" + budget +
                ", revenue=" + revenue +
                ", homepage='" + homepage + '\'' +
                ", imdbId='" + imdbId + '\'' +
                ", productionCompanies=" + productionCompanies +
                ", productionCountries=" + productionCountries +
                ", spokenLanguages=" + spokenLanguages +
                ", genres=" + genres +
                '}';
    }
}
