package com.mobiletemple.photopeople.model;

/**
 * Created by shree on 4/21/2018.
 */

public class Hotdeals {
    private String studioname;
    private String thumbnail,discountmsg,termscondition,prodId,prodCondition,price;

    public Hotdeals() {
    }

//    public Hotdeals(String studioname, String discountmsg, String termscondition, String thumbnail) {
//        this.studioname = studioname;
//        this.discountmsg = discountmsg;
//        this.termscondition = termscondition;
//        this.thumbnail = thumbnail;
//    }


    public String gettermscondition() {
        return termscondition;
    }

    public void settermscondition(String termscondition) {
        this.termscondition = termscondition;
    }

    public String getstudioname() {
        return studioname;
    }

    public void setstudioname(String studioname) {
        this.studioname = studioname;
    }

    public String getdiscountmsg() {
        return discountmsg;
    }

    public void setdiscountmsg(String discountmsg) {
        this.discountmsg = discountmsg;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getprodId() {
        return prodId;
    }

    public void setprodId(String prodId) {
        this.prodId = prodId;
    }



    public String getprodCondition() {
        return prodCondition;
    }

    public void setProdCondition(String prodCondition) {
        this.prodCondition = prodCondition;
    }



    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
