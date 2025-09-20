package com.example.library.config;

//import java.time.temporal.ChronoUnit;

public final class AppConstants {
    private AppConstants(){}

    // default borrow period in days if not specified in settings
    public static final int DEFAULT_BORROW_PERIOD_DAYS = 14;

    // default fine per day (currency units) if not specified in settings
    public static final double DEFAULT_FINE_PER_DAY = 1.00;
}
