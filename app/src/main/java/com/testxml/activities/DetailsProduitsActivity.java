package com.testxml.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


import com.testxml.R;
import com.testxml.adapters.DetailsProduitsAdapters;
import com.testxml.models.Panier;
import com.testxml.models.Product;
import com.testxml.outils.TypefaceUtil;
import com.testxml.outils.Utils;
import com.testxml.services.Services;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Raoelson on 23/10/2017.
 */

public class DetailsProduitsActivity extends AppCompatActivity {

    String idProduit;
    String showProduit = "";
    Services services;
    Typeface font;
    RecyclerView recycler;
    Button btnPanier;
    List<Product> product;
    DetailsProduitsAdapters adapters;
    CoordinatorLayout coordinatorLayout;
    int mNotificationsCount = 0;
    Toolbar toolbar;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_produits);
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "font/futura_book_font.ttf");
        font = Typeface.createFromAsset(getAssets(), "font/futura-bold_font.ttf");
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .CoordinatorLayout);
        idProduit = getIntent().getStringExtra("id");
        toolbar = (Toolbar) findViewById(R.id.toolbar1);
        recycler = (RecyclerView) findViewById(R.id.main_recycler1);
        btnPanier = (Button) findViewById(R.id.fab);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        services = new Services(this);
        services.deletePanierAfterHour();
        if (getIntent().getStringExtra("type") != null) {
            showProduit = getIntent().getStringExtra("type");
        }
        ChargementProduit();
        btnPanier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (!showProduit.equalsIgnoreCase("")) {
                    Intent intent = new Intent(DetailsProduitsActivity.this, WebActivity.class);
                    intent.putExtra("url", "https://triakaz.com/fr/contactez-nous");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    AjoutPanier();
                }*/
                AjoutPanier();
            }
        });
    }


    public void ChargementProduit() {
        product = services.getDetailsProduit(idProduit);
        toolbar.setTitle(product.get(0).getName());
        adapters = new DetailsProduitsAdapters(this, product, font, showProduit);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(linearLayoutManager);
        recycler.setAdapter(adapters);
    }

    public void AjoutPanier() {
        Panier panier = adapters.getPanier();
        Integer repo = services.addPanier(panier, "temp", "");
        String message = "";
        if (repo == 0) {
            message = "Produit ajouté au panier avec succès";
        } else {
            return;
        }

        showMessage(message);
        Date date = new Date();
        long tm = date.getTime() + 3600000;
        SharedPreferences sharedpreferences = PreferenceManager.
                getDefaultSharedPreferences(getApplicationContext());
        editor = sharedpreferences.edit();
        if (sharedpreferences.getString("dateAjout", null) != null) {
            editor.remove("dateAjout");
            editor.apply();
            editor.putString("dateAjout", "" + tm);
            editor.commit();
        } else {
            editor.putString("dateAjout", "" + tm);
            editor.commit();
        }
        showPanier();
    }

    @Override
    protected void onStart() {
        super.onStart();
        showPanier();

    }

    public void showPanier() {
        Services.FetchCountTask fetchCountTask = new Services.FetchCountTask(getApplicationContext());
        try {
            updateNotificationsBadge(fetchCountTask.execute().get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        //panier
        MenuItem item = menu.findItem(R.id.menu_panier);
        LayerDrawable icon = (LayerDrawable) item.getIcon();
        // Update LayerDrawable's BadgeDrawable
        Utils.setBadgeCount(this, icon, mNotificationsCount);
        return true;
    }

    private void updateNotificationsBadge(int count) {
        mNotificationsCount = count;

        // force the ActionBar to relayout its MenuItems.
        // onCreateOptionsMenu(Menu) will be called again.
        invalidateOptionsMenu();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                finish();
                return true;
            case R.id.menu_panier:
                startActivityForResult(new
                        Intent(getApplicationContext(), PanierActivity.class), 100);

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showMessage(String message) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}
