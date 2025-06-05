package com.example.unipiaudiostoriesapp.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;

public class AppConfig {
    public static String selectedLanguage = "en"; // Default language

    @SuppressWarnings("deprecation")
    public static void applyLocale(Context context, String languageCode) {
        selectedLanguage = languageCode;
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration config = new Configuration(resources.getConfiguration());
        config.setLocale(locale);
        context.getResources().updateConfiguration(config, resources.getDisplayMetrics());
    }
}
