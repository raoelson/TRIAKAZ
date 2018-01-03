package com.testxml.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.testxml.R;
import com.testxml.adapters.ProductAdapters;
import com.testxml.models.Product;
import com.testxml.services.Services;

import java.util.List;

/**
 * Created by Raoelson on 18/09/2017.
 */

public class ProduitFragment extends Fragment {

    private static final String id_search = "id_search";
    TextView text_vide;
    RecyclerView main_recycler_produit;
    Services services;


    public ProduitFragment newInstance(String  text) {
        ProduitFragment mFragment = new ProduitFragment();
        Bundle mBundle = new Bundle();
        mBundle.putString(id_search, String.valueOf(text));
        mFragment.setArguments(mBundle);
        return mFragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_produit, container, false);
        text_vide = (TextView) view.findViewById(R.id.text_vide);
        main_recycler_produit = (RecyclerView) view.findViewById(R.id.main_recycler_produit);
        services = new Services(getContext());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        chargement();
    }

    private void chargement() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        main_recycler_produit.setLayoutManager(linearLayoutManager);
        List<Product> productList = services.getAllProductWhere(getArguments().getString(id_search));
        ProductAdapters adaptersList = new ProductAdapters(getContext(), productList, "");
        main_recycler_produit.setAdapter(adaptersList);
        if(productList.size() ==0){
            text_vide.setVisibility(View.VISIBLE);
        }else{
            text_vide.setVisibility(View.GONE);
        }
    }

}
