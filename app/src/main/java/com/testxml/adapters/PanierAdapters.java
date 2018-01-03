package com.testxml.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;

import com.testxml.R;
import com.testxml.models.Panier;
import com.testxml.services.Services;

import java.util.List;

/**
 * Created by Raoelson on 12/09/2017.
 */

public class PanierAdapters extends RecyclerView.Adapter<PanierAdapters.ViewHolder> {

    // region Member Variables
    private Context mContext;
    List<Panier> paniers;
    Services services;
    View v;
    RecyclerView recyclerView;
    TextView textView;
    String action = "";
    Integer mQuantity = 0;


    public PanierAdapters(Context context, List<Panier> paniers, RecyclerView recyclerView, TextView textView,
                          String action) {
        mContext = context;
        this.paniers = paniers;
        this.recyclerView = recyclerView;
        this.textView = textView;
        this.action = action;
        this.services = new Services(mContext);
        // endregion
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_panier, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final Panier panier = paniers.get(position);
        holder.txtName.setText(panier.getNomProduit());
        holder.txtPrix.setText("" + panier.getPrix() + "€");
        holder.quantity_text_view.setText("" + panier.getQte());
        holder.totalpanier.setText("Total : " + TotalPrix(panier.getPrix(), Double.valueOf(panier.getQte())) + "€");
        if (action.equalsIgnoreCase("panier")) {
            holder.linerQteEdt.setVisibility(View.GONE);
        } else {
            holder.linerQte.setVisibility(View.GONE);
            holder.linerQteEdt.setVisibility(View.VISIBLE);
            holder.quantity_edit_view.setText("" + panier.getQte());
            //mQuantity = Integer.parseInt(holder.quantity_edit_view.getText().toString());
        }
        mQuantity = services.getQteMin(paniers.get(position).getIdProduit());
        if(mQuantity == 0){
            mQuantity =1;
        }
       /* ShowHide(Integer.parseInt(holder.quantity_text_view.getText().toString()),
                holder.decrement_qte_button);*/
        holder.deletePanier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePanier(position);
            }
        });
        Glide.with(mContext)
                .load(getUrlWithHeaders(panier.getUrlImage())).into(holder.image);
        holder.increment_qte_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qteNew = 0;
                qteNew = (Integer.parseInt(holder.quantity_text_view.getText().toString()) + 1);
                holder.quantity_text_view.setText("" + qteNew);
                modifactionPanier(panier, qteNew);
                holder.totalpanier.setText("Total : " + TotalPrix(panier.getPrix(), Double.valueOf(qteNew)) + "€");
                /*ShowHide(Integer.parseInt(holder.quantity_text_view.getText().toString()),
                        holder.decrement_qte_button);*/
            }
        });

        holder.decrement_qte_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qteMin = services.getQteMin(paniers.get(position).getIdProduit());
                if (qteMin == 0) {
                    qteMin = 1;
                }
                int qteOld = 0;
                qteOld = (Integer.parseInt(holder.quantity_text_view.getText().toString()));
                if (qteOld > qteMin) {
                    holder.quantity_text_view.setText("" + (qteOld - 1));
                    modifactionPanier(panier, (qteOld - 1));
                    holder.totalpanier.setText("Total : " + TotalPrix(panier.getPrix(), Double.valueOf((qteOld - 1))) + "€");
                   /* ShowHide(Integer.parseInt(holder.quantity_text_view.getText().toString()),
                            holder.decrement_qte_button);*/
                }
            }
        });

        holder.quantity_edit_view.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() > 0) {
                    String result = s.toString().replaceAll(" ", "");
                    Integer qte = Integer.parseInt(result.toString());
                    //modifactionPanier(panier,qte);
                    holder.totalpanier.setText("Total : " + TotalPrix(panier.getPrix(), Double.valueOf(qte)) + "€");
                } else {
                    holder.totalpanier.setText("Total : " + TotalPrix(panier.getPrix(), Double.valueOf(0)) + "€");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        holder.quantity_edit_view.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    String qteString = holder.quantity_edit_view.getText().toString();
                    int qteMin = services.getQteMin(paniers.get(position).getIdProduit());
                    if (qteMin == 0) {
                        qteMin = 1;
                    }
                    if (qteString.equalsIgnoreCase("0") || qteString.equalsIgnoreCase("")) {
                        holder.quantity_edit_view.setText("" + qteMin);
                        holder.quantity_edit_view.setSelection(holder.quantity_edit_view.getText().toString().length());
                        holder.totalpanier.setText("Total : " + TotalPrix(panier.getPrix(), Double.valueOf(qteMin)) + "€");
                        Toast.makeText(mContext, "Veuillez entrer une quantité minimale de " + qteMin, Toast.LENGTH_LONG).show();
                    } else {
                        int qteNew = Integer.parseInt(qteString);
                        if (qteNew >= qteMin) {
                            modifactionPanier(panier, qteNew);
                            holder.totalpanier.setText("Total : " + TotalPrix(panier.getPrix(), Double.valueOf(qteNew)) + "€");
                        } else {
                            holder.quantity_edit_view.setText("" + qteMin);
                            holder.quantity_edit_view.setSelection(holder.quantity_edit_view.getText().toString().length());
                            holder.totalpanier.setText("Total : " + TotalPrix(panier.getPrix(), Double.valueOf(qteMin)) + "€");
                            Toast.makeText(mContext, "Veuillez entrer une quantité minimale de " + qteMin, Toast.LENGTH_LONG).show();
                        }
                    }
                }
                return false;
            }
        });
    }

   /* public void ShowHide(int action, TextView button) {
        if (action > mQuantity) {
            button.setVisibility(View.VISIBLE);
        } else {
            button.setVisibility(View.INVISIBLE);
        }

    }*/

    private Double TotalPrix(Double prix, Double qte) {
        return round((prix * qte), 2);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public void modifactionPanier(Panier panier, int qte) {
        Panier panierModif = new Panier();
        panierModif.setId(panier.getId());
        panierModif.setNomProduit(panier.getNomProduit());
        panierModif.setPrix(panier.getPrix());
        panierModif.setQte(qte);
        panierModif.setIdProduit(panier.getIdProduit());
        panierModif.setUrlImage(panier.getUrlImage());
        services.addPanier(panierModif, "", "modification");
    }

    public void deletePanier(final int position) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                mContext);

        alertDialogBuilder.setIcon(R.drawable.ic_delete_forever_black_24dp);
        alertDialogBuilder.setTitle("Message de confirmation");
        alertDialogBuilder.setMessage("Voulez-vous vraiment supprimer cet produit ?");
        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OUI", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        int reponses = services.deletePanier("" + paniers.get(position).getIdProduit());
                        if (reponses == 1) {
                            paniers.remove(position);
                            notifyDataSetChanged();
                            if (paniers.size() == 0) {
                                Log.d("test", " textb " + textView);
                                shwHide();
                                //
                            }
                        }
                    }
                })
                .setNegativeButton("NON", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    GlideUrl getUrlWithHeaders(String url) {
        String credentials = "VG1GA74ZSPV861V76R79F6IERER129GE";
        credentials = credentials + ":";
        String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
        return new GlideUrl(url, new LazyHeaders.Builder()
                .addHeader("Authorization", "Basic " + base64EncodedCredentials)
                .build());
    }

    @Override
    public int getItemCount() {
        return paniers.size();
    }

    public void shwHide() {
        if (paniers.size() == 0) {
            textView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
        } else {
            textView.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtPrix, quantity_text_view, totalpanier;
        ImageView deletePanier, image;
        Button increment_qte_button, decrement_qte_button;
        LinearLayout linerQte, linerQteEdt;
        EditText quantity_edit_view;

        public ViewHolder(View view) {
            super(view);
            txtName = (TextView) view.findViewById(R.id.txtName);
            txtPrix = (TextView) view.findViewById(R.id.txtPrix);
            totalpanier = (TextView) view.findViewById(R.id.totalpanier);
            quantity_text_view = (TextView) view.findViewById(R.id.quantity_text_view);
            deletePanier = (ImageView) view.findViewById(R.id.deletePanier);
            image = (ImageView) view.findViewById(R.id.image);
            decrement_qte_button = (Button) view.findViewById(R.id.decrement_qte_button);
            increment_qte_button = (Button) view.findViewById(R.id.increment_qte_button);
            quantity_edit_view = (EditText) view.findViewById(R.id.quantity_edit_view);
            linerQteEdt = (LinearLayout) view.findViewById(R.id.linerQteEdt);
            linerQte = (LinearLayout) view.findViewById(R.id.linerQte);
            quantity_edit_view.setSelection(quantity_edit_view.getText().length());
        }

    }
}