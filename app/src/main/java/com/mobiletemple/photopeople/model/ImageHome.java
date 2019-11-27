package com.mobiletemple.photopeople.model;

/**
 * Created by shree on 4/21/2018.
 */

public class ImageHome {


    private String id,user_id,user_type;
    private String thumbnail;

    public ImageHome() { }






    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
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
