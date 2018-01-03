package com.testxml.fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.testxml.R;
import com.testxml.activities.PrincipalActivity;

/**
 * Created by Raoelson on 26/09/2017.
 */

public class AProposFragment extends Fragment {
    Typeface font;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_a_propos, container, false);
        font = Typeface.createFromAsset(getContext().getAssets(), "font/futura_book_font.ttf");
        TextView textView = (TextView) view.findViewById(R.id.textView);
        TextView textView1 = (TextView) view.findViewById(R.id.textView_adresse1);
        TextView textView2 = (TextView) view.findViewById(R.id.textView_adresse2);
        TextView textView3 = (TextView) view.findViewById(R.id.textView_codepostal);
        textView.setTypeface(font);
        textView1.setTypeface(font);
        textView2.setTypeface(font);
        textView3.setTypeface(font);
        backView(view);
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), PrincipalActivity.class));
                getActivity().finish();
            }
        });
        return view;
    }

    public void backView(View v){
        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setOnKeyListener( new View.OnKeyListener()
        {
            @Override
            public boolean onKey( View v, int keyCode, KeyEvent event )
            {
                if( keyCode == KeyEvent.KEYCODE_BACK )
                {
                    startActivity(new Intent(getContext(), PrincipalActivity.class));
                    getActivity().finish();
                    return true;
                }
                return false;
            }
        } );
    }


}
