package com.naqelexpress.naqelpointer.Activity.NotDelivered;

import android.content.DialogInterface;
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
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.Classes.MainActivity;
import com.naqelexpress.naqelpointer.DB.DBObjects.NotDelivered;
import com.naqelexpress.naqelpointer.DB.DBObjects.NotDeliveredDetail;
import com.naqelexpress.naqelpointer.R;

import org.joda.time.DateTime;

public class NotDeliveredActivity
        extends MainActivity
{
    NotDeliveredFirstFragment firstFragment;
    NotDeliveredSecondFragment secondFragment;

    DateTime TimeIn;
    private Bundle bundle;

    FusedLocationProviderClient mFusedLocationClient;
//    FloatingActionButton SaveButton, CloseButton, OptionsButton;
//    TextView txtSave, txtClose;
//    Animation ShowButtonAnimation,HideButtonAnimation,ShowLayoutAnimation,HideLayoutAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if(Created)
            return;
        Created = true;
        setContentView(R.layout.notdelivered);
        bundle = getIntent().getExtras();
        TimeIn = DateTime.now();

        //GlobalVar.GV().activity = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

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
//        CloseButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v)
//            {
//                FloatingPressed();
//                CloseActivity();
//            }
//        });

        if (!GlobalVar.GV().checkPermission(this, GlobalVar.PermissionType.AccessFindLocation))
        {
            GlobalVar.GV().askPermission(this, GlobalVar.PermissionType.AccessFindLocation);
            finish();
        }
        else {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        Latitude = location.getLatitude();
                        Longitude = location.getLongitude();
                    }
                }
            });
        }
    }

    private void SaveData()
    {
        if (IsValid())
        {
            boolean IsSaved = true;
            NotDelivered notDelivered = new NotDelivered(firstFragment.txtWaybillNo.getText().toString(), 0, TimeIn, DateTime.now(), String.valueOf(Latitude), String.valueOf(Longitude), firstFragment.ReasonID, firstFragment.txtNotes.getText().toString());
            if (GlobalVar.GV().dbConnections.InsertNotDelivered(notDelivered))
            {
                int NotDeeliveredID = GlobalVar.GV().dbConnections.getMaxID("NotDelivered");
                for (int i = 0; i < secondFragment.NotDeliveredBarCodeList.size(); i++)
                {
                    NotDeliveredDetail notDeliveredDetail = new NotDeliveredDetail(secondFragment.NotDeliveredBarCodeList.get(i), NotDeeliveredID);
                    if (!GlobalVar.GV().dbConnections.InsertNotDeliveredDetail(notDeliveredDetail))
                    {
                        GlobalVar.GV().ShowSnackbar(mainRootView,getString(R.string.ErrorWhileSaving), GlobalVar.AlertType.Error);
                        IsSaved = false;
                        break;
                    }
                }

                if (IsSaved)
                {
                    GlobalVar.GV().ShowSnackbar(GlobalVar.GV().rootViewMainPage, getString(R.string.SaveSuccessfully), GlobalVar.AlertType.Info);
                    finish();
                }
                else
                    GlobalVar.GV().ShowSnackbar(mainRootView, getString(R.string.NotSaved), GlobalVar.AlertType.Error);
            }
        }
//        else
//            GlobalVar.GV().ShowSnackbar(mainRootView,getString(R.string.ErrorWhileSaving), GlobalVar.AlertType.Error);
    }

    private boolean IsValid()
    {
        boolean isValid = true;
        if (firstFragment == null )
        {
            GlobalVar.GV().ShowSnackbar(mainRootView,"You have to enter the WaybillNo", GlobalVar.AlertType.Error);
            isValid = false;
        }

        if (firstFragment != null)
        {
            if (firstFragment.txtWaybillNo.getText().toString().equals(""))
            {
                GlobalVar.GV().ShowSnackbar(mainRootView,"You have to enter the Waybill No", GlobalVar.AlertType.Error);
                isValid = false;
            }

            if (firstFragment.ReasonID == 0)
            {
                GlobalVar.GV().ShowSnackbar(mainRootView,"You have to select the reason", GlobalVar.AlertType.Error);
                isValid = false;
            }
        }

//        if (secondFragment != null)
//        {
////            if (isValid)
////            {
////                int piecesCount = Integer.parseInt(firstFragment.txtPiecesCount.getText().toString());
////                if (secondFragment.PickUpBarCodeList.size() != piecesCount)
////                {
////                    GlobalVar.GV().ShowMessage(this,"Pieces count is not matching.", GlobalVar.AlertType.Error);
////                    isValid = false;
////                }
////            }
//        }

        return isValid;
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
    public boolean onOptionsItemSelected(MenuItem item) {
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

//    public static class PlaceholderFragment extends Fragment
//    {
//        private static final String ARG_SECTION_NUMBER = "section_number";
//
//        public PlaceholderFragment() {        }
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
//                                 Bundle savedInstanceState) {
//            View rootView = inflater.inflate(R.layout.notdeliveredfirstfragment, container, false);
////            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
////            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
//            return rootView;
//        }
//    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter
    {

        private SectionsPagerAdapter(FragmentManager fm) {
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
                        firstFragment = new NotDeliveredFirstFragment();
                        if (bundle != null)
                            firstFragment.setArguments(bundle);
                        return firstFragment;
                    }
                    else
                        return firstFragment;
                case 1:
                    if (secondFragment == null)
                    {
                        secondFragment = new NotDeliveredSecondFragment();
                        return secondFragment;
                    }
                    else
                        return secondFragment;
            }
            return null;
        }

        @Override
        public int getCount()       {            return 2;        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            switch (position) {
                case 0:
                    return getResources().getString(R.string.NotDeliveredActivity);
                case 1:
                    return getResources().getString(R.string.PiecesFragment);
            }
            return null;
        }
    }

    @Override
    public void onBackPressed()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit Not Delivery")
                .setMessage("Are you sure you want to exit without saving?")
                .setPositiveButton("OK",new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface,int which)
                    {
                        NotDeliveredActivity.super.onBackPressed();
                    }
                }).setNegativeButton("Cancel",null).setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
