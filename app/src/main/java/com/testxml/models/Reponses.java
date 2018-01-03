package com.testxml.models;

import com.android.volley.VolleyError;

/**
 * Created by Raoelson on 13/10/2017.
 */

public interface Reponses {
    void onSuccess(String result);
    void onError(VolleyError err);
}
