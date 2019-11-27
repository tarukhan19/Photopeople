package com.mobiletemple.photopeople.userauth;

public class Users {
    String userId,mobileno,name,image;



    public Users() {
    }



    public Users(String userId, String mobileno, String name,String image) {
        this.userId=userId;
        this.mobileno=mobileno;
        this.name=name;
        this.image=image;

    }
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMobileno() {
        return mobileno;
    }

    public void setMobileno(String mobileno) {
        this.mobileno = mobileno;
    }
}
