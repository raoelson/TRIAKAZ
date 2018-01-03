package com.testxml.outils;

/**
 * Created by Raoelson on 13/10/2017.
 */

public class ConfigurationUrl {
    public static final String urlCategorie = "http://triakaz.com/api/categories&filter[active]=1" +
            "&display=[id,name]&filter[level_depth]=2";
    public static final String urlAllProduit = "https://triakaz.com/api/products/" +
            "?display=[id,name,price,id_default_image,id_category_default,date_upd,description_short,minimal_quantity]&filter[active]=1";

    public static final String qui_somme = "http://triakaz.com/fr/content/6-qui-sommes-nous";
    public static final String dossier_presse = "http://triakaz.com/fr/blog/dossier-triakaz";
    public static final String video = "http://triakaz.com/fr/blog/videos-triakaz";
    public static final String photo = "http://triakaz.com/fr/blog/photos-triakaz";
    public static final String articles = "http://triakaz.com/fr/blog/pdf-triakaz";
    public static final String comptes = "https://triakaz.com/fr/login?back=my-account";
    public static final String commandes = "https://triakaz.com/fr/order";
    public static final String identier = "https://triakaz.com/fr/login";
    public static final String promotions = "http://triakaz.com/fr/prices-drop";
    public static final String new_product = "http://triakaz.com/fr/new-products";
    public static final String ventes = "http://triakaz.com/fr/best-sales";
    public static final String offices = "http://triakaz.com/img/c/3_fr.jpg";
    public static final String lifestyle = "http://triakaz.com/img/c/12_fr.jpg";
    public static final String boutique = "http://triakaz.com/c/80-category_default/la-boutique-du-tri.jpg";
    public static final String green = "http://triakaz.com/img/c/13_fr.jpg";
    public static final String fun = "http://triakaz.com/img/c/78_fr.jpg";

    public static final String urlUser(String id) {
        return "http://triakaz.com/api/customers/" + id;
    }
    public static final String urlProduit(String id,int currentPage,int TOTAL_PAGES ) {
        return "http://triakaz.com/api/products?filter[id_category_default]="
                +id+"&display=[id,name,price,id_default_image]&limit="+currentPage+","+TOTAL_PAGES;
    }


}
