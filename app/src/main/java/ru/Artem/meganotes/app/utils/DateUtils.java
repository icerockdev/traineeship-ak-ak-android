package ru.Artem.meganotes.app.utils;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by Артем on 25.04.2016.
 */
public class DateUtils {
    private static SimpleDateFormat sFormatCreateNote = new SimpleDateFormat("dd.MM.yyyy  k:mm", Locale.ROOT);
    private static SimpleDateFormat sFormatCreateFile = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ROOT);

    public static String getDate() {
        return sFormatCreateNote.format(System.currentTimeMillis());
    }

    public static String getDateCreateFile() {
        return sFormatCreateFile.format(new Date());
    }
}
