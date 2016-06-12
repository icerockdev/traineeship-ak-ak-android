package ru.Artem.meganotes.app.utils;

import android.support.v7.widget.GridLayout;
import android.view.View;

/**
 * Created by Артем on 07.06.2016.
 */
public class GridLayoutUtils {

    public static void addViewToGrid(GridLayout field, View view, int size, int countColumn) {
        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();

        int index = field.getChildCount();
        layoutParams.width = 0;
        layoutParams.height = size;
        layoutParams.rowSpec = GridLayout.spec(index / countColumn, 1f);
        layoutParams.columnSpec = GridLayout.spec(index % countColumn, 1f);

        field.addView(view, layoutParams);
    }
}
