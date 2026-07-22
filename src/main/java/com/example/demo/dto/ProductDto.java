package com.example.demo.dto;

public class ProductDto {
    private int id;
    private String name;
    private double price;
    private String description;
    private String category;
    private String image;
    private double rating;
    private int stock;

    public ProductDto() {}

    public ProductDto(int id, String name, double price, String description, String category, String image, double rating, int stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.category = category;
        this.image = image;
        this.rating = rating;
        this.stock = stock;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
}
