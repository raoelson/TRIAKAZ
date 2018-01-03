package com.testxml.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;


import com.testxml.R;
import com.testxml.adapters.PanierAdapters;
import com.testxml.models.Panier;
import com.testxml.services.Services;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Raoelson on 24/10/2017.
 */

public class PanierActivity extends AppCompatActivity {

    List<Panier> panierList;
    Services services;
    RecyclerView recyclerView;
    FloatingActionButton btnCommande,btnDevis;
    PanierAdapters adapters;
    TextView text_vide;
    SharedPreferences sharedPreferences;
    private FloatingActionMenu menu_principal;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panier);
        menu_principal = (FloatingActionMenu) findViewById(R.id.menu_principal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Mon panier");
        sharedPreferences = PreferenceManager.
                getDefaultSharedPreferences(getApplicationContext());
        recyclerView = (RecyclerView) findViewById(R.id.main_recycler1);
        btnCommande = (FloatingActionButton) findViewById(R.id.btnCommande);
        btnDevis = (FloatingActionButton) findViewById(R.id.btnDevis);
        text_vide = (TextView) findViewById(R.id.text_vide);
        panierList = new ArrayList<>();
        services = new Services(this);
        btnCommande.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu_principal.close(true);
                passerCommande();
            }
        });
        btnDevis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu_principal.close(true);
                Intent intent = new Intent(getBaseContext(), DeavisActivity.class);
                intent.putExtra("data", ""+services.getAllPanier());
                startActivity(intent);
            }
        });

        menu_principal.setMenuButtonShowAnimation(AnimationUtils.loadAnimation(getApplicationContext()
                , R.anim.scale_up));
        menu_principal.setMenuButtonShowAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale_down));

    }

    @Override
    protected void onStart() {
        super.onStart();
        ChargementProduit();
    }

    public void passerCommande() {

        if (adapters.getItemCount() == 0) {
            btnCommande.setEnabled(false);
        } else {
            /*if(sharedPreferences.getString("acces_id",null) !=null){
                Intent intent = new Intent(getBaseContext(),WebActivity.class);
                intent.putExtra("url","https://triakaz.com/fr/order?panier="
                        +new Gson().toJson(services.getAllPanier()));
                intent.putExtra("panier","panier");
                startActivity(intent);
                //Log.d("test"," reponses "+new Gson().toJson(services.getAllPanier()));
            }else{
                Intent intent = new Intent(getBaseContext(),WebActivity.class);
                intent.putExtra("url", ConfigurationUrl.identier);
                startActivity(intent);
                this.finish();
            }*/
            Intent intent = new Intent(getBaseContext(), WebActivity.class);
            intent.putExtra("url", "https://triakaz.com/fr/order?panier="
                    + new Gson().toJson(services.getAllPanier()));
            intent.putExtra("panier", "panier");
            startActivity(intent);
        }
    }

    public void ChargementProduit() {
        panierList = services.getAllPanier();
        //Log.d("test"," data "+Gson.
        if (panierList.size() != 0) {
            btnCommande.setEnabled(true);
        } else {
            text_vide.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
        }
        //toolbar.setTitle(product.get(0).getName());
        adapters = new PanierAdapters(PanierActivity.this, panierList, recyclerView, text_vide,"panier");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapters);
        adapters.notifyDataSetChanged();
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

            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
