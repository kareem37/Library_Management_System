package com.example.library.service;

public interface SettingService {
    String getSettingValue(String key, String defaultValue);
    int getSettingAsInt(String key, int defaultValue);
    double getSettingAsDouble(String key, double defaultValue);
}
