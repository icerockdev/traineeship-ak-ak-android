package ru.Artem.meganotes.app.dataBaseHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import ru.Artem.meganotes.app.models.ModelNote;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Артем on 07.04.2016.
 */
public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "mydb.db";
    private static final int DATABASE_VERSION = 1;

    public static final String DATABASE_TABLE = "notes";
    public static final String ID_COLUMN = "_id";
    public static final String TITLE_NOTES_COLUMN = "note_title";
    public static final String CONTENT_COLUMN = "content";
    public static final String IMG_PATH_COLUMN = "img_path";
    public static final String CREATE_DATE_COLUMN = "create_date";
    public static final String LAST_UPDATE_DATE_COLUMN = "last_update_date";
    public static final String CATEGORY_COLUMN = "category_column";

    private static final String CREATE_TABLE = "CREATE TABLE " + DATABASE_TABLE
            + " (" + ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TITLE_NOTES_COLUMN
            + " TEXT NOT NULL, " + CONTENT_COLUMN + " TEXT, " + IMG_PATH_COLUMN
            + " TEXT, " + CREATE_DATE_COLUMN + " TEXT NOT NULL, " + LAST_UPDATE_DATE_COLUMN
            + " TEXT, " + CATEGORY_COLUMN + " INTEGER NOT NULL);";

    private static DataBaseHelper mInstance;
    private static SQLiteDatabase mSqLiteDatabase;
    private final String LOG_TAG = "myLogs";

    public static synchronized DataBaseHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DataBaseHelper(context);
            mSqLiteDatabase = mInstance.getWritableDatabase();
        }
        return mInstance;
    }

    private DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addData(String titleNote, String contentNote, String imgPath,
                               String createDateNote, String lastUpdateDate, int category) {
        try {
            ContentValues values = new ContentValues();

            values.put(DataBaseHelper.TITLE_NOTES_COLUMN, titleNote);
            values.put(DataBaseHelper.CONTENT_COLUMN, contentNote);
            values.put(DataBaseHelper.CONTENT_COLUMN, contentNote);
            values.put(DataBaseHelper.IMG_PATH_COLUMN, imgPath);
            values.put(DataBaseHelper.CREATE_DATE_COLUMN, createDateNote);
            values.put(DataBaseHelper.LAST_UPDATE_DATE_COLUMN, lastUpdateDate);
            values.put(DataBaseHelper.CATEGORY_COLUMN, category);

            mSqLiteDatabase.insert(DataBaseHelper.DATABASE_TABLE, null, values);
        } catch (Throwable t) {
            Log.e(LOG_TAG, "Ошибка при добавлении в базу");
        }
    }

    public void onDeleteSelectedNote(String[] id) {
        mSqLiteDatabase.delete(DataBaseHelper.DATABASE_TABLE,
                DataBaseHelper.ID_COLUMN  + " = ?", id);
    }

    public ModelNote getInsertedNote() {
        ModelNote newNote = null;

        String query = "select " + DataBaseHelper.TITLE_NOTES_COLUMN + ", "
                + DataBaseHelper.CONTENT_COLUMN + ", " + DataBaseHelper.LAST_UPDATE_DATE_COLUMN + ", "
                + DataBaseHelper.IMG_PATH_COLUMN + ", " + DataBaseHelper.ID_COLUMN + ", "
                + DataBaseHelper.CATEGORY_COLUMN + " from " + DataBaseHelper.DATABASE_TABLE
                + " where " + DataBaseHelper.ID_COLUMN + " = (select last_insert_rowid())";

        Cursor cursor = mSqLiteDatabase.rawQuery(query, null);

        while (cursor.moveToNext()) {
            newNote = new ModelNote(
                    cursor.getString(cursor.getColumnIndex(DataBaseHelper.TITLE_NOTES_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(DataBaseHelper.CONTENT_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(DataBaseHelper.LAST_UPDATE_DATE_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(DataBaseHelper.IMG_PATH_COLUMN)),
                    cursor.getInt(cursor.getColumnIndex(DataBaseHelper.ID_COLUMN)));
        }

        cursor.close();
        return newNote;
    }

    public List<ModelNote> getNotes(String[] where) {
        List<ModelNote> notesList = new ArrayList<ModelNote>();

        String query = "select " + DataBaseHelper.TITLE_NOTES_COLUMN + ", "
                + DataBaseHelper.CONTENT_COLUMN + ", " + DataBaseHelper.LAST_UPDATE_DATE_COLUMN + ", "
                + DataBaseHelper.IMG_PATH_COLUMN + ", " + DataBaseHelper.ID_COLUMN + " from "
                + DataBaseHelper.DATABASE_TABLE;

        if (where != null) {
            query += " where " + CATEGORY_COLUMN + " = ?";
        }

        Cursor cursor = mSqLiteDatabase.rawQuery(query, where);

        while (cursor.moveToNext()) {
            notesList.add(new ModelNote(
                    cursor.getString(cursor.getColumnIndex(DataBaseHelper.TITLE_NOTES_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(DataBaseHelper.CONTENT_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(DataBaseHelper.LAST_UPDATE_DATE_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(DataBaseHelper.IMG_PATH_COLUMN)),
                    cursor.getInt(cursor.getColumnIndex(DataBaseHelper.ID_COLUMN)))
            );
        }

        cursor.close();
        return notesList;
    }

    public void editData(String column, String[] where, String value, String lastUpdateDate) {
        ContentValues values = new ContentValues();

        values.put(column, value);
        values.put(DataBaseHelper.LAST_UPDATE_DATE_COLUMN, lastUpdateDate);

        mSqLiteDatabase.update(DataBaseHelper.DATABASE_TABLE, values,
                DataBaseHelper.ID_COLUMN + " = ?", where);
    }

    public void deleteAll() {
        mSqLiteDatabase.delete(DataBaseHelper.DATABASE_TABLE, null, null);
    }
}