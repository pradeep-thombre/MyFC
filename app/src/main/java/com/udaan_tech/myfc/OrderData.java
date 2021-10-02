package com.udaan_tech.myfc;

import com.google.firebase.Timestamp;

public class OrderData {
    private String prodname,prodimage,custphone,custname,custid, shopemail, shopupi, shopname,orderstatus;
    private Boolean deliverystatus;
    private int ordercounter, amount;
    private Timestamp date;

    public OrderData(String prodname, String prodimage, String custphone, String custname, String custid,
                     String shopemail, String shopupi, String shopname, String orderstatus, Boolean deliverystatus,
                     int ordercounter, int amount, Timestamp date) {
        this.prodname = prodname;
        this.prodimage = prodimage;
        this.custphone = custphone;
        this.custname = custname;
        this.custid = custid;
        this.shopemail = shopemail;
        this.shopupi = shopupi;
        this.shopname = shopname;
        this.orderstatus = orderstatus;
        this.deliverystatus = deliverystatus;
        this.ordercounter = ordercounter;
        this.amount = amount;
        this.date = date;
    }

    public OrderData() {
    }

    public String getProdname() {
        return prodname;
    }

    public String getProdimage() {
        return prodimage;
    }

    public String getCustphone() {
        return custphone;
    }

    public String getCustname() {
        return custname;
    }

    public String getCustid() {
        return custid;
    }

    public String getShopemail() {
        return shopemail;
    }

    public String getShopupi() {
        return shopupi;
    }

    public String getShopname() {
        return shopname;
    }

    public String getOrderstatus() {
        return orderstatus;
    }

    public Boolean getDeliverystatus() {
        return deliverystatus;
    }

    public int getOrdercounter() {
        return ordercounter;
    }

    public int getAmount() {
        return amount;
    }

    public Timestamp getDate() {
        return date;
    }
}
