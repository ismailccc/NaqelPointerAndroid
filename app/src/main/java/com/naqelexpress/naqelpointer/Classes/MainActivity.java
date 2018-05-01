package com.naqelexpress.naqelpointer.Classes;

import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;
import me.anwarshahriar.calligrapher.Calligrapher;

public class MainActivity
        extends AppCompatActivity
{
    public View mainRootView;
    public boolean Created = false;
    public double Latitude = 0;
    public double Longitude = 0;
    public LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mainRootView =  findViewById(android.R.id.content);
        GlobalVar.GV().rootView = mainRootView;
        GlobalVar.GV().HasInternetAccess = isNetworkAvailable();
        if(!GlobalVar.GV().HasInternetAccess)
            GlobalVar.GV().ShowSnackbar(mainRootView,getString(R.string.NoInternetConnection), GlobalVar.AlertType.Warning);

        GlobalVar.GV().context = this;
        GlobalVar.GV().activity = this;
        GlobalVar.GV().dbConnections = new DBConnections(GlobalVar.GV().context,mainRootView);

        if (!GlobalVar.GV().checkPermission(this, GlobalVar.PermissionType.AccessFindLocation))
        {
            GlobalVar.GV().askPermission(this, GlobalVar.PermissionType.AccessFindLocation);
            finish();
        }
        else
        {
            FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>()
            {
                @Override
                public void onSuccess(Location location)
                {
                    if (location != null)
                    {
                        Latitude = location.getLatitude();
                        Longitude = location.getLongitude();
                        GlobalVar.GV().currentLocation = new LatLng(Latitude,Longitude);
                    }
                }
            });
        }

        if(GlobalVar.GV().SignedIn && (GlobalVar.GV().UserID == 0 || GlobalVar.GV().EmployID == 0))
            finish();

        setRequestedOrientation(getResources().getConfiguration().orientation);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        ApplyCustomFont(this,this);
    }

    private void ApplyCustomFont(Context myContext, android.app.Activity myActivity)
    {
        if (GlobalVar.GV().IsEnglish())
        {
            Calligrapher calligrapher = new Calligrapher(myContext);
            calligrapher.setFont(myActivity, "MyriadPro.otf", true);
        }
        else
            {
                Calligrapher calligrapher = new Calligrapher(myContext);
                calligrapher.setFont(myActivity, "GE Dinar One Light.otf", true);
            }
    }

    public void HideKeyBoard(View view)
    {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        SyncData();
    }

    private boolean dataSynced = false;
    private void SyncData()
    {
        if (GlobalVar.GV().HasInternetAccess)
        {
            if (dataSynced)
                return;
            dataSynced = true;
            GlobalVar.GV().SyncData(GlobalVar.GV().context,mainRootView);
        }
        else
            GlobalVar.GV().ShowSnackbar(mainRootView,getString(R.string.NoInternetConnection), GlobalVar.AlertType.Warning);
    }

    public boolean isNetworkAvailable()
    {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onPostResume()
    {
        super.onPostResume();

        mainRootView =  findViewById(android.R.id.content);
        GlobalVar.GV().rootView = mainRootView;
        GlobalVar.GV().HasInternetAccess = isNetworkAvailable();

        GlobalVar.GV().context = this;
        GlobalVar.GV().activity = this;
        GlobalVar.GV().dbConnections = new DBConnections(GlobalVar.GV().context,mainRootView);
    }

    public void CloseActivity()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("OK",new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface,int which)
                    {
                        finish();
                    }
                }).setNegativeButton("Cancel",null).setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}