package com.inshorts.cinemax.model;

public class SpokenLanguage {
    private String englishName;
    private String iso6391;
    private String name;

    // Getters and Setters
    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public String getIso6391() {
        return iso6391;
    }

    public void setIso6391(String iso6391) {
        this.iso6391 = iso6391;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // toString
    @Override
    public String toString() {
        return "SpokenLanguage{" +
                "englishName='" + englishName + '\'' +
                ", iso6391='" + iso6391 + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
