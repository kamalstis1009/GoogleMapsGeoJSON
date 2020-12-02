package com.subrasystems.geojson.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.data.geojson.GeoJsonPolygonStyle;
import com.subrasystems.geojson.R;
import com.subrasystems.geojson.dialogs.SearchableSpinnerDialog;
import com.subrasystems.geojson.models.Division;
import com.subrasystems.geojson.utils.Utility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ArrayList<Districts> arrayList = new Gson().fromJson(getJson(R.raw.bd_districts), new TypeToken<ArrayList<Districts>>(){}.getType());

        JSONArray divisionJSONArray = getJSONArray(R.raw.bd_divisions, "divisions");
        ArrayList<String> divisions = getAreaName(divisionJSONArray);

        HashMap<String, ArrayList<JSONArray>> geometries = getGeometries();



        if (divisions != null) {
            ((TextView) findViewById(R.id.items)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SearchableSpinnerDialog.getInstance(MainActivity.this).getAlertDialog(new SearchableSpinnerDialog.CallBackPosition() {
                        @Override
                        public void onPositionItem(int position, String name) {
                            ((TextView) findViewById(R.id.items)).setText(name);
                            /*Districts model = districtMap.get(name);
                            if (model != null) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(model.getLat(), model.getLng()), 11F));
                            }*/
                            ArrayList<JSONArray> jsonArrays = geometries.get(name);
                            for (JSONArray array : jsonArrays) {
                                setPolygon(array);
                            }
                        }
                    }, divisions, true);
                }
            });
        }

        //-----------------------------------------------| Google Maps
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Utility.getInstance().mapsStyle(this, mMap, R.raw.style_dark);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        LatLng latLng = new LatLng(23.8103, 90.4125);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 7F));

        try {
            GeoJsonLayer mLayer = new GeoJsonLayer(mMap, R.raw.bangladesh, this);
            GeoJsonPolygonStyle mStyle = mLayer.getDefaultPolygonStyle();
            mStyle.setFillColor(Color.GRAY);
            mStyle.setStrokeColor(Color.WHITE);
            mStyle.setStrokeWidth(2F);
            mLayer.addLayerToMap();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (googleMap != null) {
            //
        }
    }

    //===============================================| divisions json
    private JSONArray getJSONArray(int jsonFile, String jsonArea) {
        try {
            InputStream inputStream = getResources().openRawResource(jsonFile); //R.raw.bd_districts
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            StringBuilder result= new StringBuilder();
            String line="";
            while((line = bufferedReader.readLine())!= null) {
                result.append(line);
            }
            bufferedReader.close();

            JSONObject obj = new JSONObject(result.toString());
            JSONArray jsonArray = obj.getJSONArray(jsonArea); //"districts"

            //return new ArrayList<>(districtMap.keySet());
            return jsonArray;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private ArrayList<String> getAreaName(JSONArray jsonArray) {
        try {
            ArrayList<String> items = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++){
                Division model = new Gson().fromJson(jsonArray.getString(i), Division.class);
                items.add(model.getName());
            }
            return items;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //===============================================| bangladesh json
    private HashMap<String, ArrayList<JSONArray>> getGeometries() {
        HashSet<String> mHashSet = new HashSet<>();
        HashMap<String, ArrayList<JSONArray>> mHashMap = new LinkedHashMap<>();
        ArrayList<JSONArray> mArrayList = new ArrayList<>();
        try {
            InputStream inputStream = getResources().openRawResource(R.raw.bangladesh);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            StringBuilder result= new StringBuilder();
            String line="";
            while((line = bufferedReader.readLine())!= null) {
                result.append(line);
            }
            bufferedReader.close();

            JSONObject obj = new JSONObject(result.toString());
            JSONArray jsonArray = obj.getJSONArray("features");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);

                JSONObject property = object.getJSONObject("properties");
                String division = property.getString("NAME_1");
                String districts = property.getString("NAME_2");
                String policeStation = property.getString("NAME_3");
                String upazila = property.getString("NAME_4");

                JSONObject geometry = object.getJSONObject("geometry");
                JSONArray coordinates = geometry.getJSONArray("coordinates");
                JSONArray jsonArray1 = coordinates.getJSONArray(0);
                JSONArray jsonArray2 = jsonArray1.getJSONArray(0);

                //-----------------------------------------------| All upzila’s coordinates are speared by division
                mHashSet.add(division);
                if (mHashSet.contains(division)) {
                    mArrayList.add(jsonArray2);
                } else {
                    mHashMap.put(upazila, mArrayList);
                    mArrayList.clear();
                }

            }
            return mHashMap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //===============================================| Polyline || Polygon
    private void setPolygon(JSONArray jsonArray) {
        try {
            List<LatLng> arrayList = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++){
                String data = jsonArray.getString(i);
                JsonArray arr = (JsonArray) new JsonParser().parse(data);
                arrayList.add(new LatLng(Double.parseDouble(arr.get(1).toString()), Double.parseDouble(arr.get(0).toString())));
            }

            /*PolylineOptions options = new PolylineOptions().width(5).color(Color.parseColor("#f0ad4e")).geodesic(true);
            for (LatLng latLng : arrayList) {
                options.add(latLng);
            }
            mMap.addPolyline(options);*/

            PolygonOptions options = new PolygonOptions().clickable(true).fillColor(Color.parseColor("#f0ad4e")).geodesic(true);
            for (LatLng latLng : arrayList) {
                options.add(latLng);
            }
            mMap.addPolygon(options).setTag("polygon");
            mMap.addPolygon(options);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}