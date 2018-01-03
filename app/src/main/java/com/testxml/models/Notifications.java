package com.testxml.models;

/**
 * Created by Raoelson on 06/11/2017.
 */

public class Notifications {
    Integer id;
    String title;
    String message;
    String image;
    String dateUP;
    String url;
    Integer etat;

    public Notifications() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getEtat() {
        return etat;
    }

    public void setEtat(Integer etat) {
        this.etat = etat;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDateUP() {
        return dateUP;
    }

    public void setDateUP(String dateUP) {
        this.dateUP = dateUP;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
