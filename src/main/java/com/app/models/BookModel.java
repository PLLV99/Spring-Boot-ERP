package com.app.models;

// This class represents a model for a Book entity with attributes id, name, and price.
public class BookModel {
    // Unique identifier for the book
    private String id;
    // Name of the book
    private String name;
    // Price of the book
    private Integer price;

    // Constructor to initialize the BookModel object with id, name, and price
    public BookModel(String id, String name, Integer price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    // Getter method for id
    public String getId() {
        return id;
    }

    // Getter method for name
    public String getName() {
        return name;
    }

    // Getter method for price
    public Integer getPrice() {
        return price;
    }

    // Setter method for id
    public void setId(String id) {
        this.id = id;
    }

    // Setter method for name
    public void setName(String name) {
        this.name = name;
    }

    // Setter method for price
    public void setPrice(Integer price) {
        this.price = price;
    }
}