package com.naqelexpress.naqelpointer.Activity.Waybill;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.Classes.MainActivity;
import com.naqelexpress.naqelpointer.DB.DBObjects.MyRouteShipments;
import com.naqelexpress.naqelpointer.R;

public class WaybillPlanActivity
        extends MainActivity
        implements OnMapReadyCallback
{
    private GoogleMap mMap;
    Marker now;
    TextView txtWaybillNo, txtShipperName, txtConsigneeName, txtMobileNo, txtBillingType, txtCODAmount, txtPODType, txtPhoneNo;
    TextView lbPODType;
    ConsingeeMobileSpinnerDialog spinnerDialog;
    //Button btnDelivered, btnNotDeliverd, btnCall;
    private Bundle bundle;
    MyRouteShipments myRouteShipments;
    String ConsigneeLatitude, ConsigneeLongitude;
    SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if(Created)
            return;
        Created = true;
        setContentView(R.layout.waybillplan);
        bundle = getIntent().getExtras();

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        txtWaybillNo = (TextView) findViewById(R.id.txtWaybilll);
        txtShipperName = (TextView) findViewById(R.id.txtShipperName);
        txtConsigneeName = (TextView) findViewById(R.id.txtConsigneeName);
        txtMobileNo = (TextView) findViewById(R.id.txtMobileNo);
        txtBillingType = (TextView) findViewById(R.id.txtBillingType);
        txtCODAmount = (TextView) findViewById(R.id.txtCODAmount);
        txtPODType = (TextView) findViewById(R.id.txtPODType);
        lbPODType = (TextView) findViewById(R.id.lbPODType);
        txtPhoneNo = (TextView) findViewById(R.id.txtPhoneNo);

        if (bundle != null)
        {
            for (int i = 0; i < GlobalVar.GV().myRouteShipmentList.size(); i++)
            {
                int bid = Integer.parseInt(bundle.getString("ID"));
                String sWaybill = bundle.getString("WaybillNo");
                int bod = GlobalVar.GV().myRouteShipmentList.get(i).ID;
                String sw = GlobalVar.GV().myRouteShipmentList.get(i).ItemNo;

                if (GlobalVar.GV().myRouteShipmentList.get(i).ID == Integer.parseInt(bundle.getString("ID")) &&
                        GlobalVar.GV().myRouteShipmentList.get(i).ItemNo.equals(bundle.getString("WaybillNo")))
                {
                    myRouteShipments = GlobalVar.GV().myRouteShipmentList.get(i);
                    txtWaybillNo.setText(myRouteShipments.ItemNo);
                    txtShipperName.setText(myRouteShipments.ClientName);
                    txtConsigneeName.setText(myRouteShipments.ConsigneeName);
                    txtMobileNo.setText(myRouteShipments.ConsigneeMobile);
                    txtBillingType.setText(myRouteShipments.BillingType);
                    txtCODAmount.setText(String.valueOf(myRouteShipments.CODAmount));
                    txtPhoneNo.setText(myRouteShipments.ConsigneePhoneNumber);
                    ConsigneeLatitude = myRouteShipments.Latitude;
                    ConsigneeLongitude = myRouteShipments.Longitude;

                    if(myRouteShipments.PODNeeded)
                        txtPODType.setText(myRouteShipments.PODTypeCode);
                    else
                    {
                        txtPODType.setVisibility(View.GONE);
                        lbPODType.setVisibility(View.GONE);
                    }
                    break;
                }
            }
        }

//        btnDelivered = (Button) findViewById(R.id.btnDelivered);
//        btnNotDeliverd = (Button) findViewById(R.id.btnNotDeliverd);
//        btnCall = (Button) findViewById(R.id.btnCallFirst);
//
//        btnDelivered.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                Delivered();
//            }
//        });
//
//        btnNotDeliverd.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                NotDelivered();
//            }
//        });

        spinnerDialog = new ConsingeeMobileSpinnerDialog(GlobalVar.GV().activity,txtPhoneNo.getText().toString(),txtMobileNo.getText().toString());
//        spinnerDialog.bindOnSpinerListener(new OnSpinerItemClick()
//        {
//            @Override
//            public void onClick(String item, int position)
//            {
//                if (GlobalVar.GV().IsEnglish())
//                    txtReason.setText(GlobalVar.GV().DeliveryStatusNameList.get(position));
//                else
//                    txtReason.setText(GlobalVar.GV().DeliveryStatusFNameList.get(position));
//                ReasonID =  GlobalVar.GV().DeliveryStatusList.get(position).ID;
//                txtNotes.requestFocus();
//            }
//        });

//        btnCall.setOnClickListener(new View.OnClickListener()
//        {
//
//            @Override
//            public void onClick(View v)
//            {
//                spinnerDialog.showSpinerDialog();
//                //MakeCall();
//            }
//        });

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
                GlobalVar.GV().currentLocation = latLng;
                BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.currentlocation);
                now = mMap.addMarker(new MarkerOptions().position(latLng)
                        .icon(icon)
                        .title(getString(R.string.MyLocation)));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras){}

            @Override
            public void onProviderEnabled(String provider){}

            @Override
            public void onProviderDisabled(String provider){}
        };

        if (!GlobalVar.GV().checkPermission(GlobalVar.GV().activity, GlobalVar.PermissionType.AccessFindLocation))
        {
            GlobalVar.GV().ShowSnackbar(mainRootView, getString(R.string.NeedLocationPermision), GlobalVar.AlertType.Error);
            GlobalVar.GV().askPermission(GlobalVar.GV().activity, GlobalVar.PermissionType.AccessFindLocation);
        }
        else
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,500,1,locationListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mnuwaybilldetails,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle item selection
        switch (item.getItemId())
        {
            case R.id.ConsigneeAddrss:
                ConsigneeAddress();
                return true;
            case R.id.CallConsignee:
                spinnerDialog.showSpinerDialog();
                return true;
            case R.id.Delivered:
                Delivered();
                return true;
            case R.id.NotDelivered:
                NotDelivered();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void MakeCall()
    {
        GlobalVar.GV().makeCall(txtMobileNo.getText().toString());
    }

    public void ConsigneeAddress()
    {
        //Intent intent = new Intent( this, com.naqelexpress.naqelpointer.Activity.Waybill.ConsigneeAddressTranslation.class );
        Intent intent = new Intent(getApplicationContext(), ConsigneeAddressTranslationActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void Delivered()
    {
        Intent intent = new Intent( this, com.naqelexpress.naqelpointer.Activity.Delivery.DeliveryActivity.class );
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void NotDelivered()
    {
        Intent intent = new Intent( this, com.naqelexpress.naqelpointer.Activity.NotDelivered.NotDeliveredActivity.class );
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        GlobalVar.GV().ChangeMapSettings(mMap);

        ShowShipmentMarker();
    }

    private void ShowShipmentMarker()
    {
        if (ConsigneeLongitude.length() > 3 && ConsigneeLongitude.length() > 3) {
            LatLng latLng = new LatLng(GlobalVar.GV().getDoubleFromString(ConsigneeLatitude),GlobalVar.GV().getDoubleFromString(ConsigneeLongitude));
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.deliverymarker);
            mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(icon)
                    .title(txtWaybillNo.getText().toString()));
        }
        else
            mapFragment.getView().setVisibility(View.GONE);
    }
}