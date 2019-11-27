package com.mobiletemple.photopeople.model;

public class RatingDTO {

    private String name;
    private String pic,review;
    private double rating;

    public RatingDTO() {
    }

//    public Hotdeals(String studioname, String discountmsg, String termscondition, String thumbnail) {
//        this.studioname = studioname;
//        this.discountmsg = discountmsg;
//        this.termscondition = termscondition;
//        this.thumbnail = thumbnail;
//    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }


}
