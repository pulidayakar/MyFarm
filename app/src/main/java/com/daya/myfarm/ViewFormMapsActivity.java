package com.daya.myfarm;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import com.daya.myfarm.databinding.ActivityViewFormMapsBinding;
import com.daya.myfarm.roomDatabase.LocationTask;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;

public class ViewFormMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public static String LOCATION_DATA = "locationdata";
    private LocationTask locationTask;
    private ActivityViewFormMapsBinding binding;
    private ArrayList<LatLng>arrayPoints = new ArrayList<>();
    LatLngBounds.Builder builder = new LatLngBounds.Builder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_form_maps);
        if (getIntent() != null){
            locationTask = (LocationTask) getIntent().getSerializableExtra(LOCATION_DATA);
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        googleMap.setMyLocationEnabled(true);

        if (locationTask != null){
            String latitude = locationTask.getLatitude().replaceAll("[\\[\\](){}]","");
            String longitude = locationTask.getLongitude().replaceAll("[\\[\\](){}]","");
            String[] strLat = latitude.split(",");
            String[] strLng = longitude.split(",");

            int i = 0;
            for (String s: strLat){
                LatLng latLng = new LatLng(Double.parseDouble(s), Double.parseDouble(strLng[i]));
                arrayPoints.add(latLng);
                builder.include(latLng);
                i++;
            }
            if (arrayPoints.size()>3){
                drawPolygon();
            }
            Toast.makeText(this, "Area: "+convertSqtoAcre(), Toast.LENGTH_LONG).show();
        }

    }
    private Double convertSqtoAcre(){
        double sqmeters = SphericalUtil.computeArea(arrayPoints);
        return  round(sqmeters*0.00024711);
    }

    private static double round(double value) {
        long factor = (long) Math.pow(10, 2);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    private void drawPolygon() {
        int padding = 50;
        PolygonOptions polygonOptions = new PolygonOptions();
        polygonOptions.addAll(arrayPoints);
        polygonOptions.strokeColor(Color.BLUE);
        polygonOptions.strokeWidth(7);
        polygonOptions.fillColor(Color.CYAN);
        mMap.addPolygon(polygonOptions);
        LatLngBounds bounds = builder.build();
        final CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.setOnMapLoadedCallback(() -> {
            mMap.animateCamera(cu);
        });

    }

}
