package com.testxml.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.testxml.R;
import com.testxml.outils.MapWrapperLayout;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Raoelson on 20/10/2017.
 */

public class BoutiqueFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    private ViewGroup infoWindow;
    MapWrapperLayout mapWrapperLayout;
    SupportMapFragment mapFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_boutique, null);
        infoWindow = (ViewGroup) inflater.inflate(R.layout.item_map, null);
        mapWrapperLayout = (MapWrapperLayout) view.findViewById(R.id.map_relative_layout);
         mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        MapsInitializer.initialize(getContext());
        if (mapFragment != null) {
            mapFragment.onCreate(null);
            mapFragment.onResume();
        }
        mapWrapperLayout.init(googleMap, getPixelsFromDp(getContext(), 39 + 20));

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                mapWrapperLayout.setMarkerWithInfoWindow(marker, infoWindow);
                return infoWindow;
            }
        });

        MarkerOptions markerOptions = new MarkerOptions();
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.mipmap.pintriakaz);
        markerOptions.icon(icon);
        LatLng coordonnees = new LatLng(16.247168,  -61.569979);
        markerOptions.position(coordonnees);
        mMap.addMarker(markerOptions);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        CameraPosition cameraPosition = CameraPosition.builder().target(coordonnees).zoom(20).bearing(0).tilt(45).build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    public static int getPixelsFromDp(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
