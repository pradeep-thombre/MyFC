package com.udaan_tech.myfc;

public class ShopData {
    private String Email,OName,Logo,Doc,Location,UPI,Shopname;
    private boolean Status;

    public ShopData(String email, String OName, String logo, String doc, String location, String UPI, String shopname, boolean status) {
        Email = email;
        this.OName = OName;
        Logo = logo;
        Doc = doc;
        Location = location;
        this.UPI = UPI;
        Shopname = shopname;
        Status = status;
    }

    public ShopData() {
    }

    public String getEmail() {
        return Email;
    }

    public String getOName() {
        return OName;
    }

    public String getLogo() {
        return Logo;
    }

    public String getDoc() {
        return Doc;
    }

    public String getLocation() {
        return Location;
    }

    public String getUPI() {
        return UPI;
    }

    public String getShopname() {
        return Shopname;
    }

    public boolean isStatus() {
        return Status;
    }
}
