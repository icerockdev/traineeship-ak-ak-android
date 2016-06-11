package ru.Artem.meganotes.app.models;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Артем on 07.04.2016.
 */
public class Note implements Parcelable {

    private String mNameNote;
    private String mLastDateUpdateNote;
    private List<String> mPathImg;
    private String mContent;
    private long mId;
    private int mPositionInAdapter;
    private Bitmap mBitmap;
    private boolean mDeletedNote;

    public Note(String nameNote, String noteContent, String lastUpdateNote, List<String> paths, long id) {
        this.mNameNote = nameNote;
        this.mLastDateUpdateNote = lastUpdateNote;
        this.mContent = noteContent;
        this.mPathImg = paths;
        this.mId = id;
    }

    private Note(Parcel parcel) {
        this.mNameNote = parcel.readString();
        this.mContent = parcel.readString();
        this.mLastDateUpdateNote = parcel.readString();
        this.mDeletedNote = parcel.readByte() != 0;
        this.mPathImg = new ArrayList<String>();
        parcel.readList(this.mPathImg, Note.class.getClassLoader());
        this.mPositionInAdapter = parcel.readInt();
        this.mId = parcel.readInt();
    }

    public boolean isDeletedNote() {
        return mDeletedNote;
    }

    public void setDeletedNote(boolean deletedNote) {
        this.mDeletedNote = deletedNote;
    }

    public void setDateLastUpdateNote(String mLastUpdateNote) {
        this.mLastDateUpdateNote = mLastUpdateNote;
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

    public void setListPathImages(List<String> listPathImages) {
        this.mPathImg = listPathImages;
    }
    public void setPathImg(String pathImg) {
        this.mPathImg.add(pathImg);
    }

    public String getDateLastUpdateNote() {
        return mLastDateUpdateNote;
    }

    public List<String> getPathImg() {
        return mPathImg;
    }

    public long getId() {
        return mId;
    }

    public void setId(long mId) {
        this.mId = mId;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String mTitleContent) {
        this.mContent = mTitleContent;
    }

    public static final Parcelable.Creator<Note> CREATOR = new Parcelable.Creator<Note>() {
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        public Note[] newArray(int size) {
            return new Note[size];
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
        dest.writeString(mLastDateUpdateNote);
        dest.writeByte((byte) (mDeletedNote ? 1 : 0));
        dest.writeList(mPathImg);
        dest.writeInt(mPositionInAdapter);
        dest.writeLong(mId);
    }
}
