package com.testxml.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.testxml.R;
import com.testxml.activities.WebActivity;
import com.testxml.models.Product;
import com.testxml.outils.PaginationAdapterCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Suleiman on 19/10/16.
 */

public class PaginationAdapterGridView extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // View Types
    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private List<Product> productList;
    private Context context;

    private boolean isLoadingAdded = false;
    private boolean retryPageLoad = false;

    private PaginationAdapterCallback mCallback;

    private String errorMsg;
    static Typeface fontBold, font;
    Context m;
    String activeTabs;

    public PaginationAdapterGridView(Context context, Context m, String mactive) {
        this.m = m;
        this.context = context;
        this.activeTabs = mactive;
        this.mCallback = (PaginationAdapterCallback) m;
        productList = new ArrayList<>();
        fontBold = Typeface.createFromAsset(context.getAssets(), "font/futura-bold_font.ttf");
        font = Typeface.createFromAsset(context.getAssets(), "font/futura_book_font.ttf");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case ITEM:
                View viewItem = inflater.inflate(R.layout.item_girdview, parent, false);
                viewHolder = new ProductVH(viewItem);
                break;
            case LOADING:
                View viewLoading = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new LoadingVH(viewLoading);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Product result = productList.get(position);

        switch (getItemViewType(position)) {

            case ITEM:
                final ProductVH productVH = (ProductVH) holder;

                setUpDisplayNom(productVH.tvName, result);
                //setUpDisplayPrix(productVH.tvPrix, result);
                //setUpDisplayButton(productVH.contactTarif);
                Glide.with(context)
                        .load(getUrlWithHeaders(result.getActivo())).into(productVH.image);
                productVH.progressBar.setVisibility(View.GONE);
                productVH.contactTarif.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String url = "";
                        if (productVH.contactTarif.getText().toString().equalsIgnoreCase("voir détails")) {
                            url = "http://triakaz.com/index.php?controller=product&id_product=" + result.getId_product();
                        } else {
                            url = "https://triakaz.com/fr/contactez-nous";
                        }
                        Intent intent = new Intent(context, WebActivity.class);
                        intent.putExtra("url", url);
                        context.startActivity(intent);
                    }
                });
                break;

            case LOADING:
                LoadingVH loadingVH = (LoadingVH) holder;
                if (retryPageLoad) {
                    loadingVH.mErrorLayout.setVisibility(View.VISIBLE);
                    loadingVH.mProgressBar.setVisibility(View.GONE);

                    loadingVH.mErrorTxt.setText(
                            errorMsg != null ?
                                    errorMsg :
                                    context.getString(R.string.error_msg_unknown));

                } else {
                    loadingVH.mErrorLayout.setVisibility(View.GONE);
                    loadingVH.mProgressBar.setVisibility(View.VISIBLE);
                }
                break;
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

    private void setUpDisplayNom(TextView tv, Product product) {
        String displayName = String.valueOf(product.getName());
        tv.setText(Html.fromHtml("" + displayName + ""));
        tv.setTypeface(fontBold);
    }

    private void setUpDisplayButton(Button btn) {
        if (this.activeTabs.equalsIgnoreCase("The Tri Store") ||
                this.activeTabs.equalsIgnoreCase("La boutique du Tri") ||
                this.activeTabs.equalsIgnoreCase("Tienda de tri")) {
            btn.setText(R.string.btn_text_details);
        } else {
            btn.setText(R.string.btn_text);
        }
        btn.setTypeface(font);
    }

    private void setUpDisplayPrix(TextView tv,Product product) {
        String displayName = String.valueOf(product.getPrice());
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

    }

    @Override
    public int getItemCount() {
        return productList == null ? 0 : productList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == productList.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    /*
        Helpers - bind Views
   _________________________________________________________________________________________________


    /**
     * Using Glide to handle image loading.
     * Learn more about Glide here:
     * <a href="http://blog.grafixartist.com/image-gallery-app-android-studio-1-4-glide/" />
     *
     * @param posterPath from {@link Result#getPosterPath()}
     * @return Glide builder
     */
    /*private DrawableRequestBuilder<String> loadImage(@NonNull String posterPath) {
        return Glide
                .with(context)
                .load(BASE_URL_IMG + posterPath)
                .diskCacheStrategy(DiskCacheStrategy.ALL)   // cache both original & resized image
                .centerCrop()
                .crossFade();
    }*/


    /*
        Helpers - Pagination
   _________________________________________________________________________________________________
    */

    public void add(Product r) {
        productList.add(r);
        notifyItemInserted(productList.size() - 1);
    }

    public void addAll(List<Product> products) {
        for (Product result : products) {
            add(result);
        }
    }

    public void remove(Product r) {
        int position = productList.indexOf(r);
        if (position > -1) {
            productList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }


    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new Product());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = productList.size() - 1;
        Product result = getItem(position);

        if (result != null) {
            productList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public Product getItem(int position) {
        return productList.get(position);
    }

    /**
     * Displays Pagination retry footer view along with appropriate errorMsg
     *
     * @param show
     * @param errorMsg to display if page load fails
     */
    public void showRetry(boolean show, @Nullable String errorMsg) {
        retryPageLoad = show;
        notifyItemChanged(productList.size() - 1);

        if (errorMsg != null) this.errorMsg = errorMsg;
    }


    /*
    View Holders
    _________________________________________________________________________________________________


     /**
      * Main list's content ViewHolder
      */
    protected class ProductVH extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvPrix;
        ImageView image;
        Button contactTarif;
        ProgressBar progressBar;

        public ProductVH(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.txtName);
            tvPrix = (TextView) itemView.findViewById(R.id.txtPrix);
            progressBar = (ProgressBar) itemView.findViewById(R.id.movie_progress);
            image = (ImageView) itemView.findViewById(R.id.image);
            contactTarif = (Button) itemView.findViewById(R.id.contactTarif);

        }
    }


    protected class LoadingVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ProgressBar mProgressBar;
        private ImageButton mRetryBtn;
        private TextView mErrorTxt;
        private LinearLayout mErrorLayout;

        public LoadingVH(View itemView) {
            super(itemView);

            mProgressBar = (ProgressBar) itemView.findViewById(R.id.loadmore_progress);
            mRetryBtn = (ImageButton) itemView.findViewById(R.id.loadmore_retry);
            mErrorTxt = (TextView) itemView.findViewById(R.id.loadmore_errortxt);
            mErrorLayout = (LinearLayout) itemView.findViewById(R.id.loadmore_errorlayout);

            mRetryBtn.setOnClickListener(this);
            mErrorLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.loadmore_retry:
                case R.id.loadmore_errorlayout:

                    showRetry(false, null);
                    mCallback.retryPageLoad();
                    break;
            }
        }
    }

}
