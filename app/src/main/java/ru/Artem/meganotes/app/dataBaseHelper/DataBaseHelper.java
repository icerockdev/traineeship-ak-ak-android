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
    public static final String IMG_PATH_COLUMN = "img_path";
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
            //+ IMG_PATH_COLUMN + " INTEGER)";
           // + "FOREIGN KEY ("+IMG_PATH_COLUMN+") REFERENCES "+DATABASE_TABLE_IMAGES+"("+ID_NOTE_COLUMN+") ON DELETE CASCADE ON UPDATE CASCADE);";
    private static final String CREATE_TABLE_IMAGES = "CREATE TABLE " + DATABASE_TABLE_IMAGES
            + " (" + ID_IMAGE + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ID_NOTE_COLUMN + " INTEGER, "
            + IMAGE_SOURCE_COLUMN + " TEXT);";

    private static DataBaseHelper sInstance;
    private static SQLiteDatabase mSqLiteDatabase;
    private final String LOG_TAG = DataBaseHelper.class.getName();

    public static synchronized DataBaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DataBaseHelper(context);
            mSqLiteDatabase = sInstance.getWritableDatabase();
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

    public void addData(String titleNote, String contentNote, String imgPath, String lastUpdateDate) {
        try {
            //Добавляем записи в CV для первой таблицы
            ContentValues values = new ContentValues();

            values.put(DataBaseHelper.TITLE_NOTES_COLUMN, titleNote);
            values.put(DataBaseHelper.CONTENT_COLUMN, contentNote);
            //values.put(DataBaseHelper.CONTENT_COLUMN, contentNote);
            //values.put(DataBaseHelper.IMG_PATH_COLUMN, imgPath);//исправить, пока не знаю как
            values.put(DataBaseHelper.LAST_UPDATE_DATE_COLUMN, lastUpdateDate);

            long insertedNoteID = mSqLiteDatabase.insert(DataBaseHelper.DATABASE_TABLE_NOTES, null, values);
            Log.d(LOG_TAG,"we inserted in notes table:"+insertedNoteID);
            //после того как вставили первую без поля Img_path создаём CV для второй таблицы и добавляем данные в него
            ContentValues imagepath = new ContentValues();

            imagepath.put(DataBaseHelper.IMAGE_SOURCE_COLUMN, imgPath);
            imagepath.put(DataBaseHelper.ID_NOTE_COLUMN,insertedNoteID);

            long insertedImageID = mSqLiteDatabase.insert(DataBaseHelper.DATABASE_TABLE_IMAGES, null, imagepath);
            Log.d(LOG_TAG,"we inserted in imagePaths table: "+insertedImageID);
            //после того как добавили данные во вторую таблицу узнали id добавленной записи, и добавили этот id в незаполненное поле в первой таблице
           // values.put(DataBaseHelper.IMG_PATH_COLUMN, insertedImageID);
            //mSqLiteDatabase.update(DataBaseHelper.DATABASE_TABLE_NOTES, values, DataBaseHelper.ID_COLUMN + " = ?", new String[] { "(select last_insert_rowid())"});
            //здесь пока добавил вот эту строчку красивую, если не прокатит - попробую по insertedImageID
        } catch (Throwable t) {
            Log.e(LOG_TAG, "Ошибка при добавлении в базу");
        }
    }

    public void onDeleteSelectedNote(String[] id) {
        mSqLiteDatabase.delete(DataBaseHelper.DATABASE_TABLE_NOTES,
                DataBaseHelper.ID_COLUMN  + " = ?", id);
    }

    public ModelNote getInsertedNote() {
        ModelNote newNote = null;

        String query = "select " + DataBaseHelper.TITLE_NOTES_COLUMN + ", "
                + DataBaseHelper.CONTENT_COLUMN + ", " + DataBaseHelper.LAST_UPDATE_DATE_COLUMN + ", "
                + DataBaseHelper.IMG_PATH_COLUMN + ", " + DataBaseHelper.ID_COLUMN +  " from "
                + DataBaseHelper.DATABASE_TABLE_NOTES + " where " + DataBaseHelper.ID_COLUMN + " = (select last_insert_rowid())";

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

    public List<ModelNote> getNotes() {
        List<ModelNote> notesList = new ArrayList<ModelNote>();

        String query = "select " + DataBaseHelper.TITLE_NOTES_COLUMN + ", "
                + DataBaseHelper.CONTENT_COLUMN + ", " + DataBaseHelper.LAST_UPDATE_DATE_COLUMN + ", "
                + DataBaseHelper.ID_COLUMN + " from "
                + DataBaseHelper.DATABASE_TABLE_NOTES;

        Cursor cursor = mSqLiteDatabase.rawQuery(query, null);

        while (cursor.moveToNext()) {
            notesList.add(new ModelNote(
                    cursor.getString(cursor.getColumnIndex(DataBaseHelper.TITLE_NOTES_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(DataBaseHelper.CONTENT_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(DataBaseHelper.LAST_UPDATE_DATE_COLUMN)),
                    "заглушка",
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

        mSqLiteDatabase.update(DataBaseHelper.DATABASE_TABLE_NOTES, values,
                DataBaseHelper.ID_COLUMN + " = ?", where);
    }

    public void deleteAll() {
        mSqLiteDatabase.delete(DataBaseHelper.DATABASE_TABLE_NOTES, null, null);
    }
}