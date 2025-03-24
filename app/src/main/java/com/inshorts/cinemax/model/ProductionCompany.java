package com.inshorts.cinemax.model;

public class ProductionCompany {
    private int id;
    private String logoPath;
    private String name;
    private String originCountry;

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogoPath() {
        return logoPath;
    }

    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOriginCountry() {
        return originCountry;
    }

    public void setOriginCountry(String originCountry) {
        this.originCountry = originCountry;
    }

    // toString
    @Override
    public String toString() {
        return "ProductionCompany{" +
                "id=" + id +
                ", logoPath='" + logoPath + '\'' +
                ", name='" + name + '\'' +
                ", originCountry='" + originCountry + '\'' +
                '}';
    }
}
