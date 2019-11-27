package com.mobiletemple.photopeople.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.mobiletemple.AsymmetricItem;

public class DemoItem implements AsymmetricItem {
    private int columnSpan;
    private int rowSpan;
    private int position;
    private String thumbnail;
    private String id,user_id,user_type;
    public DemoItem() {
        this(1, 1, 0);
    }

    public DemoItem(int columnSpan, int rowSpan, int position) {
        this.columnSpan = columnSpan;
        this.rowSpan = rowSpan;
        this.position = position;
    }

    public DemoItem(Parcel in) {
        readFromParcel(in);
    }

    @Override public int getColumnSpan() {
        return columnSpan;
    }

    @Override public int getRowSpan() {
        return rowSpan;
    }

    public int getPosition() {
        return position;
    }

//    @Override public String toString() {
//        return String.format("%s: %sx%s", position, rowSpan, columnSpan);
//    }

    @Override public int describeContents() {
        return 0;
    }

    private void readFromParcel(Parcel in) {
        columnSpan = in.readInt();
        rowSpan = in.readInt();
        position = in.readInt();
    }

    @Override public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(columnSpan);
        dest.writeInt(rowSpan);
        dest.writeInt(position);
    }
    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
    /* Parcelable interface implementation */
    public static final Parcelable.Creator<DemoItem> CREATOR = new Parcelable.Creator<DemoItem>() {
        @Override public DemoItem createFromParcel(@NonNull Parcel in) {
            return new DemoItem(in);
        }

        @Override @NonNull public DemoItem[] newArray(int size) {
            return new DemoItem[size];
        }
    };


    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }





    public String getUserId()
    {
        return user_id;
    }

    public void setUserId(String userid)
    {
        this.user_id = userid;
    }


    public String getUserType()
    {
        return user_type;
    }

    public void setUserType(String usertype)
    {
        this.user_type = usertype;
    }
}
