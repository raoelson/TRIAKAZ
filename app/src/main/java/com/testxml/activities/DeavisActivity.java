package com.testxml.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.testxml.R;
import com.testxml.adapters.PanierAdapters;
import com.testxml.models.Panier;
import com.testxml.models.Reponses;
import com.testxml.services.Services;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Raoelson on 08/11/2017.
 */

public class DeavisActivity extends AppCompatActivity {
    List<Panier> panierList;
    Services services;
    RecyclerView recyclerView;
    PanierAdapters adapters;
    EditText edtNom, edtPrenom, edtEmail, edtSociete, edtMessage;
    ProgressDialog dialog = null;
    TextView text_vide;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devis);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Demande de devis");
        recyclerView = (RecyclerView) findViewById(R.id.main_recycler1);
        text_vide = (TextView) findViewById(R.id.text_vide);
        edtMessage = (EditText) findViewById(R.id.edtMessage);
        edtNom = (EditText) findViewById(R.id.edtNom);
        edtPrenom = (EditText) findViewById(R.id.edtPrenom);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtSociete = (EditText) findViewById(R.id.edtSociete);
        panierList = new ArrayList<>();
        services = new Services(this);
        dialog = new ProgressDialog(this);
        ChargementProduit();
    }
    public void Show(){
        dialog.setTitle("TRIAKAZ");
        dialog.setMessage("Envoi du demande de devis en cours...");
        dialog.setIndeterminate(true);
        dialog.show();
    }
    public void Dismiss(){
        dialog.dismiss();
    }

    public void ChargementProduit() {
        panierList = services.getAllPanier();
        if (panierList.size() == 0) {
            text_vide.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
        }
        //toolbar.setTitle(product.get(0).getName());
        adapters = new PanierAdapters(DeavisActivity.this, panierList, recyclerView, text_vide, "");
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.content_panier, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                finish();
                return true;
            case R.id.main_envoyer:
                Show();
                EnvoieMail();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void EnvoieMail() {
        List<Panier> panierList = services.getAllPanier();
        if (!validate()) {
            Dismiss();
            return;
        }
        if (panierList.size() > 0) {
            services.getServicePost(edtNom.getText().toString().toUpperCase(), edtPrenom.getText().toString(),
                    edtEmail.getText().toString(), edtSociete.getText().toString(),
                    edtMessage.getText().toString(),new Reponses() {
                        @Override
                        public void onSuccess(String result) {
                           if(result.equalsIgnoreCase("1")){
                               Dismiss();
                               Toast.makeText(getApplicationContext(),
                                       "Votre demande de devis a été envoyé avec succès",Toast.LENGTH_SHORT).show();
                               finish();
                           }
                        }

                        @Override
                        public void onError(VolleyError err) {
                            Dismiss();
                            Log.d("test", " error " + err.getMessage().toString());
                        }
                    });
        }else {
            finish();
        }
        return;
        /*Log.d("test"," commande "+ new Gson().toJson(panierList));*/
    }

    public boolean validate() {
        boolean valid = true;
        String email = edtEmail.getText().toString();
        String _nom = edtNom.getText().toString();
        String _prenom = edtPrenom.getText().toString();
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Entrez une adresse mail valide");
            valid = false;
        } else {
            edtEmail.setError(null);
        }

        if (_nom.isEmpty()) {
            edtNom.setError("Veuillez remplir ce champs svp");
            valid = false;
        } else {
            edtNom.setError(null);
        }
        if (_prenom.isEmpty()) {
            edtPrenom.setError("Veuillez remplir ce champs svp");
            valid = false;
        } else {
            edtPrenom.setError(null);
        }

        return valid;
    }
}
