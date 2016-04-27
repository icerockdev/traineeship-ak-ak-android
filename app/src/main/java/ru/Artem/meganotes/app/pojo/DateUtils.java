package ru.Artem.meganotes.app.pojo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Артем on 25.04.2016.
 */
public class DateUtils {

    public static String getDate() {
        long date = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.ROOT);
        return simpleDateFormat.format(date);
    }

    public static String getDateCreateFile() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ROOT);
        return simpleDateFormat.format(new Date());
    }
}
