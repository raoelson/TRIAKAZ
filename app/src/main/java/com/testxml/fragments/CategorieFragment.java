package com.testxml.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.testxml.R;
import com.testxml.activities.ProductActivity;
import com.testxml.adapters.CategoriesAdapters;
import com.testxml.models.Categorie;
import com.testxml.models.Reponses;
import com.testxml.models.User;
import com.testxml.outils.ConfigurationUrl;
import com.testxml.outils.ItemClickListener;
import com.testxml.outils.OutilsConnexion;
import com.testxml.services.Services;
import com.testxml.sqlite.DatabaseHandler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Raoelson on 20/10/2017.
 */

public class CategorieFragment extends Fragment implements ItemClickListener {

    Context mContext;
    RecyclerView recyclerView;
    DatabaseHandler databaseHandler;
    Services services;
    OutilsConnexion connexion;
    SharedPreferences.Editor editor;
    SharedPreferences sharedpreferences;
    List<Categorie> categorieList;

    public CategorieFragment newInstance(Context context) {
        CategorieFragment mFragment = new CategorieFragment();
        this.mContext = context;
        return mFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_catogories, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.categorie_recycler);
        categorieList = new ArrayList<>();
        sharedpreferences = PreferenceManager.
                getDefaultSharedPreferences(getContext());
        editor = sharedpreferences.edit();
        services = new Services(getActivity());
        services.deletePanierAfterHour();
        connexion = new OutilsConnexion(getActivity());
        init();
        if (sharedpreferences.getString("acces_id", null) != null) {
            byte[] decodedBytes = Base64.decode(sharedpreferences.getString("acces_id", null),
                    Base64.DEFAULT);
            try {
                String text = new String(decodedBytes, "UTF-8");
                if (sharedpreferences.getString("acces_id_ancien", null) == null) {
                    initSalut(text);
                } else if (sharedpreferences.getString("acces_id_ancien", null) != null) {
                    if (!sharedpreferences.getString("acces_id_ancien", null).
                            equalsIgnoreCase(sharedpreferences.getString("acces_id", null))) {
                        initSalut(text);
                    }
                }
                //editor.putString("acces_id_ancien",uri.getQueryParameter("ckey"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return view;
    }

    public void init() {
        if (connexion.isNetworkConnected()) {
            services.getServices(ConfigurationUrl.urlCategorie, new Reponses() {
                @Override
                public void onSuccess(String result) {
                    services.addCategorieSqlite(result);
                    Chargement();
                }

                @Override
                public void onError(VolleyError err) {

                }
            });
        } else {
            Chargement();
        }

    }

    private void Chargement() {
        List<Categorie> elementSearch =  services.getAllCategories();
        for(Categorie categorie : elementSearch){
            Categorie cat = new Categorie();
            if(categorie.getNom() != null && categorie.getNom().contains("La boutique du Tri")){
                cat.setId(categorie.getId());
                cat.setNom(categorie.getNom());
                categorieList.add(cat);
            }
        }

        for(Categorie categorie : elementSearch){
            Categorie cat = new Categorie();
            if(categorie.getNom() != null && categorie.getNom().contains("Office")){
                cat.setId(categorie.getId());
                cat.setNom(categorie.getNom());
                categorieList.add(cat);
            }
        }

        for(Categorie categorie : elementSearch){
            Categorie cat = new Categorie();
            if(categorie.getNom() != null && categorie.getNom().contains("Lifestyle")){
                cat.setId(categorie.getId());
                cat.setNom(categorie.getNom());
                categorieList.add(cat);
            }
        }

        for(Categorie categorie : elementSearch){
            Categorie cat = new Categorie();
            if(categorie.getNom() != null && (categorie.getNom().contains("La boutique du Tri")
             || (categorie.getNom().contains("La boutique du Tri"))
                    || (categorie.getNom().contains("Office"))
                    || (categorie.getNom().contains("Lifestyle")))){
            }else{
                cat.setId(categorie.getId());
                cat.setNom(categorie.getNom());
                categorieList.add(cat);
            }
        }


        //categorieList = ;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        CategoriesAdapters adaptersList = new CategoriesAdapters(getContext(), categorieList);
        recyclerView.setAdapter(adaptersList);
        adaptersList.setClickListener(this);
        //recyclerView.set
    }

    @Override
    public void onClick(View view, int position) {
        Intent intent = new Intent(getContext(), ProductActivity.class);
        intent.putExtra("nom", categorieList.get(position).getNom());
        intent.putExtra("id", "" + categorieList.get(position).getId());
        getContext().startActivity(intent);
    }

    public void initSalut(String id) {
        services.getServices(ConfigurationUrl.urlUser(id), new Reponses() {
            @Override
            public void onSuccess(String result) {
                User user = getUser(result.toString());
                AffichageDialogue(user);
            }

            @Override
            public void onError(VolleyError err) {

            }
        });

    }

    public User getUser(String data) {
        Document document = Jsoup.parse(data);
        User user = new User();
        Elements elements = document.getElementsByTag("customer");
        for (int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);
            String nom = (element.child(10).text());
            String prenom = (element.child(9).text());
            user.setFirstname(nom);
            user.setLastname(prenom);
        }
        return user;
    }

    public void AffichageDialogue(User user) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialogue_pop);
        TextView txtBjr = (TextView) dialog.findViewById(R.id.textBonjour);
        TextView txtUser = (TextView) dialog.findViewById(R.id.txtUsr);
        ImageView imageView = (ImageView) dialog.findViewById(R.id.cancel_btn);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        editor.remove("acces_id_ancien");
        editor.apply();
        editor.putString("acces_id_ancien", sharedpreferences.getString("acces_id", null));
        editor.commit();
        txtUser.setText(user.getFirstname() + " " + user.getLastname());
        /*txtBjr.setTypeface(font);
        txtUser.setTypeface(font);*/
        dialog.setCancelable(false);
        dialog.show();
    }
}
