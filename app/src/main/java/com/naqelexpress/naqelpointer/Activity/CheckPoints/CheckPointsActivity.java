package com.naqelexpress.naqelpointer.Activity.CheckPoints;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.naqelexpress.naqelpointer.Classes.MainActivity;
import com.naqelexpress.naqelpointer.DB.DBObjects.CheckPoint;
import com.naqelexpress.naqelpointer.DB.DBObjects.CheckPointBarCodeDetails;
import com.naqelexpress.naqelpointer.DB.DBObjects.CheckPointWaybillDetails;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;
import org.joda.time.DateTime;

// Created by sofan on 21/03/2018.

public class CheckPointsActivity
        extends MainActivity
{
    CheckPointsFirstFragment firstFragment;
    CheckPointsSecondFragment secondFragment;
    CheckPointsThirdFragment thirdFragment;
    DateTime TimeIn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if(Created)
            return;
        Created = true;
        setContentView(R.layout.checkpoints);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CheckPointsActivity.SectionsPagerAdapter mSectionsPagerAdapter = new CheckPointsActivity.SectionsPagerAdapter(getSupportFragmentManager());
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
//        ShowButtonAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.show_button);
//        HideButtonAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide_button);
//        ShowLayoutAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.show_layout);
//        HideLayoutAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.hide_layout);
//
//        OptionsButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FloatingPressed();
//            }
//        });
//
//        SaveButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FloatingPressed();
//                SaveData();
//            }
//        });
//
//        CloseButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FloatingPressed();
//                CloseActivity();
//            }
//        });

        TimeIn = DateTime.now();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.checkpointmenu,menu);
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

            CheckPoint checkPoint = new CheckPoint(firstFragment.CheckPointTypeID, String.valueOf(Latitude), String.valueOf(Longitude), firstFragment.CheckPointTypeDetailID ,firstFragment.CheckPointTypeDDetailID);

            if(GlobalVar.GV().dbConnections.InsertCheckPoint(checkPoint))
            {
                int ID = GlobalVar.GV().dbConnections.getMaxID("CheckPoint");
                for (int i = 0; i < secondFragment.WaybillList.size(); i++)
                {
                    CheckPointWaybillDetails waybills = new CheckPointWaybillDetails(secondFragment.WaybillList.get(i), ID);
                    if(!GlobalVar.GV().dbConnections.InsertCheckPointWaybillDetails(waybills))
                    {
                        GlobalVar.GV().ShowSnackbar(mainRootView,getString(R.string.ErrorWhileSaving), GlobalVar.AlertType.Error);
                        IsSaved = false;
                        break;
                    }
                }

                for (int i = 0; i < thirdFragment.BarCodeList.size(); i++)
                {
                    CheckPointBarCodeDetails checkPointBarCodeDetails = new CheckPointBarCodeDetails(thirdFragment.BarCodeList.get(i), ID);
                    if(!GlobalVar.GV().dbConnections.InsertCheckPointBarCodeDetails(checkPointBarCodeDetails))
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
            else
                GlobalVar.GV().ShowSnackbar(mainRootView,getString(R.string.ErrorWhileSaving), GlobalVar.AlertType.Error);
        }
    }

    private boolean IsValid()
    {
        boolean isValid = true;
        if (firstFragment == null  || firstFragment.CheckPointTypeID <= 0)
        {
            GlobalVar.GV().ShowSnackbar(mainRootView,"You have to select the Check Point Type", GlobalVar.AlertType.Error);
            isValid = false;
        }
        else
            if (firstFragment.txtCheckPointTypeDetail.getVisibility() == View.VISIBLE &&
                    firstFragment.CheckPointTypeDetailID == 0)
            {
                GlobalVar.GV().ShowSnackbar(mainRootView,"You have to select the reason", GlobalVar.AlertType.Error);
                isValid = false;
            }
            else
                if (firstFragment.txtCheckPointTypeDDetail.getVisibility() == View.VISIBLE &&
                    firstFragment.CheckPointTypeDDetailID == 0 )
                    {
                        GlobalVar.GV().ShowSnackbar(mainRootView,"You have to select the reason", GlobalVar.AlertType.Error);
                        isValid = false;
                    }

        if (secondFragment == null)
        {
            GlobalVar.GV().ShowSnackbar(mainRootView,"You have to scan the waybill list", GlobalVar.AlertType.Error);
            isValid = false;
        }

        if (thirdFragment == null)
        {
            GlobalVar.GV().ShowSnackbar(mainRootView,"You have to scan the pieces list", GlobalVar.AlertType.Error);
            isValid = false;
        }

//        if (firstFragment != null)
//        {
//            if (firstFragment.txtCourierID.getText().toString().equals(""))
//            {
//                GlobalVar.GV().ShowSnackbar(mainRootView,"You have to enter the Courier ID", GlobalVar.AlertType.Error);
//                isValid = false;
//            }
//
//            if (firstFragment.txtTruckID.getText().toString().equals(""))
//            {
//                GlobalVar.GV().ShowSnackbar(mainRootView,"You have to enter the Truck ID", GlobalVar.AlertType.Error);
//                isValid = false;
//            }
//        }

        if (secondFragment != null)
        {
            if (secondFragment.WaybillList.size() <= 0)
            {
                GlobalVar.GV().ShowSnackbar(mainRootView, "You have to scan the Waybills", GlobalVar.AlertType.Error);
                isValid = false;
            }
        }

        if (thirdFragment != null)
        {
            if (thirdFragment.BarCodeList.size() <= 0)
            {
                GlobalVar.GV().ShowSnackbar(mainRootView, "You have to scan the Pieces", GlobalVar.AlertType.Error);
                isValid = false;
            }
        }

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

    public static class PlaceholderFragment extends Fragment
    {
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {}

        public static CheckPointsActivity.PlaceholderFragment newInstance(int sectionNumber) {
            CheckPointsActivity.PlaceholderFragment fragment = new CheckPointsActivity.PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.checkpointsfirstfragment, container, false);
        }
    }

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
                        firstFragment = new CheckPointsFirstFragment();
                        return firstFragment;
                    }
                    else
                        return firstFragment;
                case 1:
                    if (secondFragment == null)
                    {
                        secondFragment = new CheckPointsSecondFragment();
                        return secondFragment;
                    }
                    else
                    {
                        return secondFragment;
                    }
                case 2:
                    if (thirdFragment== null)
                    {
                        thirdFragment = new CheckPointsThirdFragment();
                        return thirdFragment;
                    }
                    else
                    {
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
                    return getResources().getString(R.string.CheckPointFirstFragement);
                case 1:
                    return getResources().getString(R.string.Waybill);
                case 2:
                    return getResources().getString(R.string.Pieces);
            }
            return null;
        }
    }

    @Override
    public void onBackPressed()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit")
                .setMessage("Are you sure you want to exit without saving?")
                .setPositiveButton("OK",new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface,int which)
                    {
                        CheckPointsActivity.super.onBackPressed();
                    }
                }).setNegativeButton("Cancel",null).setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}