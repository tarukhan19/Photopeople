package com.mobiletemple.photopeople.model;

public class BuyNewProdDTO {
    String productname,categoryName,price,dayesago,favorite,prodcondition,prodimage,feature,prodId,countryname;
    public BuyNewProdDTO() {
    }

    public String getCountryname() {
        return countryname;
    }

    public void setCountryname(String countryname) {
        this.countryname = countryname;
    }

    public String getProdId() {return prodId;}

    public void setProdId(String prodId) {
        this.prodId = prodId;
    }

    public String getFeature() {return feature;}

    public void setFeature(String feature) {
        this.feature = feature;
    }


    public String getProdName() {return productname;}

    public void setProdName(String productname) {
        this.productname = productname;
    }

    public String getCatgName() {return categoryName;}

    public void setCategName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getPrice() {return price;}

    public void setPrice(String price) {
        this.price = price;
    }


    public  String getDayesago() {return dayesago;}
    public  void setDayesago(String dayesago) {this.dayesago=dayesago;}

    public  String getFavorite() {return favorite;}
    public  void setFavorite(String favorite) {this.favorite=favorite;}

    public  String getProdcondition() {return prodcondition;}
    public  void setProdcondition(String prodcondition) {this.prodcondition=prodcondition;}

    public  String getProdimage() {return prodimage;}
    public  void setProdimage(String prodimage) {this.prodimage=prodimage;}
}
