package ru.Artem.meganotes.app.dataBaseHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import ru.Artem.meganotes.app.models.Note;

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
    public static final String NAME_NOTES_COLUMN = "note_title";
    public static final String CONTENT_COLUMN = "content";
    public static final String LAST_UPDATE_DATE_COLUMN = "last_update_date";

    public static final String DATABASE_TABLE_IMAGES = "imagepaths";
    public static final String ID_IMAGE = "_id";
    public static final String IMAGE_SOURCE_COLUMN = "path";
    public static final String ID_NOTE_COLUMN = "id_note";

    private static final String CREATE_TABLE_NOTES = "CREATE TABLE " + DATABASE_TABLE_NOTES
            + " (" + ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + NAME_NOTES_COLUMN + " TEXT NOT NULL, "
            + CONTENT_COLUMN + " TEXT, "
            + LAST_UPDATE_DATE_COLUMN + " TEXT)";

    private static final String CREATE_TABLE_IMAGES = "CREATE TABLE " + DATABASE_TABLE_IMAGES
            + " (" + ID_IMAGE + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ID_NOTE_COLUMN + " INTEGER, "
            + IMAGE_SOURCE_COLUMN + " TEXT, "
            + "FOREIGN KEY (" + ID_NOTE_COLUMN + ") REFERENCES " + DATABASE_TABLE_IMAGES + "(" + ID_COLUMN + ") ON DELETE CASCADE ON UPDATE CASCADE);";

    private static DataBaseHelper sInstance;
    private static final String LOG_TAG = DataBaseHelper.class.getName();
    private static final int EDIT_NOTES_TABLE_KEY = 0;

    public static synchronized DataBaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DataBaseHelper(context);
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

    public Note addNote(String name, String content, String date, List<String> imagePaths) throws SQLiteException {
        ContentValues values = new ContentValues();

        values.put(DataBaseHelper.NAME_NOTES_COLUMN, name);
        values.put(DataBaseHelper.CONTENT_COLUMN, content);
        values.put(DataBaseHelper.LAST_UPDATE_DATE_COLUMN, date);

        long insertedNoteID = sInstance.getWritableDatabase().insert(DataBaseHelper.DATABASE_TABLE_NOTES, null, values);

        ContentValues imagePath = new ContentValues();
        if (!imagePaths.isEmpty()) {
            for (int i = 0; i < imagePaths.size(); i++) {
                imagePath.put(DataBaseHelper.IMAGE_SOURCE_COLUMN, imagePaths.get(i));
                imagePath.put(DataBaseHelper.ID_NOTE_COLUMN, insertedNoteID);
                sInstance.getWritableDatabase().insert(DataBaseHelper.DATABASE_TABLE_IMAGES, null, imagePath);
            }
        }
        return new Note(name, content, date, imagePaths, insertedNoteID);
    }

    public void deleteSelectNote(Note note) {
        long id = note.getId();
        sInstance.getWritableDatabase().delete(DataBaseHelper.DATABASE_TABLE_NOTES,
                DataBaseHelper.ID_COLUMN + " = ?", new String[]{String.valueOf(id)});
    }

    public List<Note> getAllNotesWithoutImages() {
        List<Note> notesList = new ArrayList<>();
        Cursor cursor = sInstance.getReadableDatabase().query(DATABASE_TABLE_NOTES, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            notesList.add(new Note(
                            cursor.getString(cursor.getColumnIndex(DataBaseHelper.NAME_NOTES_COLUMN)),
                            cursor.getString(cursor.getColumnIndex(DataBaseHelper.CONTENT_COLUMN)),
                            cursor.getString(cursor.getColumnIndex(DataBaseHelper.LAST_UPDATE_DATE_COLUMN)),
                            null,
                            cursor.getInt(cursor.getColumnIndex(DataBaseHelper.ID_COLUMN)))
            );
        }
        cursor.close();
        return notesList;
    }

    public void updateNote(Note note) {
        long idUpdatedNote = note.getId();
        String nameUpdatedNote = note.getNameNote();
        String contentUpdatedNote = note.getContent();
        String dateUpdatedNote = note.getDateLastUpdateNote();
        List<String> imagesUpdatedNote = note.getPathImg();

        ContentValues values = new ContentValues();
        values.put(NAME_NOTES_COLUMN, nameUpdatedNote);
        values.put(CONTENT_COLUMN, contentUpdatedNote);
        values.put(LAST_UPDATE_DATE_COLUMN, dateUpdatedNote);
        sInstance.getWritableDatabase().update(DATABASE_TABLE_NOTES, values,
                DataBaseHelper.ID_COLUMN + " = ?", new String[]{String.valueOf(idUpdatedNote)});

        List<String> oldImagesThisNote = new ArrayList<>();
        Cursor cursor = sInstance.getReadableDatabase().query(DATABASE_TABLE_IMAGES,
                null,
                ID_NOTE_COLUMN + " = ?",
                new String[]{String.valueOf(idUpdatedNote)},
                null, null, null);
        if (cursor.moveToNext()) {
            oldImagesThisNote.add(cursor.getString(cursor.getColumnIndex(IMAGE_SOURCE_COLUMN)));
        }
        cursor.close();

        if (!oldImagesThisNote.equals(imagesUpdatedNote)) {
            if (!imagesUpdatedNote.isEmpty()) {
                ContentValues containerForImages = new ContentValues();
                for (int i = 0; i < imagesUpdatedNote.size(); i++) {
                    containerForImages.put(IMAGE_SOURCE_COLUMN, imagesUpdatedNote.get(i));
                    containerForImages.put(ID_NOTE_COLUMN, idUpdatedNote);
                    sInstance.getWritableDatabase().insert(DATABASE_TABLE_IMAGES, null, containerForImages);
                }
            } else {
                for (int i = 0; i < oldImagesThisNote.size(); i++) {
                    sInstance.getWritableDatabase().delete(DATABASE_TABLE_IMAGES, ID_NOTE_COLUMN + "= ?", new String[]{String.valueOf(idUpdatedNote)});
                    //удаление всех картинок, надо сюда попробовать при тесте ввести переменную и посмотреть сколько удаляет.
                }
            }
        }
    }

    public void deleteImage(String id) {
        sInstance.getWritableDatabase().delete(DATABASE_TABLE_IMAGES, ID_IMAGE + "= ?", new String[]{id});
    }

    public void deleteAllNotesAndImages() {
        sInstance.getWritableDatabase().delete(DataBaseHelper.DATABASE_TABLE_NOTES, null, null);
        sInstance.getWritableDatabase().delete(DataBaseHelper.DATABASE_TABLE_IMAGES, null, null);
    }
}