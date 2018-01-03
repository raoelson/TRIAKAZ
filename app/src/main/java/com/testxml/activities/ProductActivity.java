package com.testxml.activities;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.chootdev.csnackbar.Duration;
import com.chootdev.csnackbar.Type;

import com.testxml.R;
import com.testxml.adapters.ProductAdapters;
import com.testxml.adapters.ProductAdaptersGridView;
import com.testxml.models.Product;
import com.testxml.outils.TypefaceUtil;
import com.testxml.outils.Utils;
import com.testxml.services.Services;
import com.testxml.test.NotificationPackage.Configuration;
import com.testxml.test.NotificationPackage.NotificationUtils;
import com.testxml.test.app.Config;
import com.google.firebase.messaging.FirebaseMessaging;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Raoelson on 18/10/2017.
 */

public class ProductActivity extends AppCompatActivity {

    private boolean mSearchCheck;
    Services services;

    private ViewStub stubGrid;
    private ViewStub stubList;

    static final int VIEW_MODE_LISTVIEW = 0;
    static final int VIEW_MODE_GRIDVIEW = 1;

    private int currentViewMode = 0;
    private List<Product> productList;
    ProductAdapters adaptersList;
    ImageButton btnFilteView;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    Button btnTrier, btnFiltre;
    int position = -1;
    SharedPreferences.Editor editor;
    SharedPreferences sharedpreferences;
    String tri = "";
    String minPrice = "", maxPrice = "";
    String idCategorie = "", mTabs = " ";
    private int mNotificationsCount = 0;
    String searchText = "";
    TextView text_vide;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private int mnotificationsCount = 0;
    Context mContext;
    View view;
    MenuItem itemNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        view = getLayoutInflater().inflate(R.layout.snackbar_custom, null);
        mContext = this;
        services = new Services(this);
        services.deletePanierAfterHour();
        stubList = (ViewStub) findViewById(R.id.stub_list);
        stubGrid = (ViewStub) findViewById(R.id.stub_grid);
        stubList.inflate();
        stubGrid.inflate();
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "font/futura_book_font.ttf");
        sharedpreferences = PreferenceManager.
                getDefaultSharedPreferences(this);
        editor = sharedpreferences.edit();
        productList = new ArrayList<>();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        btnFilteView = (ImageButton) findViewById(R.id.btnFilteView);
        btnTrier = (Button) findViewById(R.id.btnTrier);
        btnFiltre = (Button) findViewById(R.id.btnFiltre);
        text_vide = (TextView) findViewById(R.id.text_vide);

        idCategorie = getIntent().getStringExtra("id");
        if (idCategorie.equalsIgnoreCase("80")) {
            btnFiltre.setEnabled(true);
        } else {
            btnFiltre.setEnabled(false);
        }
        mTabs = getIntent().getStringExtra("nom");
        toolbar.setTitle(getIntent().getStringExtra("nom"));
        currentViewMode = sharedpreferences.getInt("currentViewMode", VIEW_MODE_LISTVIEW);
        Bitmap icon;
        if (VIEW_MODE_LISTVIEW == currentViewMode) {
            currentViewMode = VIEW_MODE_GRIDVIEW;
            icon = BitmapFactory.decodeResource(getResources(),
                    R.drawable.ic_view_module_black_24dp);
        } else {
            icon = BitmapFactory.decodeResource(getResources(),
                    R.drawable.ic_view_list_black_24dp);
            currentViewMode = VIEW_MODE_LISTVIEW;
        }
        btnFilteView.setImageBitmap(icon);
        loadFirstPage();
        btnFilteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap icon;
                if (VIEW_MODE_LISTVIEW == currentViewMode) {
                    currentViewMode = VIEW_MODE_GRIDVIEW;
                    icon = BitmapFactory.decodeResource(getResources(),
                            R.drawable.ic_view_module_black_24dp);
                } else {
                    icon = BitmapFactory.decodeResource(getResources(),
                            R.drawable.ic_view_list_black_24dp);
                    currentViewMode = VIEW_MODE_LISTVIEW;
                }
                btnFilteView.setImageBitmap(icon);
                //Switch view
                switchView();
                //Save view mode in share reference

                editor.putInt("currentViewMode", currentViewMode);
                editor.commit();

            }
        });

        btnTrier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowTrier();
            }
        });
        btnFiltre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowFiltre();
            }
        });
        BroadNotification();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mnotificationsCount = services.getCountNotification();
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
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Configuration.REGISTRATION_COMPLETE));

        // register new push postUrlFromNotification receiver
        // by doing this, the activity will be notified each time a new postUrlFromNotification arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Configuration.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_notification:
                startActivityForResult(new
                        Intent(getApplicationContext(), NotificationActivity.class), 100);
                return true;
            case R.id.menu_search:
                mSearchCheck = true;
                return true;
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

    @Override
    protected void onStop() {
        super.onStop();
        editor.remove("idpostionTrier");
        editor.remove("_minFiltre");
        editor.remove("_maxFiltre");
        editor.apply();
    }

    public void ShowFiltre() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_filter_filtre);
        final TextView textMax, textMin, textFiltre, textFermer;
        textMin = (TextView) dialog.findViewById(R.id.textMin);
        textMax = (TextView) dialog.findViewById(R.id.textMax);
        textFiltre = (TextView) dialog.findViewById(R.id.textFiltre);
        textFermer = (TextView) dialog.findViewById(R.id.textFermer);


        RangeSeekBar rangeSeekBarTextColorWithCode = (RangeSeekBar)
                dialog.findViewById(R.id.rangeSeekBarTextColorWithCode);
        rangeSeekBarTextColorWithCode.setTextAboveThumbsColorResource(android.R.color.holo_blue_bright);

        String[] textSplit = services.getMinAndMax(idCategorie).split("-");
        rangeSeekBarTextColorWithCode.setRangeValues(Integer.parseInt(textSplit[0]), Integer.parseInt(textSplit[1]));
/*
        final SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor _editor;
        _editor = shared.edit();*/


        if ((sharedpreferences.getString("_minFiltre", null) != null) && (sharedpreferences.getString("_maxFiltre", null) != null)) {
            textMin.setText("€ " + sharedpreferences.getString("_minFiltre", null));
            textMax.setText("€ " + sharedpreferences.getString("_maxFiltre", null));
            rangeSeekBarTextColorWithCode.setSelectedMinValue(Integer.parseInt(sharedpreferences.getString("_minFiltre", null)));
            rangeSeekBarTextColorWithCode.setSelectedMaxValue(Integer.parseInt(sharedpreferences.getString("_maxFiltre", null)));
        } else {
            textMin.setText("€ " + textSplit[0]);
            textMax.setText("€ " + textSplit[1]);
        }

        rangeSeekBarTextColorWithCode.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Object minValue, Object maxValue) {
                textMin.setText("€ " + minValue.toString());
                textMax.setText("€ " + maxValue.toString());
                minPrice = minValue.toString();
                maxPrice = maxValue.toString();

            }
        });

        textFermer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        textFiltre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (minPrice.equalsIgnoreCase("") && maxPrice.equalsIgnoreCase("")) {
                    dialog.dismiss();
                    return;
                }
                AffiageSortPrice();
                if (sharedpreferences.getString("_minFiltre", null) != null &&
                        sharedpreferences.getString("_maxFiltre", null) != null) {
                    editor.remove("_minFiltre");
                    editor.remove("_maxFiltre");
                    editor.apply();
                    editor.putString("_minFiltre", "" + minPrice);
                    editor.putString("_maxFiltre", "" + maxPrice);
                    editor.commit();

                } else {
                    editor.putString("_minFiltre", "" + minPrice);
                    editor.putString("_maxFiltre", "" + maxPrice);
                    editor.commit();
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void AffiageSortPrice() {
        loadFirstPage();
    }

    public void ShowTrier() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.trierText);
        //list of items
        final String[] items = getResources().getStringArray(R.array.text_trier);

        int postion_ = -1;
        if (sharedpreferences.getString("idpostionTrier", null) != null) {
            postion_ = Integer.parseInt(sharedpreferences.getString("idpostionTrier", null));
        }
        builder.setSingleChoiceItems(items, postion_,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // item selected logic
                        position = which;
                    }
                });

        String positiveText = getString(R.string.trier);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // positive button logic
                        if (sharedpreferences.getString("idpostionTrier", null) != null) {
                            editor.remove("idpostionTrier");
                            editor.apply();
                            editor.putString("idpostionTrier", "" + position);
                            editor.commit();
                        } else {
                            editor.putString("idpostionTrier", "" + position);
                            editor.commit();
                        }
                        //tri = items[position];
                        if (items[position].toString().equalsIgnoreCase("nom")) {
                            tri = "name";
                        } else if (items[position].toString().equalsIgnoreCase("prix")) {
                            tri = "price";
                        } else {
                            tri = "datepro";
                        }
                        loadFirstPage();
                    }

                });

        String negativeText = getString(R.string.fermer);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // negative button logic
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void loadFirstPage() {
        chargement("load");
    }


    private void chargement(final String action) {
        //hideErrorView();
        if (action.equalsIgnoreCase("load")) {
            productList = services.getAllProduit(idCategorie, tri, minPrice, maxPrice, searchText);
        }
      /*  if(productList.size() == 0){
            Toast.makeText(this,"Aucun Résultat",Toast.LENGTH_LONG).show();
            return;
        }*/
        switchView();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.principal, menu);

        itemNotification = menu.findItem(R.id.menu_notification);
        ShowBadgeNotification();

        //panier
        MenuItem item = menu.findItem(R.id.menu_panier);
        LayerDrawable icon = (LayerDrawable) item.getIcon();
        // Update LayerDrawable's BadgeDrawable
        Utils.setBadgeCount(this, icon, mNotificationsCount);

        final MenuItem menuItem = menu.findItem(R.id.menu_search);
        menuItem.setVisible(true);

        final MenuItem menuItem_ = menu.findItem(R.id.menu_search);
        menuItem_.setVisible(true);

        SearchView searchView = (SearchView) menuItem_.getActionView();
        searchView.setQueryHint("Tapez le nom de produit ...");
        ((EditText) searchView.findViewById(R.id.search_src_text))
                .setHintTextColor(getResources().getColor(R.color.colorSeconde));
        searchView.setOnQueryTextListener(onQuerySearchView);
        mSearchCheck = false;

        return true;
    }

    private void updateNotificationsBadge(int count) {
        mNotificationsCount = count;

        // force the ActionBar to relayout its MenuItems.
        // onCreateOptionsMenu(Menu) will be called again.
        invalidateOptionsMenu();
    }

    private SearchView.OnQueryTextListener onQuerySearchView = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            if (mSearchCheck) {
                searchText = s;
                loadFirstPage();
            }
            return false;
        }

        @Override
        public boolean onQueryTextChange(String query) {
            if (mSearchCheck) {
                if (query.length() == 0) {
                    searchText = "";
                    loadFirstPage();
                }
            }
            return false;
        }
    };

    private void switchView() {
        if (VIEW_MODE_LISTVIEW == currentViewMode) {
            //Display listview
            recyclerView = (RecyclerView) findViewById(R.id.main_recycler1);
            linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(linearLayoutManager);
            stubList.setVisibility(View.VISIBLE);
            stubGrid.setVisibility(View.GONE);
        } else {
            recyclerView = (RecyclerView) findViewById(R.id.main_recycler1_);
            linearLayoutManager = new GridLayoutManager(this, 2);
            recyclerView.setLayoutManager(linearLayoutManager);
            stubList.setVisibility(View.GONE);
            stubGrid.setVisibility(View.VISIBLE);
        }


        setAdapters();
    }

    private void setAdapters() {
        if (productList.size() == 0) {
            text_vide.setVisibility(View.VISIBLE);
        } else {
            text_vide.setVisibility(View.GONE);
        }
        if (VIEW_MODE_LISTVIEW == currentViewMode) {
            adaptersList = new ProductAdapters(getApplicationContext(), productList, mTabs);
            recyclerView.setAdapter(adaptersList);
        } else {
            ProductAdaptersGridView gridViewAdapter_ = new ProductAdaptersGridView(getApplicationContext(), productList, mTabs);
            recyclerView.setAdapter(gridViewAdapter_);
        }

    }

    public void BroadNotification() {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received
                    String message = intent.getStringExtra("message");
                    String textTitre = intent.getStringExtra("title");
                    String imageUrl = intent.getStringExtra("imageUrl");
                    mnotificationsCount = services.getCountNotification();
                    ShowBadgeNotification();
                    invalidateOptionsMenu();
                    TextView titre = (TextView) view.findViewById(R.id.textView2);
                    TextView description = (TextView) view.findViewById(R.id.textView3);
                    TextView textVoir = (TextView) view.findViewById(R.id.textVoir);
                    TextView textFermer = (TextView) view.findViewById(R.id.textFermer);
                    ImageView snackbar_image = (ImageView) view.findViewById(R.id.snackbar_image);
                    final ProgressBar _progressBar = (ProgressBar) view.findViewById(R.id.snackbar_progress);
                    /*titre.setTypeface(font_bold);
                    textVoir.setTypeface(font_bold);
                    textFermer.setTypeface(font_bold);*/
                    //description.setTypeface(font);
                    titre.setText(Html.fromHtml(textTitre));
                    description.setText(Html.fromHtml(message));
                    if (view.getParent() != null)
                        ((ViewGroup) view.getParent()).removeView(view);

                    loadImage(imageUrl)
                            .listener(new RequestListener<String, GlideDrawable>() {
                                @Override
                                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                    _progressBar.setVisibility(View.GONE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                    // image ready, hide progress now
                                    _progressBar.setVisibility(View.GONE);
                                    return false;   // return false if you want Glide to handle everything else.
                                }
                            })
                            .into(snackbar_image);

                    textFermer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            com.chootdev.csnackbar.Snackbar.dismiss();
                        }
                    });

                    com.chootdev.csnackbar.Snackbar.with(mContext, null)
                            .type(Type.UPDATE)
                            .contentView(view, 76)
                            .duration(Duration.INFINITE)
                            .show();

                }
            }
        };
    }

    private DrawableRequestBuilder<String> loadImage(@NonNull String posterPath) {
        return Glide
                .with(getApplicationContext())
                .load(posterPath)
                .diskCacheStrategy(DiskCacheStrategy.ALL)   // cache both original & resized image
                .centerCrop()
                .crossFade();
    }

    public void ShowBadgeNotification() {
        LayerDrawable iconNotification = (LayerDrawable) itemNotification.getIcon();
        // Update LayerDrawable's BadgeDrawable
        Utils.setBadgeCount(this, iconNotification, mnotificationsCount);
    }

}
