package com.naqelexpress.naqelpointer.Activity.MultiDelivery;
import android.content.DialogInterface;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.naqelexpress.naqelpointer.Activity.MultiDelivery.MultiDeliveryFirstFragment;
import com.naqelexpress.naqelpointer.Activity.MultiDelivery.MultiDeliverySecondFragment;
import com.naqelexpress.naqelpointer.Activity.MultiDelivery.MultiDeliveryThirdFragment;
import com.naqelexpress.naqelpointer.Classes.MainActivity;
import com.naqelexpress.naqelpointer.DB.DBObjects.MultiDelivery;
import com.naqelexpress.naqelpointer.DB.DBObjects.MultiDeliveryDetail;
import com.naqelexpress.naqelpointer.DB.DBObjects.MultiDeliveryWaybillDetail;
import com.naqelexpress.naqelpointer.DB.DBObjects.MyRouteShipments;
import com.naqelexpress.naqelpointer.DB.DBObjects.OnCLoadingForDDetail;
import com.naqelexpress.naqelpointer.DB.DBObjects.OnCLoadingForDWaybill;
import com.naqelexpress.naqelpointer.DB.DBObjects.OnDelivery;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;
import org.joda.time.DateTime;
import static com.naqelexpress.naqelpointer.R.id.container;

public class MultiDeliveryActivity
        extends MainActivity
{
    MultiDeliveryFirstFragment firstFragment;
    MultiDeliverySecondFragment secondFragment;
    MultiDeliveryThirdFragment thirdFragment;
    DateTime TimeIn;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (Created)
            return;
        Created = true;

        setContentView(R.layout.multidelivery);
        bundle = getIntent().getExtras();
        TimeIn = DateTime.now();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        com.naqelexpress.naqelpointer.Activity.MultiDelivery.MultiDeliveryActivity.SectionsPagerAdapter mSectionsPagerAdapter = new com.naqelexpress.naqelpointer.Activity.MultiDelivery.MultiDeliveryActivity.SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = (ViewPager) findViewById(container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (!GlobalVar.GV().checkPermission(this, GlobalVar.PermissionType.AccessFindLocation))
        {
            GlobalVar.GV().askPermission(this, GlobalVar.PermissionType.AccessFindLocation);
            finish();
        }
        else
            mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>()
            {
                @Override
                public void onSuccess(Location location)
                {
                    if (location != null)
                    {
                        Latitude = location.getLatitude();
                        Longitude = location.getLongitude();
                    }
                }
            });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.notdeliveredmenu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle item selection
        switch (item.getItemId())
        {
            case R.id.mnuSave:
                SaveData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void SaveData()
    {
        if (IsValid())
        {
            boolean IsSaved = true;
            String txtReceivedBy = firstFragment.txtReceiverName.getText().toString();

            MultiDelivery multiDelivery = new MultiDelivery(txtReceivedBy,thirdFragment.PieceBarCodeList.size(),TimeIn,
                    DateTime.now(),secondFragment.WaybillList.size(),
                    String.valueOf(Latitude),String.valueOf(Longitude),0,"0",0);

            if (GlobalVar.GV().dbConnections.InsertMultiDelivery(multiDelivery))
            {
                int multiDeliveryID = GlobalVar.GV().dbConnections.getMaxID("MultiDelivery");
                for (int i = 0; i < secondFragment.WaybillList.size(); i++)
                {
                    MultiDeliveryWaybillDetail multiDeliveryWaybillDetail = new MultiDeliveryWaybillDetail(secondFragment.WaybillList.get(i), multiDeliveryID);
                    if(!GlobalVar.GV().dbConnections.InsertMultiDeliveryWaybillDetail(multiDeliveryWaybillDetail))
                    {
                        GlobalVar.GV().ShowSnackbar(mainRootView,getString(R.string.ErrorWhileSaving), GlobalVar.AlertType.Error);
                        IsSaved = false;
                        break;
                    }
                }

                for (int i = 0; i < thirdFragment.PieceBarCodeList.size(); i++)
                {
                    MultiDeliveryDetail multiDeliveryDetail = new MultiDeliveryDetail(thirdFragment.PieceBarCodeList.get(i), multiDeliveryID);
                    if(!GlobalVar.GV().dbConnections.InsertMultiDeliveryDetail(multiDeliveryDetail))
                    {
                        GlobalVar.GV().ShowSnackbar(mainRootView,getString(R.string.ErrorWhileSaving), GlobalVar.AlertType.Error);
                        IsSaved = false;
                        break;
                    }
                }

                if (IsSaved)
                {
                    GlobalVar.GV().ShowSnackbar(GlobalVar.GV().rootViewMainPage, getString(R.string.SaveSuccessfully), GlobalVar.AlertType.Info);
                    UpdateMyRouteShipments();
                    finish();
                }
                else
                    GlobalVar.GV().ShowSnackbar(mainRootView, getString(R.string.NotSaved), GlobalVar.AlertType.Error);
            }
            else
                GlobalVar.GV().ShowSnackbar(mainRootView,getString(R.string.ErrorWhileSaving), GlobalVar.AlertType.Error);
        }
    }

    private boolean IsValid()
    {
        boolean isValid = true;
        if (firstFragment == null)
        {
            GlobalVar.GV().ShowSnackbar(mainRootView,"You have to enter the Receiver Name", GlobalVar.AlertType.Error);
            isValid = false;
        }
        else
        {
            if (firstFragment.txtReceiverName.getText().toString().equals("") || firstFragment.txtReceiverName.getText().toString().length() < 3)
            {
                GlobalVar.GV().ShowSnackbar(mainRootView,"You have to enter the Receiver Name", GlobalVar.AlertType.Error);
                isValid = false;
            }
        }

        if (secondFragment == null )
        {
            GlobalVar.GV().ShowSnackbar(mainRootView,"You have to enter the Waybills", GlobalVar.AlertType.Error);
            isValid = false;
        }

        if (thirdFragment == null)
        {
            GlobalVar.GV().ShowSnackbar(mainRootView,"You have to scan the piece barcodes", GlobalVar.AlertType.Error);
            isValid = false;
        }

        if (secondFragment != null && secondFragment.WaybillList.size() <= 0)
        {
            GlobalVar.GV().ShowSnackbar(mainRootView,"You have to scan the Waybill List", GlobalVar.AlertType.Error);
            isValid = false;
            /*if (secondFragment.txtReceiverName.getText().toString().equals(""))
            {
                GlobalVar.GV().ShowSnackbar(mainRootView,"You have to enter the Receiver Name", GlobalVar.AlertType.Error);
                isValid = false;
            }
        }

        if (thirdFragment != null)
            if (thirdFragment.DeliveryBarCodeList.size() <= 0) {
                GlobalVar.GV().ShowSnackbar(mainRootView, "You have to scan the piece barcodes", GlobalVar.AlertType.Error);
                isValid = false;*/
        }

        if (thirdFragment != null && thirdFragment.PieceBarCodeList.size() <= 0)
        {
            GlobalVar.GV().ShowSnackbar(mainRootView,"You have to scan the piece barcodes", GlobalVar.AlertType.Error);
            isValid = false;
        }
        return isValid;
    }

    private void UpdateMyRouteShipments()
    {
        if(GlobalVar.GV().CourierDailyRouteID > 0)
        {
            for (int j = 0 ;j< secondFragment.WaybillList.size();j++)
            {
                String WaybillNo = secondFragment.WaybillList.get(j).toString();
                Cursor resultDetail = GlobalVar.GV().dbConnections.Fill("select * from MyRouteShipments where ItemNo = " + WaybillNo + " and CourierDailyRouteID = " + GlobalVar.GV().CourierDailyRouteID);

                if (resultDetail.getCount() > 0) {
                    resultDetail.moveToLast();
                    GlobalVar.GV().dbConnections.UpdateMyRouteShipmentsWithDelivery(Integer.parseInt(resultDetail.getString(resultDetail.getColumnIndex("ID"))));
                    if (GlobalVar.GV().myRouteShipmentList.size() > 0) {
                        for (int i = 0; i < GlobalVar.GV().myRouteShipmentList.size(); i++) {
                            MyRouteShipments item = GlobalVar.GV().myRouteShipmentList.get(i);
                            if (item.TypeID == 1 && item.ItemNo == WaybillNo)
                                GlobalVar.GV().myRouteShipmentList.remove(i);
                        }
                    }
                }
            }
        }
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter
    {
        SectionsPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            switch (position)
            {
                case 0:
                    if (firstFragment == null)
                    {
                        firstFragment = new MultiDeliveryFirstFragment();
                        if (bundle != null)
                            firstFragment.setArguments(bundle);
                        return firstFragment;
                    }
                    else
                        return firstFragment;
                case 1:
                    if (secondFragment == null)
                    {
                        secondFragment = new MultiDeliverySecondFragment();
                        return secondFragment;
                    }
                    else
                    {
                        return secondFragment;
                    }
                case 2:
                    if (thirdFragment == null)
                    {
                        thirdFragment = new MultiDeliveryThirdFragment();
                        //thirdFragment.ShipmentBarCodeList = firstFragment.ShipmentBarCodeList;
                        return thirdFragment;
                    }
                    else
                    {
                        //thirdFragment.ShipmentBarCodeList = firstFragment.ShipmentBarCodeList;
                        return thirdFragment;
                    }
            }
            return null;
        }

        @Override
        public int getCount()
        {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            switch (position)
            {
                case 0:
                    return getResources().getString(R.string.MultiDeliveryFirstFragment);
                case 1:
                    return getResources().getString(R.string.MultiDeliverySecondFragment);
                case 2:
                    return getResources().getString(R.string.MultiDeliveryThirdFragment);
            }
            return null;
        }
    }

    @Override
    public void onBackPressed()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit Multi Delivery")
                .setMessage("Are you sure you want to exit without saving?")
                .setPositiveButton("OK",new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface,int which)
                    {
                        com.naqelexpress.naqelpointer.Activity.MultiDelivery.MultiDeliveryActivity.super.onBackPressed();
                    }
                }).setNegativeButton("Cancel",null).setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}