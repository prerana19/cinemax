package com.inshorts.cinemax.model;

public class ProductionCountry {
    private String iso31661;
    private String name;

    // Getters and Setters
    public String getIso31661() {
        return iso31661;
    }

    public void setIso31661(String iso31661) {
        this.iso31661 = iso31661;
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
        return "ProductionCountry{" +
                "iso31661='" + iso31661 + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
