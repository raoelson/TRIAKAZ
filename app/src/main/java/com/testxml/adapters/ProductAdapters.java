package com.testxml.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;

import com.testxml.R;
import com.testxml.activities.DetailsProduitsActivity;
import com.testxml.activities.WebActivity;
import com.testxml.models.Product;

import java.util.List;

/**
 * Created by Raoelson on 12/09/2017.
 */

public class ProductAdapters extends RecyclerView.Adapter<ProductAdapters.ViewHolder> {

    // region Member Variables
    private Context mContext;
    private List<Product> productList;
    static Typeface fontBold, font;
    String activeTabs;

    //String url_Image = null;
    public ProductAdapters(Context context, List<Product> products, String mactive) {
        mContext = context;
        productList = products;
        this.activeTabs = mactive;
        fontBold = Typeface.createFromAsset(context.getAssets(), "font/futura-bold_font.ttf");
        font = Typeface.createFromAsset(context.getAssets(), "font/futura_book_font.ttf");
        // endregion
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Product product = productList.get(position);
        setUpDisplayNom(holder.tvName, product);
        setUpDisplayPrix(holder.tvPrix, product);
        Glide.with(mContext)
                .load(getUrlWithHeaders(product.getActivo())).into(holder.image);
        holder.progressBar.setVisibility(View.GONE);
        setUpDisplayButton(holder.contactTarif, product);
        if (product.getDate_upd() == null) {
            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (activeTabs.equalsIgnoreCase("The Tri Store") ||
                            activeTabs.equalsIgnoreCase("La boutique du Tri") ||
                            activeTabs.equalsIgnoreCase("Tienda de tri")) {
                        AppelActivity("voir", "" + product.getId_product());
                    }else{
                        AppelActivity("voirNon", "" + product.getId_product());
                    }
                }
            });
        }else{
            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (product.getDate_upd().equalsIgnoreCase("The Tri Store") ||
                            product.getDate_upd().equalsIgnoreCase("La boutique du Tri") ||
                            product.getDate_upd().equalsIgnoreCase("Tienda de tri")) {
                        AppelActivity("voir", "" + product.getId_product());
                    }else{
                        AppelActivity("voirNon", "" + product.getId_product());
                    }
                }
            });
        }

        holder.contactTarif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (holder.contactTarif.getText().toString().equalsIgnoreCase("voir détails")) {
                    //url = "http://triakaz.com/index.php?controller=product&id_product=" + product.getId_product();
                    //url_Image = product.getActivo();
                    AppelActivity("voir", "" + product.getId_product());

                } else {
                    //url = "https://triakaz.com/fr/contactez-nous";
                    Intent intent = new Intent(mContext, WebActivity.class);
                    intent.putExtra("url", "https://triakaz.com/fr/contactez-nous");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    private void setUpDisplayNom(TextView tv, Product product) {
        String displayName = String.valueOf(product.getName());
        tv.setText(Html.fromHtml("" + displayName + ""));
        tv.setTypeface(fontBold);
    }

    public void AppelActivity(String action, String id) {

        if (action.equalsIgnoreCase("voir")) {
            Intent intent = new Intent(mContext, DetailsProduitsActivity.class);
            intent.putExtra("id", id);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        }else{
            Intent intent = new Intent(mContext, DetailsProduitsActivity.class);
            intent.putExtra("id", id);
            intent.putExtra("type", "nonshow");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        }
    }

    private void setUpDisplayButton(Button btn, Product product) {
        if (product.getDate_upd() == null) {
            if (this.activeTabs.equalsIgnoreCase("The Tri Store") ||
                    this.activeTabs.equalsIgnoreCase("La boutique du Tri") ||
                    this.activeTabs.equalsIgnoreCase("Tienda de tri")) {
                btn.setText(R.string.btn_text_details);
            } else {
                btn.setText(R.string.btn_text);
            }
        } else {

            if (product.getDate_upd().equalsIgnoreCase("The Tri Store") ||
                    product.getDate_upd().equalsIgnoreCase("La boutique du Tri") ||
                    product.getDate_upd().equalsIgnoreCase("Tienda de tri")) {
                btn.setText(R.string.btn_text_details);
            } else {
                btn.setText(R.string.btn_text);
            }
        }

        btn.setTypeface(font);
    }

    private void setUpDisplayPrix(TextView tv, Product product) {
        String displayName = String.valueOf(product.getPrice());
        if (product.getDate_upd() == null) {
            if (this.activeTabs.equalsIgnoreCase("The Tri Store") ||
                    this.activeTabs.equalsIgnoreCase("La boutique du Tri") ||
                    this.activeTabs.equalsIgnoreCase("Tienda de tri")) {
                if (!TextUtils.isEmpty(displayName)) {
                    tv.setText(Html.fromHtml("<b>" + displayName + "€</b> "));
                    tv.setTypeface(fontBold);
                }
            } else {
                tv.setVisibility(View.GONE);
            }
        } else {
            if (product.getDate_upd().equalsIgnoreCase("The Tri Store") ||
                    product.getDate_upd().equalsIgnoreCase("La boutique du Tri") ||
                    product.getDate_upd().equalsIgnoreCase("Tienda de tri")) {
                if (!TextUtils.isEmpty(displayName)) {
                    tv.setText(Html.fromHtml("<b>" + displayName + "€</b> "));
                    tv.setTypeface(fontBold);
                }
            } else {
                tv.setVisibility(View.GONE);
            }
        }
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvPrix;
        ImageView image;
        Button contactTarif;
        ProgressBar progressBar;

        public ViewHolder(View view) {
            super(view);
            tvName = (TextView) itemView.findViewById(R.id.txtName);
            tvPrix = (TextView) itemView.findViewById(R.id.txtPrix);
            progressBar = (ProgressBar) itemView.findViewById(R.id.movie_progress);
            image = (ImageView) itemView.findViewById(R.id.image);
            contactTarif = (Button) itemView.findViewById(R.id.contactTarif);
            contactTarif.setTypeface(font);
        }
    }

    GlideUrl getUrlWithHeaders(String url) {
        String credentials = "VG1GA74ZSPV861V76R79F6IERER129GE";
        credentials = credentials + ":";
        String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
        return new GlideUrl(url, new LazyHeaders.Builder()
                .addHeader("Authorization", "Basic " + base64EncodedCredentials)
                .build());
    }
}
