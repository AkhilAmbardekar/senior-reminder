package com.example.seniorreminder;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.SearchView;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.util.List;

/**
 * Flu shot map search activity
 *
 * @author Akhil Ambardekar
 * @since JDK 1.8
 * @version 1.1
 */
public class FluShotActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap fluShotMap;

    private BottomNavigationView bottomNavView;

    private SearchView clinicSV;

    /**
     * creates layout for flu shot activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flu_shot);

        setupFluShotActivity();

        clinicSV.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            /**
             * searches location that user inputs into searchview
             *
             * @param query - user's location search
             * @return false
             */
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = clinicSV.getQuery().toString();
                List<Address> addressList = null;

                if (location != null | !location.equals("")) {
                    Geocoder geocoder = new Geocoder(FluShotActivity.this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address address = address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    fluShotMap.addMarker(new MarkerOptions().position(latLng).title(location));
                    fluShotMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                }
                return false;
            }

            /**
             * changes when query is edited (none)
             */
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fluShotMap);
        mapFragment.getMapAsync(this);
    }

    /**
     * sets up flu shot activity layout
     */
    private void setupFluShotActivity(){
        BottomNavViewSetup bottomNavViewSetup = new BottomNavViewSetup();
        bottomNavView = findViewById(R.id.bottomNavView);
        bottomNavViewSetup.setupBottomNavViewSetup(bottomNavView, FluShotActivity.this);

        clinicSV = findViewById(R.id.clinicSV);
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
        fluShotMap = googleMap;
    }
}