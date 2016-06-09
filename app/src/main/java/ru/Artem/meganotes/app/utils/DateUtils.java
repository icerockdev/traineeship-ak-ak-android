package ru.Artem.meganotes.app.utils;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by Артем on 25.04.2016.
 */
public class DateUtils {

    public static String getDate() {
        long date = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d.MM.yyyy  k:mm", Locale.ROOT);
        return simpleDateFormat.format(date);
    }
}
