package com.mobiletemple.photopeople.model;

public class TrendingTimelineDTO {

    private String  postid,distance,userid,usertype,username,posttype,postimage,postdate,reportabusestatus,postlikestatus,likecount,trendingstatus,userimage,videolink;
    private static boolean  isLikeSelected ;


    public TrendingTimelineDTO() {
    }

    public String getUsertype() {
        return usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }



    public String getUserimage() {
        return userimage;
    }

    public String getVideolink() {
        return videolink;
    }

    public void setVideolink(String videolink) {
        this.videolink = videolink;
    }

    public void setUserimage(String userimage) {
        this.userimage = userimage;
    }

    public void setLikeSelected(boolean isLikeSelected) {
        isLikeSelected = isLikeSelected;
    }


    public static boolean isLikeSelected() {
        return isLikeSelected;
    }


    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPosttype() {
        return posttype;
    }

    public void setPosttype(String posttype) {
        this.posttype = posttype;
    }

    public String getPostimage() {
        return postimage;
    }

    public void setPostimage(String postimage) {
        this.postimage = postimage;
    }

    public String getPostdate() {
        return postdate;
    }

    public void setPostdate(String postdate) {
        this.postdate = postdate;
    }

    public String getReportabusestatus() {
        return reportabusestatus;
    }

    public void setReportabusestatus(String reportabusestatus) {
        this.reportabusestatus = reportabusestatus;
    }

    public String getPostlikestatus() {
        return postlikestatus;
    }

    public void setPostlikestatus(String postlikestatus) {
        this.postlikestatus = postlikestatus;
    }

    public String getLikecount() {
        return likecount;
    }

    public void setLikecount(String likecount) {
        this.likecount = likecount;
    }

    public String getTrendingstatus() {
        return trendingstatus;
    }

    public void setTrendingstatus(String trendingstatus) {
        this.trendingstatus = trendingstatus;
    }
}
