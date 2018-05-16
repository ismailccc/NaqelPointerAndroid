package com.naqelexpress.naqelpointer.Activity.Booking;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.naqelexpress.naqelpointer.Classes.ConsingeeMobileSpinnerDialog;
import com.naqelexpress.naqelpointer.Classes.MainActivity;
import com.naqelexpress.naqelpointer.DB.DBObjects.Booking;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

public class BookingDetailActivity extends MainActivity
        implements OnMapReadyCallback
{

    TextView txtReferenceNo, txtClientId, txtClient, txtContactPerson, txtContactNo, txtOrgin,
            txtDestination, txtPiecesCount,txtWeight,txtBillType,txtLoadType,txtReqTime,txtCloseTime,
            txtRReqTime,txtRCloseTime,txtSpecialInst;
    private GoogleMap mMap;
    Booking myBooking;
    String ConsigneeLatitude, ConsigneeLongitude;
    Marker now;
    double Latitude = 0;
    double Longitude = 0;
    LatLng latLng;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookingdetail);
        bundle = getIntent().getExtras();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        txtReferenceNo = (TextView) findViewById(R.id.txtReferenceNo);
        txtClientId = (TextView) findViewById(R.id.txtClientId);
        txtClient = (TextView) findViewById(R.id.txtClientName);
        txtContactPerson = (TextView) findViewById(R.id.txtContactPerson);
        txtContactNo = (TextView) findViewById(R.id.txtContactNo);
        txtOrgin = (TextView) findViewById(R.id.txtOrgin);
        txtDestination = (TextView) findViewById(R.id.txtDestination);
        txtPiecesCount = (TextView) findViewById(R.id.txtPiecesCount);
        txtWeight = (TextView) findViewById(R.id.txtWeight);
        txtBillType = (TextView) findViewById(R.id.txtBillType);
         txtLoadType = (TextView) findViewById(R.id.txtLoadType);
        txtReqTime = (TextView) findViewById(R.id.txtReqTime);
        txtCloseTime = (TextView) findViewById(R.id.txtCloseTime);
//        txtRReqTime = (TextView) findViewById(R.id.txtRReqTime);
//        txtRCloseTime = (TextView) findViewById(R.id.txtRCloseTime);
        txtSpecialInst = (TextView) findViewById(R.id.txtSpecialInstruction);


        if (bundle != null)
        {
            for (int i = 0; i < GlobalVar.GV().myBookingList.size(); i++)
            {
                int bid = Integer.parseInt(bundle.getString("ID"));

                int bod = GlobalVar.GV().myBookingList.get(i).ID;


                if (GlobalVar.GV().myBookingList.get(i).ID == Integer.parseInt(bundle.getString("ID")) )
                {
                    myBooking = GlobalVar.GV().myBookingList.get(i);
                    txtReferenceNo.setText(myBooking.RefNo);
                    txtClientId.setText(String.valueOf(myBooking.ClientID));
                    txtClient.setText(myBooking.ClientName);
                    txtContactPerson.setText(myBooking.ContactPerson);
                    txtContactNo.setText(myBooking.ContactNumber);
                    txtOrgin.setText(myBooking.Orgin);
                    txtDestination.setText(myBooking.Destination);


                    txtPiecesCount.setText(String.valueOf(myBooking.PicesCount));
                    txtWeight.setText(String.valueOf(myBooking.Weight));
                    txtBillType.setText(myBooking.BillType);
                    txtLoadType.setText(myBooking.LoadType);
                    txtReqTime.setText(myBooking.PickUpReqDT.toString("HH:mm"));
                    txtCloseTime.setText(myBooking.OfficeUpTo.toString("HH:mm"));
                   //txtRReqTime.setText(String.format("%R",(DateTime.now().minuteOfHour()-myBooking.PickUpReqDT)));
                   // txtRCloseTime.setText(DateTime.now()-myBooking.OfficeUpTo);


                    txtSpecialInst.setText(myBooking.SpecialInstruction);




                    ConsigneeLatitude = myBooking.Latitude;
                    ConsigneeLongitude = myBooking.Longitude;


                    break;
                }
            }


        }


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
            public void onStatusChanged(String provider, int status, Bundle extras)           {            }

            @Override
            public void onProviderEnabled(String provider){}

            @Override
            public void onProviderDisabled(String provider) {            }
        };

        if (!GlobalVar.GV().checkPermission(this, GlobalVar.PermissionType.AccessFindLocation))
        {
            GlobalVar.GV().askPermission(this, GlobalVar.PermissionType.AccessFindLocation);
            finish();
        }
        else
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,500,1,locationListener);
    }

    public void CallConsignee(View view)
    {
        GlobalVar.GV().makeCall(txtContactNo.getText().toString());
    }

    public void Delivered(View view)
    {
        Intent intent = new Intent( this, com.naqelexpress.naqelpointer.Activity.Delivery.DeliveryActivity.class );
//        intent.putExtra("WaybillNo",60038400);
        startActivity(intent);
    }
    public void AcceptClick(View view)
    {

    }

    public void RejectClick(View view)
    {

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