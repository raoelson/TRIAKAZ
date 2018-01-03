package com.testxml.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;

import com.testxml.R;
import com.testxml.models.Categorie;
import com.testxml.models.Reponses;
import com.testxml.models.User;
import com.testxml.outils.ConfigurationUrl;
import com.testxml.outils.OutilsConnexion;
import com.testxml.services.Services;
import com.testxml.sqlite.DatabaseHandler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Raoelson on 26/09/2017.
 */

public class AcceuilFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    Typeface font;

    Context mContext;
    LinearLayout resultatSearch, categorie;
    DatabaseHandler databaseHandler;
    RecyclerView recyclerView;
    TextView error_txt_cause_title;
    SharedPreferences.Editor editor;
    SharedPreferences sharedpreferences;
    Services services;
    OutilsConnexion connexion;



    public AcceuilFragment newInstance(Context context) {
        AcceuilFragment mFragment = new AcceuilFragment();
        this.mContext = context;
        return mFragment;
    }
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         view = inflater.inflate(R.layout.activity_main, container, false);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
         sharedpreferences = PreferenceManager.
                getDefaultSharedPreferences(getContext());
        editor = sharedpreferences.edit();
        services = new Services(getActivity());
        connexion = new OutilsConnexion(getActivity());

        if(sharedpreferences.getString("acces_id",null) != null){
            byte[] decodedBytes = Base64.decode(sharedpreferences.getString("acces_id",null),
                    Base64.DEFAULT);
            try {
                String text = new String(decodedBytes, "UTF-8");
                if(sharedpreferences.getString("acces_id_ancien",null) ==null){
                    initSalut(text);
                }else if(sharedpreferences.getString("acces_id_ancien",null) != null){
                    if(!sharedpreferences.getString("acces_id_ancien",null).
                            equalsIgnoreCase(sharedpreferences.getString("acces_id",null))){
                        initSalut(text);
                    }
                }
                //editor.putString("acces_id_ancien",uri.getQueryParameter("ckey"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        font = Typeface.createFromAsset(getContext().getAssets(), "font/futura_book_font.ttf");
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        databaseHandler = new DatabaseHandler(getActivity());
        tabLayout.setupWithViewPager(viewPager);
        resultatSearch = (LinearLayout) view.findViewById(R.id.resultatSearch);
        categorie = (LinearLayout) view.findViewById(R.id.categorie);
        //recyclerView = (RecyclerView) view.findViewById(R.id.main_recycler);
       // main_progress = (ProgressBar) view.findViewById(R.id.main_progress);
        //error_txt_cause_title = (TextView) view.findViewById(R.id.text_vide_ancienne);
        //init();
        return view;
    }

    public void init() {
        if(connexion.isNetworkConnected()){
            services.getServices(ConfigurationUrl.urlCategorie,new Reponses() {
                @Override
                public void onSuccess(String result) {
                    services.addCategorieSqlite(result);
                    setupViewPager(viewPager);
                }

                @Override
                public void onError(VolleyError err) {

                }
            });
        }else{
            setupViewPager(viewPager);
        }

    }

   /* @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        inflater.inflate(R.menu.principal, menu);
        final MenuItem menuItem = menu.findItem(R.id.menu_search);
        menuItem.setVisible(true);

        SearchView searchView = (SearchView) menuItem.getActionView();
        ((EditText) searchView.findViewById(R.id.search_src_text)).setTypeface(font);
        searchView.setOnQueryTextListener(onQuerySearchView);
        super.onCreateOptionsMenu(menu, inflater);
        mSearchCheck = false;

    }*/

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub

        switch (item.getItemId()) {
            case R.id.menu_search:
                mSearchCheck = true;
        }
        return true;
    }*/

    public void initSalut(String id){
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

    private void setupViewPager(ViewPager viewPager) {
        Iterator iterator = services.getAllCategories().iterator();

        ViewPagerAdapter adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        while (iterator.hasNext()){
            Categorie categorie = (Categorie) iterator.next();
            adapter.addFrag(new TestFragment().newInstance(categorie.getId(), getActivity(),
                    categorie.getNom()), categorie.getNom());
        }
        viewPager.setAdapter(adapter);
        changeTabsFont();
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private void changeTabsFont() {

        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildsCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    ((TextView) tabViewChild).setTypeface(font);
                }
            }
        }
    }

    private SearchView.OnQueryTextListener onQuerySearchView = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
           /* if (mSearchCheck) {
                categorie.setVisibility(View.GONE);
                resultatSearch.setVisibility(View.VISIBLE);
                chargementSearch(s);
            }*/
            return false;
        }

        @Override
        public boolean onQueryTextChange(String query) {
            if (query.toString().equalsIgnoreCase("")) {
                categorie.setVisibility(View.VISIBLE);
                resultatSearch.setVisibility(View.GONE);
                //error_txt_cause_title.setVisibility(View.GONE);
            }
            return false;
        }

    };

    private void chargementSearch(String action) {
        /*List<Product> products = databaseHandler.getAllProduct(action);
        if (products.size() == 0) {
            error_txt_cause_title.setText("Aucun résultat trouvé");
            error_txt_cause_title.setTypeface(font);
            error_txt_cause_title.setVisibility(View.VISIBLE);
        }
        ProductAdapters productAdapters = new ProductAdapters(getActivity(), products);
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(productAdapters);

        main_progress.setVisibility(View.INVISIBLE);*/

    }

    public User getUser(String data){
        Document document = Jsoup.parse(data);
        User user = new User();
        Elements elements = document.getElementsByTag("customer");
        for (int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);
            String  nom = (element.child(10).text());
            String  prenom = (element.child(9).text());
            user.setFirstname(nom);
            user.setLastname(prenom);
        }
        return user;
    }

    public void AffichageDialogue(User user){
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialogue_pop);
        TextView txtBjr = (TextView) dialog.findViewById(R.id.textBonjour);
        TextView txtUser = (TextView) dialog.findViewById(R.id.txtUsr);
        ImageView imageView  = (ImageView) dialog.findViewById(R.id.cancel_btn);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        editor.remove("acces_id_ancien");
        editor.apply();
        editor.putString("acces_id_ancien",sharedpreferences.getString("acces_id",null));
        editor.commit();
        txtUser.setText(user.getFirstname()+ " "+ user.getLastname());
        txtBjr.setTypeface(font);
        txtUser.setTypeface(font);
        dialog.setCancelable(false);
        dialog.show();
    }

}
