package ru.Artem.meganotes.app.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Артем on 25.04.2016.
 */
public class DateUtils {

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d.MM.yyyy  k:mm", Locale.ROOT);

    public static String getDate() {
        long date = System.currentTimeMillis();
        return simpleDateFormat.format(date);
    }
}
