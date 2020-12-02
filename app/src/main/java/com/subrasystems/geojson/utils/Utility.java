package com.subrasystems.geojson.utils;

import android.content.Context;
import android.content.res.Resources;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MapStyleOptions;

public class Utility {

    private String TAG = this.getClass().getSimpleName();
    private static Utility mUtility;

    //Utility Instance creation
    public static Utility getInstance() {
        if (mUtility == null) {
            mUtility = new Utility();
        }
        return mUtility;
    }

    //Pass a JSON style object to your map
    public void mapsStyle(Context context, GoogleMap googleMap, int res) {
        try {
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, res));
            if (!success) {
                //Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }
}
