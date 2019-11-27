package com.mobiletemple.photopeople.model;

import android.os.Parcel;


/**
 * Created by Ishan Puranik on 04/05/2018.
 */

public class FreedetailimageDTO  {


    private String thumbnail;
    int id;
    String text;

    public FreedetailimageDTO() {
    }

    public FreedetailimageDTO(int id) {
        this.id=id;
    }

    public int getThumbnailId() {
        return id;
    }

    public void setThumbnailId(int id) {
        this.thumbnail = thumbnail;
    }



    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }



}
