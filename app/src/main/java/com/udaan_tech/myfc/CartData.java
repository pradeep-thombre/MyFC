package com.udaan_tech.myfc;

public class CartData {
    private  String shopemail,productname,productimage,key;
    private int productprice,quantity;

    public CartData(String shopemail, String productname, String productimage, String key, int productprice, int quantity) {
        this.shopemail = shopemail;
        this.productname = productname;
        this.productimage = productimage;
        this.key = key;
        this.productprice = productprice;
        this.quantity = quantity;
    }

    public CartData() {
    }

    public String getShopemail() {
        return shopemail;
    }

    public String getProductname() {
        return productname;
    }

    public String getProductimage() {
        return productimage;
    }

    public String getKey() {
        return key;
    }

    public int getProductprice() {
        return productprice;
    }

    public int getQuantity() {
        return quantity;
    }
}
