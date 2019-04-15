package com.MwandoJrTechnologies.the_smart_parent.BabyProducts;

public class Products {

    public String category;
    public String productDescription;
    public String productImage;
    public String productManufactureCompany;
    public String productName;


    public Products() {
        //empty constructor
    }

    public Products(String category, String productDescription, String productImage, String productManufactureCompany, String productName) {
        category = category;
        this.productDescription = productDescription;
        this.productImage = productImage;
        this.productManufactureCompany = productManufactureCompany;
        this.productName = productName;
    }

    public String getcategory() {
        return category;
    }

    public void setcategory(String category) {
        category = category;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getProductManufactureCompany() {
        return productManufactureCompany;
    }

    public void setProductManufactureCompany(String productManufactureCompany) {
        this.productManufactureCompany = productManufactureCompany;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}
