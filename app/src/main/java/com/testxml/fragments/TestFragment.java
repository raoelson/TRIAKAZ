package com.testxml.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.testxml.R;
import com.testxml.adapters.PaginationAdapter;
import com.testxml.adapters.ProductAdapters;
import com.testxml.adapters.ProductAdaptersGridView;
import com.testxml.models.Product;
import com.testxml.outils.PaginationAdapterCallback;
import com.testxml.services.Services;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Raoelson on 17/10/2017.
 */

public class TestFragment extends Fragment implements PaginationAdapterCallback {
    private static final String id_search = "id_search";
    private static final String value_search = "value_search";
    ProgressBar progressBar;
    RecyclerView recyclerView,recyclerViewGrid;
    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 0;
    private int currentPage = PAGE_START;
    LinearLayoutManager linearLayoutManager;
    LinearLayout errorLayout;
    Button btnRetry;
    TextView txtError;
    PaginationAdapter adapter;
    Context mContext;
    Services services;

    private ViewStub stubGrid;
    private ViewStub stubList;

    static final int VIEW_MODE_LISTVIEW = 0;
    static final int VIEW_MODE_GRIDVIEW = 1;

    private int currentViewMode = 0;
    private List<Product> productList;
    ProductAdapters adaptersList;
    ImageButton btnFilteView;

    public TestFragment newInstance(Integer text, Context context, String value) {
        TestFragment mFragment = new TestFragment();
        this.mContext = context;
        Bundle mBundle = new Bundle();
        mBundle.putString(id_search, String.valueOf(text));
        mBundle.putString(value_search, value);
        mFragment.setArguments(mBundle);
        return mFragment;
    }
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_test, container, false);
        services = new Services(getActivity());
        stubList = (ViewStub) view.findViewById(R.id.stub_list);
        stubGrid = (ViewStub) view.findViewById(R.id.stub_grid);
        stubList.inflate();
        stubGrid.inflate();
        productList = new ArrayList<>();
        btnFilteView = (ImageButton) view.findViewById(R.id.btnFilteView);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("ViewMode", MODE_PRIVATE);
        currentViewMode = sharedPreferences.getInt("currentViewMode", VIEW_MODE_LISTVIEW);
        Log.d("test"," test "+currentViewMode);
        Bitmap icon;
        if(VIEW_MODE_LISTVIEW == currentViewMode) {
            currentViewMode = VIEW_MODE_GRIDVIEW;
            icon= BitmapFactory.decodeResource(getActivity().getResources(),
                    R.drawable.ic_view_module_black_24dp);
        } else {
            icon= BitmapFactory.decodeResource(getActivity().getResources(),
                    R.drawable.ic_view_list_black_24dp);
            currentViewMode = VIEW_MODE_LISTVIEW;
        }
        btnFilteView.setImageBitmap(icon);

        loadFirstPage();


        btnFilteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if(testPositionView == false){
                    StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
                    recyclerView.setLayoutManager(staggeredGridLayoutManager);
                    testPositionView =true;
                    btnFilteView.setBackgroundResource(R.drawable.ic_view_module_black_24dp);
                    icon= BitmapFactory.decodeResource(getActivity().getResources(),
                            R.drawable.ic_view_list_black_24dp);
                }else{
                    linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    testPositionView =false;

                    icon= BitmapFactory.decodeResource(getActivity().getResources(),
                            R.drawable.ic_view_list_black_24dp);
                }*/
                Bitmap icon;
                if(VIEW_MODE_LISTVIEW == currentViewMode) {
                    currentViewMode = VIEW_MODE_GRIDVIEW;
                    icon= BitmapFactory.decodeResource(getActivity().getResources(),
                            R.drawable.ic_view_module_black_24dp);
                } else {
                    icon= BitmapFactory.decodeResource(getActivity().getResources(),
                            R.drawable.ic_view_list_black_24dp);
                    currentViewMode = VIEW_MODE_LISTVIEW;
                }
                btnFilteView.setImageBitmap(icon);
                //Switch view
                switchView();
                //Save view mode in share reference
                SharedPreferences sharedPreferences = getContext().getSharedPreferences("ViewMode", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("currentViewMode", currentViewMode);
                editor.commit();

            }
        });
        return view;
    }

    private void switchView() {
        if(VIEW_MODE_LISTVIEW == currentViewMode) {
            //Display listview
            recyclerView = (RecyclerView) view.findViewById(R.id.main_recycler1);
            linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(linearLayoutManager);
            stubList.setVisibility(View.VISIBLE);
            //Hide gridview
            stubGrid.setVisibility(View.GONE);
        } else {
            recyclerView = (RecyclerView) view.findViewById(R.id.main_recycler1_);
            linearLayoutManager = new GridLayoutManager(getContext(), 2);
            recyclerView.setLayoutManager(linearLayoutManager);
            //Hide listview
            stubList.setVisibility(View.GONE);
            //Display gridview
            stubGrid.setVisibility(View.VISIBLE);
        }


        setAdapters();
    }

    private void setAdapters() {
        if(VIEW_MODE_LISTVIEW == currentViewMode) {
            adaptersList = new ProductAdapters(getContext(), productList,"");
            recyclerView.setAdapter(adaptersList);
        } else {
            ProductAdaptersGridView gridViewAdapter_ = new ProductAdaptersGridView(getContext(), productList,"");
            recyclerView.setAdapter(gridViewAdapter_);
        }

    }

    private void loadFirstPage() {
        chargement("load");
    }


    private void chargement(final String action) {
        //hideErrorView();

        if(action.equalsIgnoreCase("load")){
            //productList = services.getAllProduit(getArguments().getString(id_search), currentPage);
        }
        switchView();

    }

    @Override
    public void retryPageLoad() {

    }
}
