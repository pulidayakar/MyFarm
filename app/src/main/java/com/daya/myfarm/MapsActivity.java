package com.daya.myfarm;

import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.daya.myfarm.databinding.ActivityMapsBinding;
import com.daya.myfarm.roomDatabase.LocationTask;
import com.daya.myfarm.viewModels.LocationViewModel;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "";
    LocationManager locationManager;
    LatLng flatlng, llatlng;
    ActivityMapsBinding binding;
    LocationViewModel viewModel;
    private GoogleMap mMap;
    private ArrayList<LatLng> arrayPoints = new ArrayList<>();
    private LocationListener locationListener;
    private boolean isFirstLocation = true, isPermissionEnabled;
    private List<String> latList = new ArrayList<>();
    private List<String> longList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_maps);
        requestLocationPermission();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        viewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(LocationViewModel.class);
        viewModel.getMessage().observe(this, s -> {
            Toast.makeText(MapsActivity.this, s, Toast.LENGTH_LONG).show();
        });
        binding.btnGetLocation.setOnClickListener(v -> {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationListener = new MyLocationListener();
            if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                LatLng latLng = new LatLng(latitude, longitude);
                latList.add(String.valueOf(latitude));
                longList.add(String.valueOf(longitude));
                arrayPoints.add(latLng);
                if (arrayPoints != null && arrayPoints.size() > 2)
                    drawPolyLineOnMap(arrayPoints);
                updateCameraBearing(latLng);
            }

            //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        });
        binding.deleteLast.setOnClickListener(v -> {
            if (arrayPoints != null && latList != null){
                arrayPoints.remove(arrayPoints.size()-1);
                latList.remove(latList.size()-1);
                longList.remove(longList.size()-1);
                drawPolyLineOnMap(arrayPoints);
            }

        });

        binding.btnStopLocation.setOnClickListener(v -> {
            stopLocation();
        });

        binding.btnSaveLocation.setOnClickListener(v -> {
            binding.llButtons.setVisibility(View.GONE);
            binding.rlSave.setVisibility(View.VISIBLE);
        });
        binding.btnCancel.setOnClickListener(v -> {
            binding.etName.setText("");
            binding.llButtons.setVisibility(View.VISIBLE);
            binding.rlSave.setVisibility(View.GONE);
        });
        binding.btnSave.setOnClickListener(v -> {
            String name = binding.etName.getText().toString();
            if (TextUtils.isEmpty(name))
                Toast.makeText(MapsActivity.this, "Please enter name", Toast.LENGTH_LONG).show();
            else
                saveData(name);
        });
    }

    private void stopLocation() {
        if (locationManager != null)
            locationManager.removeUpdates(locationListener);
    }

    public void saveData(final String name) {
        viewModel.addLocation(new LocationTask(name, latList.toString(), longList.toString()));
        binding.etName.setText("");
        binding.llButtons.setVisibility(View.VISIBLE);
        binding.rlSave.setVisibility(View.GONE);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        if (isPermissionEnabled) googleMap.setMyLocationEnabled(true);
    }

    public void drawPolyline(LatLng first, LatLng second) {
        PolylineOptions options = new PolylineOptions()
                .width(8)
                .jointType(JointType.ROUND)
                .color(Color.RED)
                .startCap(new RoundCap())
                .endCap(new RoundCap())
                .geodesic(true);
        options.add(first);
        mMap.addPolyline(options);

    }

    public void drawPolyLineOnMap(List<LatLng> list) {
        PolylineOptions polyOptions = new PolylineOptions()
                .width(8)
                .jointType(JointType.ROUND)
                .color(Color.RED)
                .startCap(new RoundCap())
                .endCap(new RoundCap())
                .geodesic(true);
        polyOptions.addAll(list);
        mMap.clear();
        mMap.addPolyline(polyOptions);
        /*LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng latLng : list) {
            builder.include(latLng);
        }
*/
        //   final LatLngBounds bounds = builder.build();

        //BOUND_PADDING is an int to specify padding of bound.. try 100.
        //  CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 18);
        //  mMap.animateCamera(cu);
    }

    private void updateCameraBearing(LatLng latLng) {

        CameraPosition newCamPos = new CameraPosition(latLng,
                18.0f,
                mMap.getCameraPosition().tilt, //use old tilt
                mMap.getCameraPosition().bearing); //use old bearing
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(newCamPos), 1000, null);

    }

    private void requestLocationPermission() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            isPermissionEnabled = true;
                            // Toast.makeText(getApplicationContext(), "All permissions are granted!", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocation();
    }

    public class MyLocationListener implements LocationListener {

        public void onLocationChanged(Location location) {
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                LatLng latLng = new LatLng(latitude, longitude);
                latList.add(String.valueOf(latitude));
                longList.add(String.valueOf(longitude));
                if (isFirstLocation) {
                    flatlng = new LatLng(latitude, longitude);
                    isFirstLocation = false;
                } else
                    llatlng = new LatLng(latitude, longitude);

                if (flatlng != null && llatlng != null)
                    drawPolyline(flatlng, llatlng);

                flatlng = llatlng;
                updateCameraBearing(latLng);
            }
        }

        public void onProviderDisabled(String arg0) {

        }

        public void onProviderEnabled(String provider) {

        }

        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    }
}
