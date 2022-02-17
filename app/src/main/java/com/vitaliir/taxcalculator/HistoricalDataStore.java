package com.vitaliir.taxcalculator;

import android.preference.PreferenceDataStore;

import androidx.annotation.Nullable;

public class HistoricalDataStore implements PreferenceDataStore {

    @Override
    public void putString(String key, @Nullable String value) {
        // Save the value somewhere
    }

    @Override
    @Nullable
    public String getString(String key, @Nullable String defValue) {
        return null;
        // Retrieve the value
    }
}
