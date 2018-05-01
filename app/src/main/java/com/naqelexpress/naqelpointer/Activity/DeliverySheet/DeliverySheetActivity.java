package com.naqelexpress.naqelpointer.Activity.DeliverySheet;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
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

import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.Classes.JsonSerializerDeserializer;
import com.naqelexpress.naqelpointer.Classes.MainActivity;
import com.naqelexpress.naqelpointer.DB.DBObjects.OnCLoadingForDDetail;
import com.naqelexpress.naqelpointer.DB.DBObjects.OnCLoadingForDWaybill;
import com.naqelexpress.naqelpointer.DB.DBObjects.OnCloadingForD;
import com.naqelexpress.naqelpointer.JSON.Request.OptimizedOutOfDeliveryShipmentRequest;
import com.naqelexpress.naqelpointer.JSON.Results.OptimizedOutOfDeliveryShipmentResult;
import com.naqelexpress.naqelpointer.R;

import org.joda.time.DateTime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DeliverySheetActivity
        extends MainActivity
{
    DeliverySheetFirstFragment firstFragment;
    DeliverySheetSecondFragment secondFragment;
    DeliverySheetThirdFragment thirdFragment;
    DateTime TimeIn;

//    FloatingActionButton SaveButton, CloseButton, OptionsButton;
//    TextView txtSave, txtClose;
//    Animation ShowButtonAnimation, HideButtonAnimation, ShowLayoutAnimation, HideLayoutAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if(Created)
            return;
        Created = true;
        setContentView(R.layout.deliverysheet);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
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
        inflater.inflate(R.menu.menu_delivery_sheet,menu);
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
            case R.id.mnuGetMyRouteShipments:
                GetMyRouteShipments();
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
            OnCloadingForD onCloadingForD = new OnCloadingForD(GlobalVar.GV().getIntegerFromString(firstFragment.txtCourierID.getText().toString()),
                    thirdFragment.PieceBarCodeList.size(),secondFragment.WaybillList.size(),
                    firstFragment.txtTruckID.getText().toString());

            if(GlobalVar.GV().dbConnections.InsertOnCloadingForD(onCloadingForD))
            {
                int OnCloadingForDID = GlobalVar.GV().dbConnections.getMaxID("OnCloadingForD");
                for (int i = 0; i < secondFragment.WaybillList.size(); i++)
                {
                    OnCLoadingForDWaybill onCLoadingForDWaybill = new OnCLoadingForDWaybill(secondFragment.WaybillList.get(i), OnCloadingForDID);
                    if(!GlobalVar.GV().dbConnections.InsertOnCLoadingForDWaybill(onCLoadingForDWaybill))
                    {
                        GlobalVar.GV().ShowSnackbar(mainRootView,getString(R.string.ErrorWhileSaving), GlobalVar.AlertType.Error);
                        IsSaved = false;
                        break;
                    }
                }

                for (int i = 0; i < thirdFragment.PieceBarCodeList.size(); i++)
                {
                    OnCLoadingForDDetail onCLoadingForDDetail = new OnCLoadingForDDetail(thirdFragment.PieceBarCodeList.get(i), OnCloadingForDID);
                    if(!GlobalVar.GV().dbConnections.InsertOnCLoadingForDDetail(onCLoadingForDDetail))
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
        if (firstFragment == null )
        {
            GlobalVar.GV().ShowSnackbar(mainRootView,"You have to enter the Courier ID", GlobalVar.AlertType.Error);
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

        if (firstFragment != null)
        {
            if (firstFragment.txtCourierID.getText().toString().equals(""))
            {
                GlobalVar.GV().ShowSnackbar(mainRootView,"You have to enter the Courier ID", GlobalVar.AlertType.Error);
                isValid = false;
            }

            if (firstFragment.txtTruckID.getText().toString().equals(""))
            {
                GlobalVar.GV().ShowSnackbar(mainRootView,"You have to enter the Truck ID", GlobalVar.AlertType.Error);
                isValid = false;
            }
        }

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
            if (thirdFragment.PieceBarCodeList.size() <= 0)
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

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.deliverysheetfirstfragment, container, false);
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
                        firstFragment = new DeliverySheetFirstFragment();
                        return firstFragment;
                    }
                    else
                        return firstFragment;
                case 1:
                    if (secondFragment == null)
                    {
                        secondFragment = new DeliverySheetSecondFragment();
                        return secondFragment;
                    }
                    else
                    {
                        return secondFragment;
                    }
                case 2:
                    if (thirdFragment== null)
                    {
                        thirdFragment = new DeliverySheetThirdFragment();
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
                    return getResources().getString(R.string.DeliverySheetFirstFragement);
                case 1:
                    return getResources().getString(R.string.DeliverySheetSecondFragment);
                case 2:
                    return getResources().getString(R.string.DeliverySheetThirdFragment);
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
                        DeliverySheetActivity.super.onBackPressed();
                    }
                }).setNegativeButton("Cancel",null).setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //--------------Get My Route Shipments -------------------------------
    public void GetMyRouteShipments()
    {
        GlobalVar.GV().optimizedOutOfDeliveryShipmentList = new ArrayList<>();
        if (!GlobalVar.GV().HasInternetAccess)
            return;

        OptimizedOutOfDeliveryShipmentRequest optimizedOutOfDeliveryShipmentRequest = new OptimizedOutOfDeliveryShipmentRequest();

        String jsonData = JsonSerializerDeserializer.serialize(optimizedOutOfDeliveryShipmentRequest, true);
        new GetMyRouteShipmentsData().execute(jsonData);
    }

    private class GetMyRouteShipmentsData extends AsyncTask<String,Void,String>
    {
        private ProgressDialog progressDialog;
        String result = "";
        StringBuffer buffer;

        @Override
        protected void onPreExecute()
        {
            progressDialog = ProgressDialog.show(GlobalVar.GV().context, "Please wait.", "Downloading My Route Shipments.", true);
        }

        @Override
        protected String doInBackground(String... params) {
            String jsonData = params[0];
            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try
            {
                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "OptimizedOutOfDeliveryShipment");
                httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.connect();

                dos = httpURLConnection.getOutputStream();
                httpURLConnection.getOutputStream();
                dos.write(jsonData.getBytes());

                ist = httpURLConnection.getInputStream();
                String line;
                BufferedReader reader = new BufferedReader(new InputStreamReader(ist));
                buffer = new StringBuffer();

                while ((line = reader.readLine()) != null)
                {
                    buffer.append(line);
                }

                return String.valueOf(buffer);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                try {
                    if (ist != null)
                        ist.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    if (dos != null)
                        dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (httpURLConnection != null)
                    httpURLConnection.disconnect();
                result = String.valueOf(buffer);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String finalJson)
        {
            progressDialog.dismiss();
            super.onPostExecute(String.valueOf(finalJson));
            if (finalJson != null)
                new OptimizedOutOfDeliveryShipmentResult(finalJson);
        }
    }
}