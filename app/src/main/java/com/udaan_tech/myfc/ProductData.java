package com.udaan_tech.myfc;

import com.google.firebase.Timestamp;

public class ProductData {
    private  String fcmail,productname,productimage,description,category,key;
    private int productprice;
    private Boolean status;
    private Timestamp date;

    public ProductData(String fcmail, String productname, String productimage, String description,
                       String category, int productprice, Boolean status, Timestamp date,String key) {
        this.fcmail = fcmail;
        this.productname = productname;
        this.productimage = productimage;
        this.description = description;
        this.category = category;
        this.productprice = productprice;
        this.status = status;
        this.date = date;
        this.key = key;
    }

    public ProductData() {
    }

    public String getKey() {
        return key;
    }

    public Boolean getStatus() {
        return status;
    }

    public String getFcmail() {
        return fcmail;
    }

    public String getProductname() {
        return productname;
    }

    public String getProductimage() {
        return productimage;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public int getProductprice() {
        return productprice;
    }

    public Timestamp getDate() {
        return date;
    }
}
