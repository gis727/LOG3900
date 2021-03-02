package com.example.client_leger.Utilities;

import android.annotation.SuppressLint;
import android.graphics.Color;

import java.util.concurrent.TimeUnit;

public final class Converter {

    @SuppressLint("DefaultLocale")
    public static final String ToTimeString(int timestamp) {
        return String.format("%02d:%02d", TimeUnit.SECONDS.toMinutes(timestamp) -
                TimeUnit.HOURS.toMinutes(TimeUnit.SECONDS.toHours(timestamp)), TimeUnit.SECONDS.toSeconds(timestamp) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(timestamp)));
    }

    public static final String ColorToString(final int color) {
        return String.format("#%06X", 0xFFFFFF & color);
    }

    public static final int StringToColor(String color) {
        return Color.parseColor(color);
    }
}
