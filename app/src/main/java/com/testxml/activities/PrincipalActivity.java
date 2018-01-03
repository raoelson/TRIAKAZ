package com.testxml.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.SubMenu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.chootdev.csnackbar.Duration;
import com.chootdev.csnackbar.Snackbar;
import com.chootdev.csnackbar.Type;

import com.testxml.R;
import com.testxml.fragments.AProposFragment;
import com.testxml.fragments.BoutiqueFragment;
import com.testxml.fragments.CategorieFragment;
import com.testxml.fragments.ProduitFragment;
import com.testxml.fragments.WebFragment;
import com.testxml.models.Reponses;
import com.testxml.outils.ConfigurationUrl;
import com.testxml.outils.CustomTypefaceSpan;
import com.testxml.outils.OutilsConnexion;
import com.testxml.outils.TypefaceUtil;
import com.testxml.outils.Utils;
import com.testxml.services.Services;
import com.testxml.test.NotificationPackage.Configuration;
import com.testxml.test.NotificationPackage.NotificationUtils;
import com.testxml.test.app.Config;
import com.felipecsl.gifimageview.library.GifImageView;
import com.google.firebase.messaging.FirebaseMessaging;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import me.leolin.shortcutbadger.ShortcutBadger;

public class PrincipalActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Typeface font, font_bold;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    Context mContext;
    private boolean mSearchCheck;
    View view;
    Services services;
    private int mNotificationsCount = 0;
    private int mnotificationsCount = 0;
    MenuItem itemNotification;
    OutilsConnexion connexion;
    ProgressDialog dialog = null;
    InputStream inputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        connexion = new OutilsConnexion(this);
        dialog = new ProgressDialog(this);
        sharedpreferences = PreferenceManager.
                getDefaultSharedPreferences(this);
        editor = sharedpreferences.edit();
       /* try {
           String  currentVersion = getPackageManager (). getPackageInfo (getPackageName (), 0) .versionName ;
            Log.v("test"," version google"+ PrincipalActivity.this.getPackageName ());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.logo_11);
        services = new Services(getApplicationContext());
        view = getLayoutInflater().inflate(R.layout.snackbar_custom, null);
        services.deletePanierAfterHour();
        mContext = this;
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "font/futura_book_font.ttf");
        font = Typeface.createFromAsset(getAssets(), "font/futura_book_font.ttf");
        font_bold = Typeface.createFromAsset(getAssets(), "font/futura-bold_font.ttf");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu m = navigationView.getMenu();
        for (int i = 0; i < m.size(); i++) {
            MenuItem mi = m.getItem(i);
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu != null && subMenu.size() > 0) {
                for (int j = 0; j < subMenu.size(); j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem1(subMenuItem);
                }
            }
            applyFontToMenuItem(mi);
        }
        navigationView.setNavigationItemSelectedListener(this);
        Fragment fragment = new CategorieFragment().newInstance(getApplicationContext());
        getSupportFragmentManager().beginTransaction()
               /* .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter,
                        R.anim.pop_exit)*/
                .replace(R.id.content_frame, fragment)
                //.addToBackStack(null)
                .commit();
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
                    final String imageUrl = intent.getStringExtra("imageUrl");
                    final String urlUrl = intent.getStringExtra("url");
                    mnotificationsCount = services.getCountNotification();
                    ShowBadgeNotification();
                    invalidateOptionsMenu();
                    TextView titre = (TextView) view.findViewById(R.id.textView2);
                    TextView description = (TextView) view.findViewById(R.id.textView3);
                    TextView textVoir = (TextView) view.findViewById(R.id.textVoir);
                    TextView textFermer = (TextView) view.findViewById(R.id.textFermer);
                    ImageView snackbar_image = (ImageView) view.findViewById(R.id.snackbar_image);
                    final ProgressBar _progressBar = (ProgressBar) view.findViewById(R.id.snackbar_progress);
                    titre.setTypeface(font_bold);
                    textVoir.setTypeface(font_bold);
                    textFermer.setTypeface(font_bold);
                    description.setTypeface(font);
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
                            Snackbar.dismiss();
                        }
                    });
                    textVoir.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(!urlUrl.equalsIgnoreCase("")){
                                UpdateNotification();
                                Snackbar.dismiss();
                                Intent intent = new Intent(getBaseContext(), WebActivity.class);
                                intent.putExtra("url", urlUrl);
                                startActivity(intent);
                            }
                        }
                    });
                    snackbar_image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(!urlUrl.equalsIgnoreCase("")){
                                UpdateNotification();
                                Snackbar.dismiss();
                                Intent intent = new Intent(getBaseContext(), WebActivity.class);
                                intent.putExtra("url", urlUrl);
                                startActivity(intent);
                            }
                        }
                    });

                    Snackbar.with(mContext, null)
                            .type(Type.UPDATE)
                            .contentView(view, 76)
                            .duration(Duration.INFINITE)
                            .show();

                }
            }
        };


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
        timeChargment();
    }

    private DrawableRequestBuilder<String> loadImage(@NonNull String posterPath) {
        return Glide
                .with(getApplicationContext())
                .load(posterPath)
                .diskCacheStrategy(DiskCacheStrategy.ALL)   // cache both original & resized image
                .centerCrop()
                .crossFade();
    }

    private void applyFontToMenuItem(MenuItem mi) {
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        if (mi.getTitle().toString().equalsIgnoreCase("INFORMATIONS")) {
            mNewTitle.setSpan(new CustomTypefaceSpan("", font_bold), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        } else if (mi.getTitle().toString().equalsIgnoreCase("MON COMPTE")) {
            mNewTitle.setSpan(new CustomTypefaceSpan("", font_bold), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        } else if (mi.getTitle().toString().equalsIgnoreCase("REVUE DE PRESSE")) {
            mNewTitle.setSpan(new CustomTypefaceSpan("", font_bold), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }
        mi.setTitle(mNewTitle);
    }

    private void applyFontToMenuItem1(MenuItem mi) {
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*services.deleteCache();*/
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


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        Date date1 = null;
        try {
            date1 = sdf.parse(sdf.format(date));
            Date date2 = sdf.parse("2017-12-32");
            if (date1.compareTo(date2) < 0) {
                affichagePop();
            } else if (date1.compareTo(date2) == 0) {
                affichagePop();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //this.menu = menu;
        getMenuInflater().inflate(R.menu.principal, menu);
        if (services.getAllNotification().size() != 0) {
            /*menu.getItem(2).setIcon(getResources().getDrawable(R.drawable.badge_circle_notification));
            menu.getItem(2).setTitle("Notifications");*/
            menu.getItem(2).setEnabled(true);
        } else {
            menu.getItem(2).setEnabled(false);
        }

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

    public void ShowBadgeNotification() {
        LayerDrawable iconNotification = (LayerDrawable) itemNotification.getIcon();
        // Update LayerDrawable's BadgeDrawable
        Utils.setBadgeCount(this, iconNotification, mnotificationsCount);
    }

    private SearchView.OnQueryTextListener onQuerySearchView = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            if (mSearchCheck) {
                AffichageRecherche(s);
            }
            return false;
        }

        @Override
        public boolean onQueryTextChange(String query) {
            if (mSearchCheck) {
                if (query.length() == 0) {
                    Fragment fragment = new CategorieFragment().newInstance(getApplicationContext());
                    getSupportFragmentManager().beginTransaction()
               /* .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter,
                        R.anim.pop_exit)*/
                            .replace(R.id.content_frame, fragment)
                            //.addToBackStack(null)
                            .commit();
                }
            }
            return false;
        }
    };

    public void AffichageRecherche(String nom) {
        Fragment fragment = new ProduitFragment().newInstance(nom);
        getSupportFragmentManager().beginTransaction()
               /* .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter,
                        R.anim.pop_exit)*/
                .replace(R.id.content_frame, fragment)
                //.addToBackStack(null)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_notification) {
            startActivityForResult(new
                    Intent(getApplicationContext(), NotificationActivity.class), 100);
            return true;
        } else if (id == R.id.menu_search) {
            mSearchCheck = true;
            return true;
        } else if (id == R.id.menu_panier) {
            startActivityForResult(new
                    Intent(getApplicationContext(), PanierActivity.class), 100);
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateNotificationsBadge(int count) {
        mNotificationsCount = count;

        // force the ActionBar to relayout its MenuItems.
        // onCreateOptionsMenu(Menu) will be called again.7
        invalidateOptionsMenu();
    }

    public void UpdateNotification(){
        services.updateAllNotifications();
        ShortcutBadger.removeCount(PrincipalActivity.this);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Fragment fragment = null;
        int id = item.getItemId();
        if (id == R.id.nav_accueil) {
            fragment = new CategorieFragment().newInstance(getApplicationContext());
        } else if (id == R.id.nav_boutique) {
            fragment = new BoutiqueFragment();
        }
        if (id == R.id.nav_qui_sommes) {
            fragment = new WebFragment().newInstance(ConfigurationUrl.qui_somme, this);
        } else if (id == R.id.nav_dossier_presse) {
            fragment = new WebFragment().newInstance(ConfigurationUrl.dossier_presse, getApplicationContext());
        } else if (id == R.id.nav_video) {
            fragment = new WebFragment().newInstance(ConfigurationUrl.video, getApplicationContext());
        } else if (id == R.id.nav_photo) {
            fragment = new WebFragment().newInstance(ConfigurationUrl.photo, getApplicationContext());
        } else if (id == R.id.nav_articles) {
            fragment = new WebFragment().newInstance(ConfigurationUrl.articles, getApplicationContext());
        } else if (id == R.id.nav_compte) {
            fragment = new WebFragment().newInstance(ConfigurationUrl.comptes, getApplicationContext());
        } else if (id == R.id.nav_commande) {
            fragment = new WebFragment().newInstance(ConfigurationUrl.commandes, getApplicationContext());
        } else if (id == R.id.nav_identier) {
            fragment = new WebFragment().newInstance(ConfigurationUrl.identier, getApplicationContext());
        } else if (id == R.id.nav_promotion) {
            fragment = new WebFragment().newInstance(ConfigurationUrl.promotions, getApplicationContext());
        } else if (id == R.id.nav_produits) {
            fragment = new WebFragment().newInstance(ConfigurationUrl.new_product, getApplicationContext());
        } else if (id == R.id.nav_vente) {
            fragment = new WebFragment().newInstance(ConfigurationUrl.ventes, getApplicationContext());
        } else if (id == R.id.nav_propos) {
            fragment = new AProposFragment();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        this.CallFragment(fragment);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void CallFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
               /* .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter,
                        R.anim.pop_exit)*/
                .replace(R.id.content_frame, fragment)
                //.addToBackStack(null)
                .commit();
    }

    public void ChargmentProduit() {
        if (connexion.isNetworkConnected()) {
            dialog.setTitle("TRIAKAZ");
            dialog.setMessage("Chargement en cours...");
            dialog.setIndeterminate(true);
            dialog.show();
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    services.getServices(ConfigurationUrl.urlCategorie, new Reponses() {
                        @Override
                        public void onSuccess(String result) {
                            services.deleteAllProduit();
                            AddToSqlite();
                        }

                        @Override
                        public void onError(VolleyError err) {
                            dialog.dismiss();
                        }
                    });
                }
            }, 3000);

        }

    }

    public void AddToSqlite() {
        String url = ConfigurationUrl.urlAllProduit;
        services.getServices(url, new Reponses() {
            @Override
            public void onSuccess(String result) {
                Log.d("test"," produit "+result);
                services.addProduit(result);
                dialog.dismiss();
                //Log.d("test", " test prduit" + result);
            }

            @Override
            public void onError(VolleyError err) {
                dialog.dismiss();
            }
        });
    }

    public void timeChargment(){
        Date date = new Date();
        //long tm = date.getTime() + 3600000;
        long tm = date.getTime() + 900000;
        if (sharedpreferences.getString("dateTimeProduit", null) != null) {
            long dateAjout_ = Long.parseLong(sharedpreferences.getString("dateTimeProduit", null));
            if (dateAjout_ >= date.getTime()) {
                return;
                //Log.d("test"," daty if "+date.getTime() + "  --  "+sharedpreferences.getString("dateTimeProduit", null));

            } else {
                editor.remove("dateTimeProduit");
                editor.apply();
                editor.putString("dateTimeProduit", "" + tm);
                editor.commit();
                ChargmentProduit();
               //Log.d("test"," daty else "+date.getTime() + "  --  "+sharedpreferences.getString("dateTimeProduit", null));
            }

        } else {
            Log.d("test"," daty depart "+date.getTime() + "  --  "+sharedpreferences.getString("dateTimeProduit", null));
            editor.putString("dateTimeProduit", "" + tm);
            editor.commit();
            ChargmentProduit();
        }
    }

    public void affichagePop(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
        dialog.setContentView(R.layout.dialague_noel);
        ImageView imageView  = (ImageView) dialog.findViewById(R.id.cancel_btn);
        GifImageView gifImageView = (GifImageView)dialog.findViewById(R.id.gifImageView);
        try {
            inputStream = getAssets().open("images/promos_noel_triakaz.gif");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try{

            byte[] bytes = IOUtils.toByteArray(inputStream);
            gifImageView.setBytes(bytes);
            gifImageView.startAnimation();
        }
        catch (Exception ex)
        {

        }

        //Wait for 3 seconds and start Activity Main
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        },20000);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

}
