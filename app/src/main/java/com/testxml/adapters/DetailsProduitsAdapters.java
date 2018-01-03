package com.testxml.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.testxml.R;
import com.testxml.models.Panier;
import com.testxml.models.Product;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.mostafaaryan.transitionalimageview.TransitionalImageView;
import com.mostafaaryan.transitionalimageview.model.TransitionalImage;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

import okhttp3.Authenticator;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Route;

/**
 * Created by Raoelson on 12/09/2017.
 */

public class DetailsProduitsAdapters extends RecyclerView.Adapter<DetailsProduitsAdapters.ViewHolder> {

    // region Member Variables
    private Context mContext;
    private List<Product> productList;
    private Typeface font;
    private int mQuantity = 0;
    private int mQuantityDepart = 0;
    private String noShow;


    public DetailsProduitsAdapters(Context context, List<Product> prod, Typeface font, String noShow) {
        mContext = context;
        productList = prod;
        this.font = font;
        this.noShow = noShow;
        // endregion
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.content_details, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Product product = productList.get(position);
        if (!noShow.equalsIgnoreCase("")) {
            mQuantityDepart = product.getMinimal_quantity();
        }
        String text1 = "<b>&#149;</b> Les produits sont à récupérer en nos locaux : " +
                " <b>TRIAKAZ, 3 Immeuble MAHOGANY – Voie Verte – ZI de JARRY – 97122- BAIE-MAHAULT</b>" +
                ".";
        holder.textView1.setText(Html.fromHtml(text1));
        String text2 = "<b>&#149;</b> Notre boutique est ouverte du Lundi au Vendredi, de 8h à 12h et de 14h à 16h.";
        holder.textView2.setText(Html.fromHtml(text2));

        String text3 = "<b>&#149;</b> Pour toute demande de livraison par voie postale à votre domicile, nous contacter pour  <br/>" +
                "&nbsp;une demande de devis : <b> contact@triakaz.com </b>" +
                "&nbsp;ou <b>+590 590 94 56 12</b> ";
        holder.textView3.setText(Html.fromHtml(text3));
        holder.NomProduit.setText(product.getName());
        holder.NomProduit.setTypeface(font);
        holder.descProduit.setText(Html.fromHtml(product.getDescription_short()));
        holder.price.setText("" + product.getPrice() + "€");
        if (product.getMinimal_quantity() == 0) {
            holder.quantity_text_view.setText("1");
        } else {
            holder.quantity_text_view.setText("" + product.getMinimal_quantity());
        }

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                            .authenticator(new Authenticator() {
                                @Override
                                public Request authenticate(Route route, okhttp3.Response response) throws IOException {
                                    String credentials = "VG1GA74ZSPV861V76R79F6IERER129GE";
                                    credentials = credentials + ":";
                                    String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                                    //String credential = "";
                                    return response.request().newBuilder()
                                            .header("Authorization", "Basic " + base64EncodedCredentials)
                                            .build();
                                }
                            })
                            .build();
                    Picasso picasso = new Picasso.Builder(mContext)
                            .downloader(new OkHttp3Downloader(okHttpClient))
                            .build();
                    final Bitmap bitmap = picasso.load(product.getActivo()).get();
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TransitionalImage transitionalImage = new TransitionalImage.Builder()
                                    .duration(500)
                            /*.backgroundColor(ContextCompat.getColor(, R.color.colorAccent))*/
                                    /*.image(R.drawable.sample_image)*/
                                    .image(bitmap)
                                    .create();
                            holder.image.setTransitionalImage(transitionalImage);
                            bitmap.recycle();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        mQuantity = Integer.parseInt(holder.quantity_text_view.getText().toString());
        holder.increment_qte_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mQuantity = mQuantity + 1;
                holder.quantity_text_view.setText("" + mQuantity);
               /* ShowHide(Integer.parseInt(holder.quantity_text_view.getText().toString()),
                        holder.decrement_qte_button);*/
            }
        });

        holder.decrement_qte_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mQuantityDepart == 0) {
                    if (mQuantity > 1) {
                        mQuantity = mQuantity - 1;
                        holder.quantity_text_view.setText("" + (mQuantity));
                        /*ShowHide(Integer.parseInt(holder.quantity_text_view.getText().toString()),
                                holder.decrement_qte_button);*/
                    }
                } else {
                    if (mQuantity > mQuantityDepart) {
                        mQuantity = mQuantity - 1;
                        holder.quantity_text_view.setText("" + (mQuantity));
                        /*ShowHide(Integer.parseInt(holder.quantity_text_view.getText().toString()),
                                holder.decrement_qte_button);*/
                    }
                }

            }
        });

    }

    /*public void ShowHide(int action, TextView button) {
        if (mQuantityDepart == 0) {
            if (action == 1) {
                button.setVisibility(View.INVISIBLE);
            } else {
                button.setVisibility(View.VISIBLE);
            }
        } else {
            if (action > mQuantityDepart) {
                button.setVisibility(View.VISIBLE);
            } else {
                button.setVisibility(View.INVISIBLE);
            }
        }
    }*/

    public Panier getPanier() {
        Panier stringList = new Panier();
        stringList.setQte(mQuantity);
        stringList.setNomProduit(productList.get(0).getName());
        stringList.setPrix(productList.get(0).getPrice());
        stringList.setIdProduit(productList.get(0).getId_product());
        stringList.setUrlImage(productList.get(0).getActivo());
        return stringList;
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView1, textView2, textView3,
                NomProduit, descProduit, price,
                quantity_text_view, decrement_qte_button;
        TransitionalImageView image;
        Button increment_qte_button;
        LinearLayout test, LinearPrice;

        public ViewHolder(View view) {
            super(view);
            textView1 = (TextView) view.findViewById(R.id.textView1);
            textView2 = (TextView) view.findViewById(R.id.textView2);
            textView3 = (TextView) view.findViewById(R.id.textView3);
            NomProduit = (TextView) view.findViewById(R.id.NomProduit);
            descProduit = (TextView) view.findViewById(R.id.descProduit);
            quantity_text_view = (TextView) view.findViewById(R.id.quantity_text_view);
            decrement_qte_button = (TextView) view.findViewById(R.id.decrement_qte_button);
            price = (TextView) view.findViewById(R.id.price);
            image = (TransitionalImageView) view.findViewById(R.id.image);
            increment_qte_button = (Button) view.findViewById(R.id.increment_qte_button);
            test = (LinearLayout) view.findViewById(R.id.test);
            LinearPrice = (LinearLayout) view.findViewById(R.id.LinearPrice);
        }

    }
}
