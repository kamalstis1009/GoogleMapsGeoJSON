package com.subrasystems.geojson.utils;

import android.app.Activity;
import android.app.ProgressDialog;
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

    public ProgressDialog showProgressDialog(Activity activity, final String message, boolean isCancelable) {
        ProgressDialog mProgress = new ProgressDialog(activity, ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
        mProgress.setCancelable(isCancelable); //setCancelable(false); = invisible clicking the outside
        mProgress.setMessage(message);
        mProgress.show();
        return mProgress;
    }

    public void dismissProgressDialog(ProgressDialog mProgress) {
        if (mProgress != null && mProgress.isShowing()) {
            mProgress.dismiss();
        }
    }

}
