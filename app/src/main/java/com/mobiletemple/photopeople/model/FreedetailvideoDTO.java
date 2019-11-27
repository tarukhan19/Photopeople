package com.mobiletemple.photopeople.model;

/**
 * Created by shree on 5/7/2018.
 */

public class FreedetailvideoDTO {

    private String video,videoid,userid,usertype;
    public FreedetailvideoDTO() {
    }
    //int imageid,,String imagepath
    public FreedetailvideoDTO(String video) {
        this.video = video;
//        this.Imageid=Imageid;
//        this.imagepath=imagepath;
    }

    public String getUsertype() {
        return usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

    public String getVideoid() {
        return videoid;
    }

    public void setVideoid(String videoid) {
        this.videoid = videoid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }


}
