package com.example.library.service.impl;

import com.example.library.model.Setting;
import com.example.library.repository.SettingRepository;
import com.example.library.service.SettingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SettingServiceImpl implements SettingService {

    private final SettingRepository repository;

    public SettingServiceImpl(SettingRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public String getSettingValue(String key, String defaultValue) {
        return repository.findByKey(key).map(Setting::getValue).orElse(defaultValue);
    }

    @Override
    @Transactional(readOnly = true)
    public int getSettingAsInt(String key, int defaultValue) {
        String val = getSettingValue(key, null);
        if (val == null) return defaultValue;
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public double getSettingAsDouble(String key, double defaultValue) {
        String val = getSettingValue(key, null);
        if (val == null) return defaultValue;
        try {
            return Double.parseDouble(val);
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }
}
