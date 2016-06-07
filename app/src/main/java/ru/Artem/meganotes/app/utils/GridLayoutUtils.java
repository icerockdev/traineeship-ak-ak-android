package ru.Artem.meganotes.app.utils;

import android.support.v7.widget.GridLayout;
import android.view.View;

/**
 * Created by Артем on 07.06.2016.
 */
public class GridLayoutUtils {
    public static void addViewToGrid(GridLayout field, View view, int size) {//добавить параметр для установки количества колонок
        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();

        layoutParams.width = 0;
        layoutParams.height = size;
        layoutParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        layoutParams.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);

        field.addView(view, layoutParams);
    }
}
