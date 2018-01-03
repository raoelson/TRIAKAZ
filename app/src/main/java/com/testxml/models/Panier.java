package com.testxml.models;

/**
 * Created by Raoelson on 24/10/2017.
 */

public class Panier {
    Integer id;
    Integer idProduit;
    Integer Qte;
    Double prix;
    String nomProduit;
    String urlImage;

    public Panier() {
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNomProduit() {
        return nomProduit;
    }

    public void setNomProduit(String nomProduit) {
        this.nomProduit = nomProduit;
    }

    public Integer getIdProduit() {
        return idProduit;
    }

    public void setIdProduit(Integer idProduit) {
        this.idProduit = idProduit;
    }

    public Integer getQte() {
        return Qte;
    }

    public void setQte(Integer qte) {
        Qte = qte;
    }

    public Double getPrix() {
        return prix;
    }

    public void setPrix(Double prix) {
        this.prix = prix;
    }
}
