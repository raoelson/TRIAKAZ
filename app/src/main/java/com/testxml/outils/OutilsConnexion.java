package com.testxml.outils;

import android.content.Context;
import android.net.ConnectivityManager;


import com.testxml.R;

import java.util.concurrent.TimeoutException;

/**
 * Created by Raoelson on 30/09/2017.
 */

public class OutilsConnexion {
    Context context;
    public OutilsConnexion(Context mcontext) {
        this.context = mcontext;
    }

    public String fetchErrorMessage(Throwable throwable) {
        String errorMsg = context.getResources().getString(R.string.error_msg_unknown);

        if (!isNetworkConnected()) {
            errorMsg = context.getResources().getString(R.string.error_msg_no_internet);
        } else if (throwable instanceof TimeoutException) {
            errorMsg = context.getResources().getString(R.string.error_msg_timeout);
        }

        return errorMsg;
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
}
