package com.testxml.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.testxml.models.Categorie;
import com.testxml.models.Notifications;
import com.testxml.models.Panier;
import com.testxml.models.Product;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Raoelson on 03/10/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "triakaz.db";

    // Contacts table product
    private static final String TABLE_PRODUCT = "product";

    private static final String TABLE_CATEGORIE = "categorie";

    private static final String TABLE_PANIER = "panier";
    private static final String TABLE_PANIER_TEMP = "panier_temps";


    private static final String TABLE_NOTIFICATION = "nofications";

    private static final String KEY_ID_NOTIFICATION = "id_notification";
    private static final String KEY_TITRE_NOTIFICATION = "titre_notification";
    private static final String KEY_MESSAGE_NOTIFICATION = "message_notification";
    private static final String KEY_IMAGE_NOTIFICATION = "image_notification";
    private static final String KEY_DATE_NOTIFICATION = "date_notification";
    private static final String KEY_ETAT_NOTIFICATION = "etat_notification";
    private static final String KEY_URL_NOTIFICATION = "url_notification";

    // product Table Columns names
    private static final String KEY_ID = "id_product";
    private static final String KEY_ID_CATEGORIEP = "id_cat_prod";
    private static final String KEY_NAME = "name";
    private static final String KEY_IMAGE = "id_image";
    private static final String KEY_DESCShort = "description_short";
    private static final String KEY_DESC = "description";
    private static final String KEY_PRICE = "price";
    private static final String KEY_SUPPLIER = "id_supplier";
    private static final String KEY_REFERENCE = "reference";
    private static final String KEY_ACTIVO = "activo";
    private static final String KEY_AIVALABLE = "available";
    private static final String KEY_DATE = "datepro";
    private static final String KEY_QUANTITE_MIN = "qtemin";


    // categorie Table Columns names

    private static final String KEY_ID_CATEGORIE = "id_categorie";
    private static final String KEY_NAME_CATEGORIE = "namecategorie";

    // categorie Table Columns names

    private static final String KEY_ID_CART = "id_cart";
    private static final String KEY_ID_PPRODUIT_CART = "id_produit_cart";
    private static final String KEY_NOM_CART = "nom_produit_cart";
    private static final String KEY_QTE_CART = "quantite_cart";
    private static final String KEY_PRICE_CART = "prix_cart";
    private static final String KEY_DATE_CART = "date_cart";
    private static final String KEY_url_CART = "URL_IMAGE";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_PRODUCT + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_ID_CATEGORIEP + " INTEGER," + KEY_IMAGE + " TEXT," + KEY_DESCShort + " TEXT," + KEY_DESC + " TEXT,"
                + KEY_PRICE + " REAL," + KEY_SUPPLIER + " TEXT," + KEY_REFERENCE + " TEXT" +
                "," + KEY_ACTIVO + " TEXT," + KEY_AIVALABLE + " TEXT," + KEY_DATE + " TEXT," + KEY_QUANTITE_MIN + " INTEGER)";
        db.execSQL(CREATE_CONTACTS_TABLE);

        String CREATE_CATEGORIE_TABLE = "CREATE TABLE " + TABLE_CATEGORIE + "("
                + KEY_ID_CATEGORIE + " INTEGER PRIMARY KEY," + KEY_NAME_CATEGORIE + " TEXT )";
        db.execSQL(CREATE_CATEGORIE_TABLE);

        String CREATE_PANIER_TABLE = "CREATE TABLE " + TABLE_PANIER + "("
                + KEY_ID_CART + " INTEGER PRIMARY KEY," + KEY_ID_PPRODUIT_CART + " INTEGER," +
                "" + KEY_NOM_CART + " TEXT," + KEY_QTE_CART + " INTEGER," + KEY_PRICE_CART + " REAL,"
                + KEY_DATE_CART + " DATETIME DEFAULT CURRENT_TIMESTAMP, " + KEY_url_CART + " TEXT  )";
        db.execSQL(CREATE_PANIER_TABLE);

        String CREATE_PANIER_TABLE_TEMP = "CREATE TABLE " + TABLE_PANIER_TEMP + "("
                + KEY_ID_CART + " INTEGER PRIMARY KEY," + KEY_ID_PPRODUIT_CART + " INTEGER," +
                "" + KEY_NOM_CART + " TEXT," + KEY_QTE_CART + " INTEGER," + KEY_PRICE_CART + " REAL,"
                + KEY_DATE_CART + " DATETIME DEFAULT CURRENT_TIMESTAMP, " + KEY_url_CART + " TEXT  )";
        db.execSQL(CREATE_PANIER_TABLE_TEMP);

        String CREATE_TABLE_NOTIFICATION = "CREATE TABLE " + TABLE_NOTIFICATION + "("
                + KEY_ID_NOTIFICATION + " INTEGER PRIMARY KEY AUTOINCREMENT ," + KEY_TITRE_NOTIFICATION + " TEXT,"
                + KEY_MESSAGE_NOTIFICATION + " TEXT," +
                "" + KEY_IMAGE_NOTIFICATION + " TEXT," + KEY_ETAT_NOTIFICATION + " INTEGER ,"
                + KEY_DATE_NOTIFICATION + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + KEY_URL_NOTIFICATION + " TEXT  )";
        db.execSQL(CREATE_TABLE_NOTIFICATION);

    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANIER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANIER_TEMP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATION);
        // Create tables again
        //onCreate(db);
    }

    public int addPanier(Panier panier, String temp, String modif) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID_PPRODUIT_CART, panier.getIdProduit());
        values.put(KEY_PRICE_CART, panier.getPrix());
        values.put(KEY_NOM_CART, panier.getNomProduit());
        values.put(KEY_DATE_CART, getDateTime());
        values.put(KEY_url_CART, panier.getUrlImage());

        if (temp.equalsIgnoreCase("temp")) {
            values.put(KEY_QTE_CART, panier.getQte());
            db.insert(TABLE_PANIER_TEMP, null, values);
        }
        Integer panierWhere = this.getWherePanier("" + panier.getIdProduit());
        if (modif.equalsIgnoreCase("modification")) {
            if (panierWhere > 0) {
                Log.d("test", " message" + panier.getIdProduit() + " " +
                        panier.getPrix() + " " + panier.getNomProduit() + " " + panier.getUrlImage() + " " + panier.getQte());
                values.put(KEY_QTE_CART, panier.getQte());
                db.update(TABLE_PANIER, values, KEY_ID_PPRODUIT_CART + " = ?",
                        new String[]{String.valueOf(panier.getIdProduit())});
                db.close();
                return 0;
            }
        } else {
            if (panierWhere > 0) {
                Integer somme = panier.getQte() + panierWhere;
                values.put(KEY_QTE_CART, somme);
                db.update(TABLE_PANIER, values, KEY_ID_PPRODUIT_CART + " = ?",
                        new String[]{String.valueOf(panier.getIdProduit())});
                db.close();
                return 0;
            } else {
                values.put(KEY_QTE_CART, panier.getQte());
            }
            db.insert(TABLE_PANIER, null, values);
        }

        db.close();
        return 0;
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public Integer getWherePanier(String name) {
        Integer qte = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PANIER, new String[]{KEY_QTE_CART}, KEY_ID_PPRODUIT_CART + "=?",
                new String[]{String.valueOf(name)}, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                qte = Integer.parseInt(cursor.getString(0));

            } while (cursor.moveToNext());
        }
        return qte;
    }

    public void addProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, product.getId_product());
        values.put(KEY_ID_CATEGORIEP, product.getId_categorie());
        values.put(KEY_NAME, product.getName());
        values.put(KEY_IMAGE, product.getId_image());
        values.put(KEY_DESCShort, product.getDescription_short());
        values.put(KEY_DESC, product.getDescription());
        values.put(KEY_PRICE, product.getPrice());
        values.put(KEY_SUPPLIER, product.getId_supplier());
        values.put(KEY_REFERENCE, product.getReference());
        values.put(KEY_ACTIVO, product.getActivo());
        values.put(KEY_AIVALABLE, product.getAvailable());
        values.put(KEY_DATE, product.getDate_upd());
        values.put(KEY_QUANTITE_MIN, product.getMinimal_quantity());
        // Inserting Row
        if (this.getWhere("" + product.getName())) {
            return;
        }
        db.insert(TABLE_PRODUCT, null, values);
        db.close(); // Closing database connection
    }

    public void deleteAllProduit() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PRODUCT, null,
                null);
    }

    public void addCategorie(Categorie categorie) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID_CATEGORIE, categorie.getId());
        values.put(KEY_NAME_CATEGORIE, categorie.getNom());

        // Inserting Row
        if (this.getWhereCategorie(categorie.getNom())) {
            return;
        }
        db.insert(TABLE_CATEGORIE, null, values);
        db.close(); // Closing database connection
    }

    public Boolean getWhereCategorie(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CATEGORIE, new String[]{KEY_ID_CATEGORIE}, KEY_NAME_CATEGORIE + "=?",
                new String[]{String.valueOf(name)}, null, null, null);

        if (cursor.moveToFirst()) {
            return true;
        }
        return false;
    }

    public Boolean getWhere(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PRODUCT, new String[]{KEY_ID}, KEY_NAME + "=?",
                new String[]{String.valueOf(name)}, null, null, null);

        if (cursor.moveToFirst()) {
            return true;
        }
        return false;
    }

    public List<Product> getAllProduct(String nom, String tri, String min, String max, String search) {

        List<Product> productsList = new ArrayList<Product>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;

        if (!search.equalsIgnoreCase("")) {
            String countQuery = "SELECT id_product,name,id_image,description_short,description,price" +
                    ",id_supplier,reference,activo,available,id_cat_prod,qtemin from product WHERE id_cat_prod =" + nom + " AND name LIKE '" + search + "%' ";
            cursor = db.rawQuery(countQuery, null);
        } else {
            if (tri.equalsIgnoreCase("")) {
                String query = KEY_ID_CATEGORIEP + " = ? ";
                String queryParam = nom;
                if (!min.equalsIgnoreCase("") && !max.equalsIgnoreCase("")) {
                    query += "  AND price BETWEEN " + min + " AND " + max + "";
                }
                cursor = db.query(true, TABLE_PRODUCT, new String[]{KEY_ID,
                                KEY_NAME, KEY_IMAGE, KEY_DESCShort, KEY_DESC,
                                KEY_PRICE, KEY_SUPPLIER, KEY_REFERENCE, KEY_ACTIVO, KEY_AIVALABLE, KEY_ID_CATEGORIEP,KEY_QUANTITE_MIN}, query,
                        new String[]{queryParam}, null, null, null,
                        null);

            } else {
                String orderBy = tri + " asc";
                String query = KEY_ID_CATEGORIEP + " = ? ";
                String queryParam = nom;
                if (!min.equalsIgnoreCase("") && !max.equalsIgnoreCase("")) {
                    query += "  AND price BETWEEN " + min + " AND " + max + "";
                }
                cursor = db.query(true, TABLE_PRODUCT, new String[]{KEY_ID,
                                KEY_NAME, KEY_IMAGE, KEY_DESCShort, KEY_DESC,
                                KEY_PRICE, KEY_SUPPLIER, KEY_REFERENCE, KEY_ACTIVO, KEY_AIVALABLE, KEY_ID_CATEGORIEP,KEY_QUANTITE_MIN}, query,
                        new String[]{queryParam}, null, null, orderBy,
                        null);
            }
        }

        if (cursor.moveToFirst()) {
            do {
                Product product = new Product();
                product.setId_product(Integer.parseInt(cursor.getString(0)));
                product.setName(cursor.getString(1));
                product.setId_image(Integer.valueOf(cursor.getString(2)));
                product.setDescription_short(cursor.getString(3));
                product.setDescription(cursor.getString(4));
                product.setPrice(Double.valueOf(cursor.getString(5)));
                product.setId_supplier(cursor.getString(6));
                product.setReference(cursor.getString(7));
                product.setActivo(cursor.getString(8));
                product.setAvailable(cursor.getString(9));
                product.setId_categorie(Integer.parseInt(cursor.getString(10)));
                product.setMinimal_quantity(Integer.parseInt(cursor.getString(11)));
                productsList.add(product);
            } while (cursor.moveToNext());
        }

        // return adressesList list
        return productsList;
    }

    public List<Product> getDetailsProduit(String id) {
        List<Product> products = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PRODUCT, new String[]{KEY_ID,
                        KEY_NAME, KEY_IMAGE, KEY_DESCShort, KEY_DESC,
                        KEY_PRICE, KEY_SUPPLIER, KEY_REFERENCE, KEY_ACTIVO, KEY_AIVALABLE, KEY_ID_CATEGORIEP,KEY_QUANTITE_MIN}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Product product = new Product();
                product.setId_product(Integer.parseInt(cursor.getString(0)));
                product.setName(cursor.getString(1));
                product.setId_image(Integer.valueOf(cursor.getString(2)));
                product.setDescription_short(cursor.getString(3));
                product.setDescription(cursor.getString(4));
                product.setPrice(Double.valueOf(cursor.getString(5)));
                product.setId_supplier(cursor.getString(6));
                product.setReference(cursor.getString(7));
                product.setActivo(cursor.getString(8));
                product.setAvailable(cursor.getString(9));
                product.setId_categorie(Integer.valueOf(cursor.getString(10)));
                product.setMinimal_quantity(Integer.valueOf(cursor.getString(11)));
                products.add(product);
            } while (cursor.moveToNext());
        }
        return products;
    }

    public Integer getCountProduitAll(String nom) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = " SELECT  * FROM " + TABLE_PRODUCT + " WHERE " + KEY_ID_CATEGORIEP + " = " + nom + " ";
        /*Cursor cursor = db.query(true, TABLE_PRODUCT, new String[]{KEY_ID,
                        KEY_NAME, KEY_IMAGE, KEY_DESCShort, KEY_DESC,
                        KEY_PRICE, KEY_SUPPLIER, KEY_REFERENCE, KEY_ACTIVO, KEY_AIVALABLE}, KEY_ID_CATEGORIEP + " = ? ",
                new String[]{nom}, null, null, null,
                null);*/
        Cursor cursor = db.rawQuery(selectQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }

    public List<Categorie> getAllCategorie() {

        List<Categorie> categorieList = new ArrayList<Categorie>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from '" + TABLE_CATEGORIE + "'  ORDER BY namecategorie ASC", null);
        if (cursor.moveToFirst()) {
            do {
                Categorie categorie = new Categorie();
                categorie.setNom(cursor.getString(1));
                categorie.setId(Integer.valueOf(cursor.getString(0)));

                categorieList.add(categorie);
            } while (cursor.moveToNext());
        }

        return categorieList;
    }


    public String getMinAndMax(String nom) {
        String reponse = null;
        SQLiteDatabase db = this.getWritableDatabase();
       /* Cursor c = db.query(TABLE_PRODUCT, new String[]{"min(" + KEY_PRICE + ")", "max(" + KEY_PRICE + ")"}, KEY_ID_CATEGORIEP + " = ? ",
                new String[]{nom},
                null, null, null);
        c.moveToFirst();*/
        String countQuery = "SELECT  min(price),max(price) FROM " + TABLE_PRODUCT + " WHERE id_cat_prod = " + nom;
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.moveToNext();
        int rowID = cursor.getInt(0);
        int rowID_ = cursor.getInt(1);
        reponse = rowID + "-" + rowID_;
        return reponse;
    }

    public Integer getCountPanier() {
        SQLiteDatabase db = this.getWritableDatabase();
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        String debut = dateFormat.format(date) + " 00:00:00";
        String fin = dateFormat.format(date) + " 23:59:59";
        String countQuery = "SELECT  * FROM " + TABLE_PANIER_TEMP + " WHERE date_cart BETWEEN '" + debut + "' AND '" + fin + "'";
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.moveToNext();
        int rowID = cursor.getCount();
        return rowID;
    }

    public List<Panier> getAllPanier() {

        List<Panier> paniers = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        String debut = dateFormat.format(date) + " 00:00:00";
        String fin = dateFormat.format(date) + " 23:59:59";
       /* String countQuery = "SELECT id_cart,id_produit_cart,nom_produit_cart,SUM(quantite_cart)," +
                " prix_cart,date_cart,URL_IMAGE FROM " + TABLE_PANIER + " WHERE date_cart BETWEEN '" + debut + "' AND '" + fin + "' GROUP BY nom_produit_cart";*/
        String countQuery = "SELECT * FROM " + TABLE_PANIER + " WHERE date_cart BETWEEN '" + debut + "' AND '" + fin + "'";
        Cursor cursor = db.rawQuery(countQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Panier panier = new Panier();
                panier.setId(Integer.parseInt(cursor.getString(0)));
                panier.setIdProduit(Integer.parseInt(cursor.getString(1)));
                panier.setNomProduit((cursor.getString(2)));
                panier.setQte(Integer.parseInt(cursor.getString(3)));
                panier.setPrix(Double.valueOf(cursor.getString(4)));
                panier.setUrlImage((cursor.getString(6)));
                paniers.add(panier);
            } while (cursor.moveToNext());
        }
        return paniers;
    }

    public int deletePanier(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PANIER, KEY_ID_PPRODUIT_CART + " = ?",
                new String[]{String.valueOf(id)});
        db.delete(TABLE_PANIER_TEMP, KEY_ID_PPRODUIT_CART + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
        return 1;
    }

    public void deletePanierAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PANIER, null,
                null);
        db.delete(TABLE_PANIER_TEMP, null,
                null);
        db.close();
    }

    public List<Product> getAllProductWhere(String nom) {
        List<Product> productsList = new ArrayList<Product>();
        SQLiteDatabase db = this.getWritableDatabase();
        /*Cursor cursor = null;
        String query = KEY_NAME + " = ? ";
        String queryParam = nom;
        cursor = db.query(true, TABLE_PRODUCT, new String[]{KEY_ID,
                        KEY_NAME, KEY_IMAGE, KEY_DESCShort, KEY_DESC,
                        KEY_PRICE, KEY_SUPPLIER, KEY_REFERENCE, KEY_ACTIVO, KEY_AIVALABLE}, null,
               null, null, null, null,
                null);*/

        String countQuery = "SELECT id_product,name,id_image,description_short,description,price" +
                ",id_supplier,reference,activo,available,id_cat_prod from product WHERE name LIKE '" + nom + "%' ";
        Cursor cursor = db.rawQuery(countQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Product product = new Product();
                product.setId_product(Integer.parseInt(cursor.getString(0)));
                product.setName(cursor.getString(1));
                product.setId_image(Integer.valueOf(cursor.getString(2)));
                product.setDescription_short(cursor.getString(3));
                product.setDescription(cursor.getString(4));
                product.setPrice(Double.valueOf(cursor.getString(5)));
                product.setId_supplier(cursor.getString(6));
                product.setReference(cursor.getString(7));
                product.setActivo(cursor.getString(8));
                product.setAvailable(cursor.getString(9));
                product.setDate_upd(getNomCategorie(cursor.getString(10)).getNom());
                productsList.add(product);
            } while (cursor.moveToNext());
        }

        // return adressesList list
        return productsList;
    }

    public Categorie getNomCategorie(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Categorie categorie = new Categorie();
        String countQuery = "SELECT namecategorie,id_categorie from categorie WHERE id_categorie = '" + id + "' ";
        Cursor cursor = db.rawQuery(countQuery, null);
        if (cursor.moveToFirst()) {
            do {
                categorie.setNom(cursor.getString(0));
                categorie.setId(Integer.parseInt(cursor.getString(1)));
            } while (cursor.moveToNext());
        }
        return categorie;
    }

    public void addNotification(Notifications notifications) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TITRE_NOTIFICATION, notifications.getTitle());
        values.put(KEY_MESSAGE_NOTIFICATION, notifications.getMessage());
        values.put(KEY_IMAGE_NOTIFICATION, notifications.getImage());
        values.put(KEY_ETAT_NOTIFICATION, notifications.getEtat());
        values.put(KEY_DATE_NOTIFICATION, getDateTime());
        values.put(KEY_URL_NOTIFICATION, notifications.getUrl());
        db.insert(TABLE_NOTIFICATION, null, values);
        db.close(); // Closing database connection
    }

    public Integer getCountNotification() {
        SQLiteDatabase db = this.getWritableDatabase();
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        String debut = dateFormat.format(date) + " 00:00:00";
        String fin = dateFormat.format(date) + " 23:59:59";
        String countQuery = "SELECT  * FROM " + TABLE_NOTIFICATION + " WHERE date_notification BETWEEN '" + debut + "' AND '" + fin + "' " +
                " AND etat_notification=0 ";
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.moveToNext();
        int rowID = cursor.getCount();
        return rowID;
    }

    public List<Notifications> getAllNotifications() {
        List<Notifications> notificationsList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        String debut = dateFormat.format(date) + " 00:00:00";
        String fin = dateFormat.format(date) + " 23:59:59";
        String countQuery = "SELECT  * FROM " + TABLE_NOTIFICATION + " WHERE date_notification BETWEEN " +
                "'" + debut + "' AND '" + fin + "' " +
                " ORDER BY id_notification desc";
        Cursor cursor = db.rawQuery(countQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Notifications notifications = new Notifications();
                notifications.setId(Integer.parseInt(cursor.getString(0)));
                notifications.setTitle(cursor.getString(1));
                notifications.setMessage((cursor.getString(2)));
                notifications.setImage(cursor.getString(3));
                notifications.setEtat(Integer.parseInt(cursor.getString(4)));
                notifications.setDateUP((cursor.getString(5)));
                notifications.setUrl((cursor.getString(6)));
                notificationsList.add(notifications);
            } while (cursor.moveToNext());
        }
        return notificationsList;
    }

    public void updateAllNotifications() {
        List<Notifications> notificationsList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        String debut = dateFormat.format(date) + " 00:00:00";
        String fin = dateFormat.format(date) + " 23:59:59";
        String countQuery = "SELECT  * FROM " + TABLE_NOTIFICATION + " WHERE date_notification BETWEEN " +
                "'" + debut + "' AND '" + fin + "' " +
                " ORDER BY id_notification desc";
        Cursor cursor = db.rawQuery(countQuery, null);

        if (cursor.moveToFirst()) {
            do {
                /*Notifications notifications = new Notifications();
                notifications.setId(Integer.parseInt(cursor.getString(0)));
                notifications.setTitle(cursor.getString(1));
                notifications.setMessage((cursor.getString(2)));
                notifications.setImage(cursor.getString(3));
                notifications.setEtat(Integer.parseInt(cursor.getString(4)));
                notifications.setDateUP((cursor.getString(5)));
                notificationsList.add(notifications);*/
                DataUpdate(cursor.getString(0));

            } while (cursor.moveToNext());
        }
    }

    public void DataUpdate(String id) {
        ContentValues values = new ContentValues();
        SQLiteDatabase db = this.getWritableDatabase();
        values.put(KEY_ETAT_NOTIFICATION, 1);
        db.update(TABLE_NOTIFICATION, values, KEY_ID_NOTIFICATION + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
    }

    public int deleteNotification(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NOTIFICATION, KEY_ID_NOTIFICATION + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
        return 1;
    }

    public Integer getQteMin(Integer nom) {
        String reponse = null;
        SQLiteDatabase db = this.getWritableDatabase();
        String countQuery = "SELECT  qtemin FROM " + TABLE_PRODUCT + " WHERE id_product = " + nom;
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.moveToNext();
        return cursor.getInt(0);
    }

    /*public GeoAdresse getGeoAdresse(Integer id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_ADRESSE, new String[] {KEY_CHEMIN,KEY_SCORE }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null,null,null);
        if (cursor != null)
            cursor.moveToFirst();
        GeoAdresse geoAdresse = new GeoAdresse();
        geoAdresse.setChemin(cursor.getString(0));
        // return contact
        return geoAdresse;
    }*/
}