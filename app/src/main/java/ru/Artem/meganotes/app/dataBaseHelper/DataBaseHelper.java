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

    public static final String DATABASE_TABLE_NOTES = "notes";
    public static final String ID_COLUMN = "_id";
    public static final String TITLE_NOTES_COLUMN = "note_title";
    public static final String CONTENT_COLUMN = "content";
    public static final String LAST_UPDATE_DATE_COLUMN = "last_update_date";

    public static final String DATABASE_TABLE_IMAGES = "imagepaths";
    public static final String ID_IMAGE = "_id";
    public static final String IMAGE_SOURCE_COLUMN = "path";
    public static final String ID_NOTE_COLUMN = "id_note";

    private static final String CREATE_TABLE_NOTES = "CREATE TABLE " + DATABASE_TABLE_NOTES
            + " (" + ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TITLE_NOTES_COLUMN + " TEXT NOT NULL, "
            + CONTENT_COLUMN + " TEXT, "
            + LAST_UPDATE_DATE_COLUMN + " TEXT)";

    private static final String CREATE_TABLE_IMAGES = "CREATE TABLE " + DATABASE_TABLE_IMAGES
            + " (" + ID_IMAGE + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ID_NOTE_COLUMN + " INTEGER, "
            + IMAGE_SOURCE_COLUMN + " TEXT, "
            + "FOREIGN KEY ("+ID_NOTE_COLUMN+") REFERENCES "+DATABASE_TABLE_IMAGES+"("+ID_COLUMN+") ON DELETE CASCADE ON UPDATE CASCADE);";

    private static DataBaseHelper sInstance;
    private static SQLiteDatabase sSqLiteDatabase;
    private static final String LOG_TAG = DataBaseHelper.class.getName();

    public static synchronized DataBaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DataBaseHelper(context);
            sSqLiteDatabase = sInstance.getWritableDatabase();
        }
        return sInstance;
    }

    private DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);

        db.execSQL("PRAGMA foreign_keys = ON;");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_NOTES);
        db.execSQL(CREATE_TABLE_IMAGES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static void addData(String titleNote, String contentNote, String imgPath, String lastUpdateDate) {
        try {
            ContentValues values = new ContentValues();

            values.put(DataBaseHelper.TITLE_NOTES_COLUMN, titleNote);
            values.put(DataBaseHelper.CONTENT_COLUMN, contentNote);
            values.put(DataBaseHelper.LAST_UPDATE_DATE_COLUMN, lastUpdateDate);

            long insertedNoteID = sSqLiteDatabase.insert(DataBaseHelper.DATABASE_TABLE_NOTES, null, values);

            ContentValues imagepath = new ContentValues();
            imagepath.put(DataBaseHelper.IMAGE_SOURCE_COLUMN, imgPath);
            imagepath.put(DataBaseHelper.ID_NOTE_COLUMN, insertedNoteID);

            long insertedImageID = sSqLiteDatabase.insert(DataBaseHelper.DATABASE_TABLE_IMAGES, null, imagepath);
        } catch (Throwable t) {
            Log.e(LOG_TAG, "Ошибка при добавлении в базу");
        }
    }

    public static void onDeleteSelectedNote(String[] id) {
        sSqLiteDatabase.delete(DataBaseHelper.DATABASE_TABLE_NOTES,
                DataBaseHelper.ID_COLUMN  + " = ?", id);
    }

    public static ModelNote getInsertedNote() {
        ModelNote newNote = null;

        String query = "select " + DataBaseHelper.TITLE_NOTES_COLUMN + ", "
                + DataBaseHelper.CONTENT_COLUMN + ", " + DataBaseHelper.LAST_UPDATE_DATE_COLUMN + ", "
                + DataBaseHelper.ID_COLUMN +  " from "
                + DataBaseHelper.DATABASE_TABLE_NOTES + " where " + DataBaseHelper.ID_COLUMN + " = (select last_insert_rowid())";
        //оставил именно rawQuery потому что не смог обычный db.query заставить работать с last_insert_rowid()

        Cursor cursor = sSqLiteDatabase.rawQuery(query, null);
        cursor.moveToFirst();
        int tempId = cursor.getInt(cursor.getColumnIndex(ID_COLUMN));
        Cursor imageCursor = sSqLiteDatabase.query(DATABASE_TABLE_IMAGES, null, ID_NOTE_COLUMN+"= ?", new String[]{String.valueOf(tempId)}, null, null, null);
        imageCursor.moveToFirst();
        List<String> tempList = new ArrayList<String>();

        if (imageCursor.moveToFirst()) {
            do {
                tempList.add(imageCursor.getString(imageCursor.getColumnIndex(DataBaseHelper.IMAGE_SOURCE_COLUMN)));
            } while (imageCursor.moveToNext());
        }
        cursor.moveToFirst();
        do {
            newNote = new ModelNote(
                    cursor.getString(cursor.getColumnIndex(DataBaseHelper.TITLE_NOTES_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(DataBaseHelper.CONTENT_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(DataBaseHelper.LAST_UPDATE_DATE_COLUMN)),
                    tempList,
                    cursor.getInt(cursor.getColumnIndex(DataBaseHelper.ID_COLUMN)));
        } while (cursor.moveToNext());
        cursor.close();
        imageCursor.close();
        return newNote;
    }

    public static List<ModelNote> getNotes() {
        List<ModelNote> notesList = new ArrayList<ModelNote>();

        Cursor cursor = sSqLiteDatabase.query(DATABASE_TABLE_NOTES, null, null, null, null, null, null);
        Cursor imageCursor = sSqLiteDatabase.query(DATABASE_TABLE_IMAGES, null, null, null, null, null, null);

        List<String> tempList = new ArrayList<String>();

        if (imageCursor.moveToFirst()) {
            while (imageCursor.moveToNext()) {
                tempList.add(imageCursor.getString(imageCursor.getColumnIndex(IMAGE_SOURCE_COLUMN)));
            }
        }else
        {
            tempList.add("image");
        }

        while (cursor.moveToNext()) {
            notesList.add(new ModelNote(
                    cursor.getString(cursor.getColumnIndex(DataBaseHelper.TITLE_NOTES_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(DataBaseHelper.CONTENT_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(DataBaseHelper.LAST_UPDATE_DATE_COLUMN)),
                    tempList,
                    cursor.getInt(cursor.getColumnIndex(DataBaseHelper.ID_COLUMN)))
            );
        }
        cursor.close();
        return notesList;
    }

    public static void editData(String column, String[] where, String value, String lastUpdateDate, int table) {
        if (table==0) { //данные изменяются в 1-ой таблице, notes
            ContentValues values = new ContentValues();

            values.put(column, value);
            values.put(DataBaseHelper.LAST_UPDATE_DATE_COLUMN, lastUpdateDate);

            sSqLiteDatabase.update(DataBaseHelper.DATABASE_TABLE_NOTES, values,
                    DataBaseHelper.ID_COLUMN + " = ?", where);
        }
        else
        { //данные изменяются во второй таблице, в imagepaths
            ContentValues values = new ContentValues();

            values.put(column, value);
            values.put(DataBaseHelper.LAST_UPDATE_DATE_COLUMN, lastUpdateDate);

            sSqLiteDatabase.update(DataBaseHelper.DATABASE_TABLE_IMAGES, values,
                    DataBaseHelper.ID_NOTE_COLUMN + " = ?", where);
        }
    }

    public static void deleteImage(String id)
    {
        sSqLiteDatabase.delete(DATABASE_TABLE_NOTES,ID_IMAGE+"= ?",new String[]{id});
    }

    public static void deleteAll() {
        sSqLiteDatabase.delete(DataBaseHelper.DATABASE_TABLE_NOTES, null, null);
    }
}