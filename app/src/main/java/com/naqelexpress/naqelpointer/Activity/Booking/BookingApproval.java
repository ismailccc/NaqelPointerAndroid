package com.naqelexpress.naqelpointer.Activity.Booking;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.naqelexpress.naqelpointer.Classes.MainActivity;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

public class BookingApproval extends MainActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Marker now;
    double Latitude = 0;
    double Longitude = 0;
    LatLng latLng;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_booking_approval);


        Intent i= getIntent();

        String text = "Hello toast! " +i.getStringExtra("ID");
        //+ ;
        int duration = Toast.LENGTH_SHORT;

        Toast.makeText(getApplicationContext(), text, text.length()).show();


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        android.location.LocationListener locationListener = new android.location.LocationListener()
        {
            @Override
            public void onLocationChanged(Location location)
            {
                if (now != null)
                    now.remove();

                Latitude = location.getLatitude();
                Longitude = location.getLongitude();

                // Creating a LatLng object for the current location
                latLng = new LatLng(Latitude, Longitude);
                BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.currentlocation);
                now = mMap.addMarker(new MarkerOptions().position(latLng)
                        .icon(icon));
//                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,12));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider){}

            @Override
            public void onProviderDisabled(String provider) {}
        };

        if (!GlobalVar.GV().checkPermission(this, GlobalVar.PermissionType.AccessFindLocation))
        {
            GlobalVar.GV().askPermission(this, GlobalVar.PermissionType.AccessFindLocation);
            finish();
        }
        else
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,500,1,locationListener);
    }

    public void Approve(View view)
    {
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.putExtra("sms_body", "Test sms");
        sendIntent.setType("vnd.android-dir/mms-sms");
        startActivity(sendIntent);
    }

    public void Reject(View view)
    {

        PackageManager pm = getPackageManager();

        try {

            Intent waIntent = new Intent(Intent.ACTION_SEND);
            waIntent.setType("text/plain");
            String text = "Share Test";

            PackageInfo info=pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
            waIntent.setPackage("com.whatsapp");

            waIntent.putExtra(Intent.EXTRA_TEXT, text);
            startActivity(Intent.createChooser(waIntent, "Share using"));

        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(this, "WhatsApp is not installed on device.", Toast.LENGTH_SHORT)
                    .show();
        }

    }
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
//        mMap.setOnMarkerClickListener(this);

        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
//        mMap.setTrafficEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setIndoorLevelPickerEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabled(true);
        mMap.getUiSettings().setTiltGesturesEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);

        if (!GlobalVar.GV().checkPermission(GlobalVar.GV().activity, GlobalVar.PermissionType.Camera))
        {
            GlobalVar.GV().ShowSnackbar(mainRootView, getString(R.string.NeedCameraPermission), GlobalVar.AlertType.Error);
            GlobalVar.GV().askPermission(GlobalVar.GV().activity, GlobalVar.PermissionType.Camera);
        }
        else
            mMap.setMyLocationEnabled(true);
//        ShowShipmentMarker();
    }


}
