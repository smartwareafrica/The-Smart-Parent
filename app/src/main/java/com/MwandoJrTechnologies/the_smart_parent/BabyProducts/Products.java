package com.MwandoJrTechnologies.the_smart_parent.BabyProducts;

/**
 * Getting products from the database
 */
public class Products {

    public String category;
    public String productDescription;
    public String productImage;
    public String productManufactureCompany;
    public String productName;
    public String productKey;
    private float productRating;


    public Products() {
        //empty constructor
    }

    public Products(String category,
                    String productDescription,
                    String productImage,
                    String productManufactureCompany,
                    String productName,
                    String productKey,
                    float productRating) {
        this.category = category;
        this.productDescription = productDescription;
        this.productImage = productImage;
        this.productManufactureCompany = productManufactureCompany;
        this.productName = productName;
        this.productKey = productKey;
        this.productRating = productRating;

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


    public String getProductKey() {
        return productKey;
    }

    public void setProductKey(String productKey) {
        this.productKey = productKey;
    }

    public float getProductRating() {
        return productRating;
    }

    public void setProductRating(float productRating) {
        this.productRating = productRating;
    }
}
