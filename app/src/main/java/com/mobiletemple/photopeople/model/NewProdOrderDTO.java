package com.mobiletemple.photopeople.model;

public class NewProdOrderDTO {
    String printername,albumsize,time,totalprice,prodimage,orderid,prodId;
    public NewProdOrderDTO() {
    }

    public String getOrderId() {return orderid;}

    public void setOrderId(String orderid) {
        this.orderid = orderid;
    }

    public String getPoductName() {return printername;}

    public void setPoductName(String printerName) {
        this.printername = printerName;
    }

    public String getAlbumSize() {return albumsize;}

    public void setAlbumSize(String albumsize) {
        this.albumsize = albumsize;
    }

    public String getTime() {return time;}

    public void setTime(String time) {
        this.time = time;
    }




    public  String gettotalprice() {return totalprice;}
    public  void settotalprice(String totalprice) {this.totalprice=totalprice;}

    public  String getProdimage() {return prodimage;}
    public  void setProdimage(String prodimage) {this.prodimage=prodimage;}

    public String getWorkStatus() {
        return prodId;
    }

    public void setWorkStatus(String prodId) {
        this.prodId=prodId;
    }
}
