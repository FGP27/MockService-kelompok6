package com.example.demo.dto;

public class OrderRequest {
    private String product;
    private int quantity;
    private double price;
    private String customerName;
    private String address;
    private String paymentMethod;

    public OrderRequest() {
    }

    public OrderRequest(String product, int quantity, double price, String customerName, String address, String paymentMethod) {
        this.product = product;
        this.quantity = quantity;
        this.price = price;
        this.customerName = customerName;
        this.address = address;
        this.paymentMethod = paymentMethod;
    }

    public String getProduct() { return product; }
    public void setProduct(String product) { this.product = product; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
}
