package com.testxml.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.testxml.models.Categorie;
import com.testxml.models.Notifications;
import com.testxml.models.Panier;
import com.testxml.models.Product;
import com.testxml.models.Reponses;
import com.testxml.sqlite.DatabaseHandler;
import com.google.gson.Gson;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * Created by Raoelson on 13/10/2017.
 */

public class Services {
    static Context context;
    DatabaseHandler db;

    public Services(Context mContext) {

        this.context = mContext;
    }


    public Map<String, String> getHeader() {
        Map<String, String> headers = new HashMap<>();
        String credentials = "VG1GA74ZSPV861V76R79F6IERER129GE";
        credentials = credentials + ":";
        String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "Basic " + base64EncodedCredentials);
        return headers;
    }

    public void getServices(String url, final Reponses callback) {

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                callback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return super.getParams();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return getHeader();
            }
        };
        requestQueue.add(stringRequest);
    }

    public void getServicePost(final String nom
            ,final String prenom,final String email,final String societe,final String message,final Reponses callback) {
        String url = "http://triakaz.com/webservices/triakaz.php";
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("test", " error " + error.getMessage().toString());
                        // error
                        //Log.d("Error.Response", response);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("data", new Gson().toJson(getAllPanier()));
                params.put("nom", nom);
                params.put("nom", nom);
                params.put("prenom", prenom);
                params.put("email", email);
                params.put("message", message);
                params.put("societe", societe);

                return params;
            }
        };
        queue.add(postRequest);
    }

    public void addCategorieSqlite(String reponse) {
        this.db = new DatabaseHandler(context);
        Document document = Jsoup.parse(reponse);
        Elements elements = document.getElementsByTag("category");
        for (int i = 0; i < elements.size(); i++) {
            Categorie categorie = new Categorie();
            Element element = elements.get(i);
            Integer id = Integer.parseInt(element.child(0).text());
            String value = element.child(1).child(1).text();
            categorie.setId(id);
            categorie.setNom(value);
            db.addCategorie(categorie);
            // adapter.addFrag(new testFragment(), value);
        }
    }

    public List<Categorie> getAllCategories() {
        this.db = new DatabaseHandler(context);
        return db.getAllCategorie();
    }

    public List<Product> getAllProduit(String id, String tri, String min, String max, String search) {
        this.db = new DatabaseHandler(context);
        return db.getAllProduct(id, tri, min, max, search);
    }

    public Integer getCountProduitAll(String id) {
        this.db = new DatabaseHandler(context);
        return db.getCountProduitAll(id);
    }

    public void addProduit(String reponse) {
        this.db = new DatabaseHandler(context);
        Integer id_image = 0;
        Document document = Jsoup.parse(reponse);
        Elements elements = document.getElementsByTag("product");
        for (int i = 0; i < elements.size(); i++) {

            Product product = new Product();
            Element element = elements.get(i);
            Integer id = Integer.parseInt(element.child(0).text());
            Integer id_category = Integer.parseInt(element.child(1).text());
            if (!element.child(2).text().equalsIgnoreCase("")) {
                id_image = Integer.parseInt(element.child(2).text());
            }
            Integer qtemin = Integer.parseInt(element.child(3).text());
            Double prix = Double.parseDouble(element.child(4).text());
            product.setDate_upd(element.child(5).text());
            String nom = element.child(6).child(1).text();
            String descption = element.child(7).child(1).text();
            product.setDescription_short(descption);
            product.setId_product(id);
            product.setId_categorie(id_category);
            product.setActivo("http://VG1GA74ZSPV861V76R79F6IERER129GE@triakaz.com/api/images/products/" + id + "/" + id_image);
            product.setPrice(prix);
            product.setName(nom);
            product.setMinimal_quantity(qtemin);
            product.setId_image(id_image);

            //product.setAvailable(getArguments().getString(value_search));
            db.addProduct(product);
        }
        //return products;
    }
    public void deleteAllProduit(){
        this.db = new DatabaseHandler(context);
        this.db.deleteAllProduit();
    }

    public String getMinAndMax(String nom) {
        this.db = new DatabaseHandler(context);
        return db.getMinAndMax(nom);
    }

    public List<Product> getDetailsProduit(String id) {
        this.db = new DatabaseHandler(context);
        return db.getDetailsProduit(id);
    }

    public Integer addPanier(Panier panier, String temp, String modif) {
        this.db = new DatabaseHandler(context);
        return db.addPanier(panier, temp, modif);
    }

    public List<Panier> getAllPanier() {
        this.db = new DatabaseHandler(context);
        /*String json = new Gson().toJson(db.getAllPanier());
        Log.d("test", " gson" + json);*/
        return db.getAllPanier();
    }

    public int deletePanier(String id) {
        this.db = new DatabaseHandler(context);
        return db.deletePanier(id);
    }


    public static class FetchCountTask extends AsyncTask<Void, Void, Integer> {
        Context mContext;

        public FetchCountTask(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            return this.getCountPanier();
        }

        private Integer getCountPanier() {
            DatabaseHandler db = new DatabaseHandler(mContext);
            return db.getCountPanier();
        }

    }

    public void deletePanierAfterHour() {
        SharedPreferences sharedpreferences = PreferenceManager.
                getDefaultSharedPreferences(context);
        if (sharedpreferences.getString("dateAjout", null) != null) {
            Date date = new Date();
            long dateAjout = Long.parseLong(sharedpreferences.getString("dateAjout", null));
            if (dateAjout <= date.getTime()) {
                DatabaseHandler db = new DatabaseHandler(context);
                db.deletePanierAll();
            } else {
                return;
            }
        }

        if (sharedpreferences.getString("dateAjoutUser", null) != null) {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            Date date = new Date();
            long dateAjout_ = Long.parseLong(sharedpreferences.getString("dateAjoutUser", null));
            if (dateAjout_ <= date.getTime()) {
                editor.remove("acces_id");
                editor.remove("dateAjoutUser");
                editor.apply();
            } else {
                return;
            }
        }
        this.db = new DatabaseHandler(context);
        if(db.getCountNotification() == 0){
            ShortcutBadger.removeCount(context);
        }

    }

    public List<Product> getAllProductWhere(String id) {
        this.db = new DatabaseHandler(context);
        return db.getAllProductWhere(id);
    }

    public Integer addNotification(Notifications notifications){
        this.db = new DatabaseHandler(context);
        db.addNotification(notifications);
        return 1;
    }

    public Integer getCountNotification(){
        this.db = new DatabaseHandler(context);
        return db.getCountNotification();
        //return 1;
    }

    public List<Notifications> getAllNotification(){
        this.db = new DatabaseHandler(context);
        return db.getAllNotifications();
    }

    public void updateAllNotifications(){
        this.db = new DatabaseHandler(context);
        db.updateAllNotifications();
    }
    public int deleteNotification(String id) {
        this.db = new DatabaseHandler(context);
        return db.deleteNotification(id);
    }


    public static void deleteCache() {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    public Integer getQteMin(Integer id){
        this.db = new DatabaseHandler(context);
        return  db.getQteMin(id);
    }

}
