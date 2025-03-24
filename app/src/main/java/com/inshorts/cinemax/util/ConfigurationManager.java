package com.inshorts.cinemax.util;

import com.inshorts.cinemax.model.Configuration;

public class ConfigurationManager {
    private static ConfigurationManager instance;
    private Configuration configuration;

    private ConfigurationManager() { }

    public static synchronized ConfigurationManager getInstance() {
        if (instance == null) {
            instance = new ConfigurationManager();
        }
        return instance;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public Configuration getConfiguration() {
        return configuration;
    }
}