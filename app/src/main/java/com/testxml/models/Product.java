package com.testxml.models;

/**
 * Created by Raoelson on 12/09/2017.
 */

public class Product {
    private Integer id_product;
    private String name;
    private Integer id_image;
    private String description_short;
    private String description;
    private Double price;
    private String id_supplier;
    private String reference;
    private String activo;
    private String available;
    private Integer id_categorie;
    private String date_upd;
    private Integer minimal_quantity;

    public Product() {
    }

    public Integer getMinimal_quantity() {
        return minimal_quantity;
    }

    public void setMinimal_quantity(Integer minimal_quantity) {
        this.minimal_quantity = minimal_quantity;
    }

    public String getDate_upd() {
        return date_upd;
    }

    public void setDate_upd(String date_upd) {
        this.date_upd = date_upd;
    }

    public Integer getId_categorie() {
        return id_categorie;
    }

    public void setId_categorie(Integer id_categorie) {
        this.id_categorie = id_categorie;
    }

    public Integer getId_product() {
        return id_product;
    }

    public void setId_product(Integer id_product) {
        this.id_product = id_product;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId_image() {
        return id_image;
    }

    public void setId_image(Integer id_image) {
        this.id_image = id_image;
    }

    public String getDescription_short() {
        return description_short;
    }

    public void setDescription_short(String description_short) {
        this.description_short = description_short;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getId_supplier() {
        return id_supplier;
    }

    public void setId_supplier(String id_supplier) {
        this.id_supplier = id_supplier;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getActivo() {
        return activo;
    }

    public void setActivo(String activo) {
        this.activo = activo;
    }

    public String getAvailable() {
        return available;
    }

    public void setAvailable(String available) {
        this.available = available;
    }
}
