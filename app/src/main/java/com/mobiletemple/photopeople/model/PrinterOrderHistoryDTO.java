package com.mobiletemple.photopeople.model;

public class PrinterOrderHistoryDTO {
    String printername,albumsize,ratepersheet,totalprice,prodimage,orderid,prodId;
    public PrinterOrderHistoryDTO() {
    }

    public String getOrderId() {return orderid;}

    public void setOrderId(String orderid) {
        this.orderid = orderid;
    }

    public String getPrinterName() {return printername;}

    public void setPrinterName(String printerName) {
        this.printername = printerName;
    }

    public String getAlbumSize() {return albumsize;}

    public void setAlbumSize(String albumsize) {
        this.albumsize = albumsize;
    }

    public String getRateperSheet() {return ratepersheet;}

    public void setRateperSheet(String ratepersheet) {
        this.ratepersheet = ratepersheet;
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

