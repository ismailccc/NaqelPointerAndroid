package com.naqelexpress.naqelpointer.Activity.Delivery;

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
import com.naqelexpress.naqelpointer.DB.DBObjects.MyRouteShipments;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.Classes.MainActivity;
import com.naqelexpress.naqelpointer.DB.DBObjects.OnDelivery;
import com.naqelexpress.naqelpointer.DB.DBObjects.OnDeliveryDetail;
import com.naqelexpress.naqelpointer.R;

import org.joda.time.DateTime;

import static com.naqelexpress.naqelpointer.R.id.container;
import static com.naqelexpress.naqelpointer.R.id.txtWaybillNo;

public class DeliveryActivity
        extends MainActivity
{
    DeliveryFirstFragment firstFragment;
    DeliverySecondFragment secondFragment;
    DeliveryThirdFragment thirdFragment;
    DateTime TimeIn;
    private Bundle bundle;

//    FloatingActionButton SaveButton, CloseButton, OptionsButton;
//    TextView txtSave, txtClose;
//    Animation ShowButtonAnimation,HideButtonAnimation,ShowLayoutAnimation,HideLayoutAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (Created)
            return;
        Created = true;

        setContentView(R.layout.delivery);
        bundle = getIntent().getExtras();
        TimeIn = DateTime.now();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

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

//        OptionsButton = (FloatingActionButton) findViewById(R.id.btnOptions);
//        SaveButton = (FloatingActionButton) findViewById(R.id.btnShowSelectedShipments);
//        CloseButton = (FloatingActionButton) findViewById(R.id.btnClose);
//        txtSave = (TextView) findViewById(R.id.txtSave);
//        txtClose = (TextView) findViewById(R.id.txtShowShipments);
//
//        ShowButtonAnimation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.show_button);
//        HideButtonAnimation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.hide_button);
//        ShowLayoutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.show_layout);
//        HideLayoutAnimation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.hide_layout);
//
//        OptionsButton.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                FloatingPressed();
//            }
//        });
//
//        SaveButton.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                FloatingPressed();
//                SaveData();
//            }
//        });
//
//        CloseButton.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                FloatingPressed();
//                CloseActivity();
//            }
//        });
    }

//    private void FloatingPressed()
//    {
//        if (SaveButton.getVisibility() == View.VISIBLE )
//        {
//            SaveButton.setVisibility(View.GONE);
//            CloseButton.setVisibility(View.GONE);
//            txtSave.setVisibility(View.GONE);
//            txtClose.setVisibility(View.GONE);
//
//            SaveButton.startAnimation(HideLayoutAnimation);
//            CloseButton.startAnimation(HideLayoutAnimation);
//            txtSave.startAnimation(HideLayoutAnimation);
//            txtClose.startAnimation(HideLayoutAnimation);
//        }
//        else
//        {
//            SaveButton.setVisibility(View.VISIBLE);
//            CloseButton.setVisibility(View.VISIBLE);
//            txtSave.setVisibility(View.VISIBLE);
//            txtClose.setVisibility(View.VISIBLE);
//
//            SaveButton.startAnimation(ShowLayoutAnimation);
//            CloseButton.startAnimation(ShowLayoutAnimation);
//            txtSave.startAnimation(ShowLayoutAnimation);
//            txtClose.startAnimation(ShowLayoutAnimation);
//        }
//    }

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
            String WaybillNo = firstFragment.txtWaybillNo.getText().toString();
            String ReceiverName = String.valueOf(secondFragment.txtReceiverName.getText().toString());

            double POSAmount = 0;
            double CashAmount = 0;
            double TotalAmount;

            if (!secondFragment.txtPOS.getText().toString().equals(""))
                POSAmount = GlobalVar.GV().getDoubleFromString(secondFragment.txtPOS.getText().toString());
            if (!secondFragment.txtCash.getText().toString().equals(("")))
                CashAmount = GlobalVar.GV().getDoubleFromString(secondFragment.txtCash.getText().toString());
            TotalAmount = CashAmount + POSAmount;

            OnDelivery onDelivery = new OnDelivery(GlobalVar.GV().getIntegerFromString(WaybillNo),
                                                    ReceiverName,thirdFragment.DeliveryBarCodeList.size(),
                                                    TimeIn,
                                                    DateTime.now(),
                                                    String.valueOf(Latitude),String.valueOf(Longitude),
                                                    TotalAmount,CashAmount,POSAmount);
            if (GlobalVar.GV().dbConnections.InsertOnDelivery(onDelivery))
            {
                int DeliveryID = GlobalVar.GV().dbConnections.getMaxID("OnDelivery");
                for (int i = 0; i < thirdFragment.DeliveryBarCodeList.size(); i++)
                {
                    OnDeliveryDetail onDeliveryDetail = new OnDeliveryDetail(thirdFragment.DeliveryBarCodeList.get(i), DeliveryID);
                    if (!GlobalVar.GV().dbConnections.InsertOnDeliveryDetail(onDeliveryDetail))
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
            GlobalVar.GV().ShowSnackbar(mainRootView,"You have to enter the Waybill No", GlobalVar.AlertType.Error);
            isValid = false;
        }
        else
        {
            if (firstFragment.txtWaybillNo.getText().toString().equals("") || firstFragment.txtWaybillNo.getText().toString().length() < 8)
            {
                GlobalVar.GV().ShowSnackbar(mainRootView,"You have to enter the Waybill No", GlobalVar.AlertType.Error);
                isValid = false;
            }
        }

        if (secondFragment == null )
        {
            GlobalVar.GV().ShowSnackbar(mainRootView,"You have to enter the Receiver Name", GlobalVar.AlertType.Error);
            isValid = false;
        }

        if (thirdFragment == null)
        {
            GlobalVar.GV().ShowSnackbar(mainRootView,"You have to scan the piece barcodes", GlobalVar.AlertType.Error);
            isValid = false;
        }

        if (secondFragment != null)
        {
            if (secondFragment.txtReceiverName.getText().toString().equals(""))
            {
                GlobalVar.GV().ShowSnackbar(mainRootView,"You have to enter the Receiver Name", GlobalVar.AlertType.Error);
//                GlobalVar.GV().ShowMessage(this,"You have to enter the Receiver Name", GlobalVar.AlertType.Error);
                isValid = false;
            }
        }

        if (thirdFragment != null)
            if (thirdFragment.DeliveryBarCodeList.size() <= 0) {
//                GlobalVar.GV().ShowMessage(this,"You have to scan the piece barcodes", GlobalVar.AlertType.Error);
                GlobalVar.GV().ShowSnackbar(mainRootView, "You have to scan the piece barcodes", GlobalVar.AlertType.Error);
                isValid = false;
            }
        return isValid;
    }

    private void UpdateMyRouteShipments()
    {
        if(GlobalVar.GV().CourierDailyRouteID > 0)
        {
            String WaybillNo = firstFragment.txtWaybillNo.getText().toString();
            Cursor resultDetail = GlobalVar.GV().dbConnections.Fill("select * from MyRouteShipments where ItemNo = "+ WaybillNo +" and CourierDailyRouteID = " + GlobalVar.GV().CourierDailyRouteID);

            if(resultDetail.getCount() > 0 )
            {
                resultDetail.moveToLast();
                GlobalVar.GV().dbConnections.UpdateMyRouteShipmentsWithDelivery(Integer.parseInt(resultDetail.getString(resultDetail.getColumnIndex("ID"))));
                if(GlobalVar.GV().myRouteShipmentList.size() > 0)
                {
                    for (int i = 0 ;i< GlobalVar.GV().myRouteShipmentList.size();i++)
                    {
                        MyRouteShipments item = GlobalVar.GV().myRouteShipmentList.get(i);
                        if(item.TypeID == 1 && item.ItemNo == WaybillNo)
                            GlobalVar.GV().myRouteShipmentList.remove(i);
                    }
                }
            }
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
//    public static class PlaceholderFragment extends Fragment
//    {
//        /**
//         * The fragment argument representing the section number for this
//         * fragment.
//         */
//        private static final String ARG_SECTION_NUMBER = "section_number";
//
//        public PlaceholderFragment()        {        }
//
//        public static PlaceholderFragment newInstance(int sectionNumber)
//        {
//            PlaceholderFragment fragment = new PlaceholderFragment();
//            Bundle args = new Bundle();
//            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
//            fragment.setArguments(args);
//            return fragment;
//        }
//
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                                 Bundle savedInstanceState)
//        {
//            View rootView = inflater.inflate(R.layout.deliveryfirstfragment, container, false);
//            return rootView;
//        }
//    }

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
                        firstFragment = new DeliveryFirstFragment();
                        if (bundle != null)
                            firstFragment.setArguments(bundle);
                        return firstFragment;
                    }
                    else
                        return firstFragment;
                case 1:
                    if (secondFragment == null)
                    {
                        secondFragment = new DeliverySecondFragment();
                        return secondFragment;
                    }
                    else
                    {
                        return secondFragment;
                    }
                case 2:
                    if (thirdFragment == null)
                    {
                        thirdFragment = new DeliveryThirdFragment();
                        thirdFragment.ShipmentBarCodeList = firstFragment.ShipmentBarCodeList;
                        return thirdFragment;
                    }
                    else
                        {
                            thirdFragment.ShipmentBarCodeList = firstFragment.ShipmentBarCodeList;
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
                    return getResources().getString(R.string.DeliveryFirstFragment);
                case 1:
                    return getResources().getString(R.string.DeliverySecondFragment);
                case 2:
                    return getResources().getString(R.string.PiecesFragment);
            }
            return null;
        }
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event)
//    {
//        if (keyCode == KeyEvent.KEYCODE_BACK)
//        {
//            //moveTaskToBack(false);
//        }
//        return false;
//    }

    @Override
    public void onBackPressed()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit Delivery")
                .setMessage("Are you sure you want to exit without saving?")
                .setPositiveButton("OK",new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface,int which)
                    {
                        DeliveryActivity.super.onBackPressed();
                    }
                }).setNegativeButton("Cancel",null).setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}