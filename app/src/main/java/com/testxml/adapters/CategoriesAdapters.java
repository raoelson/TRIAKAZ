package com.testxml.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import com.testxml.R;
import com.testxml.models.Categorie;
import com.testxml.outils.ItemClickListener;

import java.util.List;

/**
 * Created by Raoelson on 12/09/2017.
 */

public class CategoriesAdapters extends RecyclerView.Adapter<CategoriesAdapters.ViewHolder> {

    // region Member Variables
    private Context mContext;
    private List<Categorie> categorieList;
    ItemClickListener clickListener;

    public CategoriesAdapters(Context context, List<Categorie> categories) {
        mContext = context;
        categorieList = categories;
        // endregion
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_categories, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Categorie categorie = categorieList.get(position);
        setUpDisplayNom(holder.tvNom, categorie);
        int drawable_photo = 0;
        if(categorie.getNom() !=null && categorie.getNom().equalsIgnoreCase("La boutique du Tri")){
            drawable_photo = R.mipmap.triakaz_bdt;
        }else  if(categorie.getNom() !=null && categorie.getNom().equalsIgnoreCase("Office")){
            drawable_photo = R.mipmap.triakaz_office;
        }else  if(categorie.getNom() !=null && categorie.getNom().equalsIgnoreCase("Lifestyle")){
            drawable_photo = R.mipmap.triakaz_lifestyle;
        }else  if(categorie.getNom() !=null && categorie.getNom().equalsIgnoreCase("Be green")){
            drawable_photo = R.mipmap.triakaz_begreen;
        }else  if(categorie.getNom() !=null && categorie.getNom().equalsIgnoreCase("Be fun")){
            drawable_photo = R.mipmap.triakaz_be_fun;
        }
        Glide.with(mContext)
                .load(drawable_photo).into(holder.image);
        //holder.progressBar.setVisibility(View.GONE);
       /* String url = ConfigurationUrl.green;

        loadImage(url)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        // TODO: 08/11/16 handle failure
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        // image ready, hide progress now
                        holder.progressBar.setVisibility(View.GONE);
                        return false;   // return false if you want Glide to handle everything else.
                    }
                })
                .into(holder.image);*/
    }

    @Override
    public int getItemCount() {
        return categorieList.size();
    }

    private void setUpDisplayNom(TextView tv, Categorie categorie) {
        String displayName = String.valueOf(categorie.getNom());
        tv.setText(Html.fromHtml("" + displayName + ""));
    }


    public class ViewHolder extends RecyclerView.ViewHolder  {
        TextView tvNom;
        ImageView image;
        ProgressBar progressBar;
        public ViewHolder(View view) {
            super(view);
            tvNom = (TextView) itemView.findViewById(R.id.TxtCategorie);
            progressBar = (ProgressBar) itemView.findViewById(R.id.categorie_progress);
            image = (ImageView) itemView.findViewById(R.id.imageCategorie);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clickListener != null) clickListener.onClick(v, getAdapterPosition());
                }
            });
        }

    }

    /* GlideUrl getUrlWithHeaders(String url) {
         String credentials = "VG1GA74ZSPV861V76R79F6IERER129GE";
         credentials = credentials + ":";
         String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
         return new GlideUrl(url, new LazyHeaders.Builder()
                 .addHeader("Authorization","Basic " + base64EncodedCredentials)
                 .build());
     }*/
    private DrawableRequestBuilder<String> loadImage(@NonNull String posterPath) {
        return Glide
                .with(mContext)
                .load(posterPath)
                .diskCacheStrategy(DiskCacheStrategy.ALL)   // cache both original & resized image
                .centerCrop()
                .crossFade();
    }
}
