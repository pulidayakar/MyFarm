package com.daya.myfarm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.daya.myfarm.databinding.ActivityViewFormMapsBinding;
import com.daya.myfarm.roomDatabase.LocationTask;
import com.daya.myfarm.viewModels.LocationViewModel;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;

public class ViewFormMapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String LOCATION_LIST_DATA = "allData";
    private GoogleMap mMap;
    public static String LOCATION_DATA = "locationdata";
    private LocationTask locationTask;
    private ActivityViewFormMapsBinding binding;
    LatLngBounds.Builder builder = new LatLngBounds.Builder();
    ArrayList<LocationTask> list = new ArrayList<>();
    String data = "";
    double acre = 0.0, cents = 0.0, hect = 0.0, sqFt = 0.0;
    double tacre = 0.0, tcents = 0.0, thect = 0.0, tsqFt = 0.0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_form_maps);
        setSupportActionBar(binding.toolbar);
        if (getIntent() != null){
            locationTask = (LocationTask) getIntent().getSerializableExtra(LOCATION_DATA);
            list = (ArrayList<LocationTask>) getIntent().getSerializableExtra(LOCATION_LIST_DATA);
            if (locationTask != null && getActionBar() != null)
                getActionBar().setTitle(locationTask.getName());
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.itemSync) {
            sendDataToFirebase();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendDataToFirebase() {
        if (locationTask != null) {
            DatabaseReference database = FirebaseDatabase.getInstance().getReference();
            database.push().setValue(locationTask);
        }
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

        if (list != null){
            if (list.size()>0){
                for (LocationTask locationTask: list) {
                    addLocation(locationTask);
                }
            }
        }else addLocation(locationTask);

        binding.tvAcres.setText(getString(R.string.acres_n_s, String.valueOf(round(tacre))));
        binding.tvCents.setText(getString(R.string.cents_n_s, String.valueOf(round(tcents))));
        binding.tvHectares.setText(getString(R.string.hectares_n_s, String.valueOf(round(thect))));
        binding.tvSqFt.setText(getString(R.string.sq_ft_n_s, String.valueOf(round(tsqFt))));

    }

    private void addLocation(LocationTask locationTask){
        if (locationTask != null){
            String latitude = locationTask.getLatitude().replaceAll("[\\[\\](){}]","");
            String longitude = locationTask.getLongitude().replaceAll("[\\[\\](){}]","");
            String[] strLat = latitude.split(",");
            String[] strLng = longitude.split(",");
            ArrayList<LatLng> arrayPoints = new ArrayList<>();
            int i = 0;
            for (String s: strLat){
                LatLng latLng = new LatLng(Double.parseDouble(s), Double.parseDouble(strLng[i]));
                arrayPoints.add(latLng);
                builder.include(latLng);
                i++;
            }
            if (arrayPoints.size()>2){
                drawPolygon(arrayPoints);
            }
            acre = round(convertSqtoAcre(arrayPoints));
            cents = round(convertSqtoAcre(arrayPoints)*100);
            hect = round(convertSqtoAcre(arrayPoints)*0.404686);
            sqFt = round(convertSqtoAcre(arrayPoints)*43560);

            tacre = tacre+acre;
            tcents = tcents+cents;
            thect = thect+hect;
            tsqFt = tsqFt+sqFt;
        }

    }

    private Double convertSqtoAcre(ArrayList<LatLng> arrayPoints){
        double sqmeters = SphericalUtil.computeArea(arrayPoints);
        return  round(sqmeters*0.00024711);
    }

    private static double round(double value) {
        long factor = (long) Math.pow(10, 2);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    private void drawPolygon(ArrayList<LatLng> arrayPoints) {
        int padding = 50;
        PolygonOptions polygonOptions = new PolygonOptions();
        polygonOptions.addAll(arrayPoints);
        polygonOptions.strokeColor(Color.WHITE);
        polygonOptions.strokeWidth(7);
        polygonOptions.fillColor(getResources().getColor(R.color.colorGreenLight));
        mMap.addPolygon(polygonOptions);
        LatLngBounds bounds = builder.build();
        final CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.setOnMapLoadedCallback(() -> {
            mMap.animateCamera(cu);
        });

    }

}
