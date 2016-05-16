package ru.Artem.meganotes.app.models;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ru.Artem.meganotes.app.dataBaseHelper.DataBaseHelper;

/**
 * Created by Артем on 07.04.2016.
 */
public class ModelNote implements Parcelable {

    private String mNameNote;
    private String mLastUpdateNote;
    private List<String> mPathImg;
    private String mContent;
    private int mId;
    private int mPositionInAdapter;
    private Bitmap mBitmap;

    public ModelNote(String nameNote, String noteContent, String lastUpdateNote, List<String> paths, int id) {
        this.mNameNote = nameNote;
        this.mLastUpdateNote = lastUpdateNote;
        this.mContent = noteContent;
        this.mPathImg = paths;
        this.mId = id;
    }

    private ModelNote(Parcel parcel) {
        this.mNameNote = parcel.readString();
        this.mContent = parcel.readString();
        this.mLastUpdateNote = parcel.readString();
        this.mPathImg = new ArrayList<String>();
        parcel.readList(this.mPathImg, ModelNote.class.getClassLoader());
        this.mPositionInAdapter = parcel.readInt();
        this.mId = parcel.readInt();
    }

    public void setLastUpdateNote(String mLastUpdateNote) {
        this.mLastUpdateNote = mLastUpdateNote;
    }

    public int getPositionInAdapter() {
        return mPositionInAdapter;
    }

    public void setPositionInAdapter(int mPositionInAdapter) {
        this.mPositionInAdapter = mPositionInAdapter;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.mBitmap = bitmap;
    }

    public String getNameNote() {
        return mNameNote;
    }

    public void setNameNote(String nameNote) {
        this.mNameNote = nameNote;
    }

    public String getLastUpdateNote() {
        return mLastUpdateNote;
    }

    public List<String> getPathImg() {
        return mPathImg;
    }

    public void setPathImg(String pathImg) {
        this.mPathImg.add(pathImg);
    }

    public int getId() {
        return mId;
    }

    public void setId(int mId) {
        this.mId = mId;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String mTitleContent) {
        this.mContent = mTitleContent;
    }

    public static final Parcelable.Creator<ModelNote> CREATOR = new Parcelable.Creator<ModelNote>() {
        public ModelNote createFromParcel(Parcel in) {
            return new ModelNote(in);
        }

        public ModelNote[] newArray(int size) {
            return new ModelNote[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mNameNote);
        dest.writeString(mContent);
        dest.writeString(mLastUpdateNote);
        dest.writeList(mPathImg);
        dest.writeInt(mPositionInAdapter);
        dest.writeInt(mId);
    }
}
