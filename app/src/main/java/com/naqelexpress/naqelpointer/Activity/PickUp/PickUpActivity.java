package com.naqelexpress.naqelpointer.Activity.PickUp;

import android.content.DialogInterface;
import android.content.Intent;
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

import com.naqelexpress.naqelpointer.Activity.Waybill.WaybillPlanActivity;
import com.naqelexpress.naqelpointer.Activity.WaybillMeasurments.WaybillMeasurementActivity;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.Classes.JsonSerializerDeserializer;
import com.naqelexpress.naqelpointer.Classes.MainActivity;
import com.naqelexpress.naqelpointer.DB.DBObjects.PickUp;
import com.naqelexpress.naqelpointer.DB.DBObjects.PickUpDetail;
import com.naqelexpress.naqelpointer.DB.DBObjects.Station;
import com.naqelexpress.naqelpointer.JSON.Request.BringPickUpDataRequest;
import com.naqelexpress.naqelpointer.JSON.Results.BringPickUpDataResult;
import com.naqelexpress.naqelpointer.R;

import org.joda.time.DateTime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PickUpActivity
        extends MainActivity
{
    PickUpFirstFragment firstFragment;
    PickUpSecondFragment secondFragment;
    DateTime TimeIn;
    private Bundle bundle;

    //    FloatingActionButton SaveButton, CloseButton, OptionsButton;
    //    TextView txtSave, txtClose;
    //    Animation ShowButtonAnimation, HideButtonAnimation, ShowLayoutAnimation, HideLayoutAnimation;
    //    ConstraintLayout SaveLayout,CloseLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if(Created)
            return;
        Created = true;
        setContentView(R.layout.pickup);

//        OptionsButton = (FloatingActionButton) findViewById(R.id.btnOptions);
//        SaveButton = (FloatingActionButton) findViewById(R.id.btnShowSelectedShipments);
//        CloseButton = (FloatingActionButton) findViewById(R.id.btnClose);
//        txtSave = (TextView) findViewById(R.id.txtSave);
//        txtClose = (TextView) findViewById(R.id.txtShowShipments);

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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        TimeIn = DateTime.now();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.pickupmenu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle item selection
        switch (item.getItemId())
        {
            case R.id.mnuBringData:
                BringPickUpData();
                return true;
            case R.id.mnuSave:
                SaveData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void BringPickUpData()
    {
        if (firstFragment != null && !firstFragment.txtWaybillNo.getText().toString().equals(""))
        {
            BringPickUpDataRequest bringPickUpDataRequest = new BringPickUpDataRequest();
            bringPickUpDataRequest.WaybillNo = Integer.parseInt(firstFragment.txtWaybillNo.getText().toString());
            BringPickUpData(bringPickUpDataRequest);
        }
        else
            GlobalVar.GV().ShowSnackbar(mainRootView, "Please enter correct Waybill Number", GlobalVar.AlertType.Warning);
    }

    private void SaveData()
    {
        if (IsValid())
        {
            boolean IsSaved = true;
            int ClientID = 0;
            if (firstFragment.txtClientID.getText().toString().length() > 0)
            {
                ClientID = Integer.parseInt(firstFragment.txtClientID.getText().toString());
            }
            final PickUp pickUp = new PickUp(Integer.parseInt(firstFragment.txtWaybillNo.getText().toString()),
                    ClientID,
                    firstFragment.OriginID, firstFragment.DestinationID,
                    GlobalVar.GV().getIntegerFromString(firstFragment.txtPiecesCount.getText().toString()),
                    GlobalVar.GV().getDoubleFromString(firstFragment.txtWeight.getText().toString()),
                    TimeIn, DateTime.now(), firstFragment.txtRefNo.getText().toString(),
                    String.valueOf(Latitude), String.valueOf(Longitude));

            if (GlobalVar.GV().dbConnections.InsertPickUp(pickUp))
            {
                int PickUpID = GlobalVar.GV().dbConnections.getMaxID("PickUp");
                for (int i = 0; i < secondFragment.PickUpBarCodeList.size(); i++)
                {
                    PickUpDetail pickUpDetail = new PickUpDetail(secondFragment.PickUpBarCodeList.get(i), PickUpID);
                    if(!GlobalVar.GV().dbConnections.InsertPickUpDetail(pickUpDetail))
                    {
                        GlobalVar.GV().ShowSnackbar(mainRootView,getString(R.string.ErrorWhileSaving), GlobalVar.AlertType.Error);
                        IsSaved = false;
                        break;
                    }
                }

                if (IsSaved)
                {
                    GlobalVar.GV().ShowSnackbar(GlobalVar.GV().rootViewMainPage, getString(R.string.SaveSuccessfully), GlobalVar.AlertType.Info);

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Waybill Measurements")
                            .setMessage("Do you want to add the dimensions for this shipment?")
                            .setPositiveButton("OK",new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialogInterface,int which)
                                {
                                    Intent intent = new Intent(GlobalVar.GV().MainContext, WaybillMeasurementActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("WaybillNo", String.valueOf(pickUp.WaybillNo));
                                    bundle.putString("PiecesCount", String.valueOf(pickUp.PieceCount));
                                    intent.putExtras(bundle);
                                    startActivity(intent);

                                    //Intent waybillMeasurement = new Intent(,WaybillMeasurementActivity.class);
                                    //startActivity(waybillMeasurement );
                                    finish();
                                }
                            })
                            .setNegativeButton("Cancel",new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialogInterface,int which)
                                {
                                    finish();
                                }
                            }).setCancelable(false);

                            //.setNegativeButton("Cancel",null).setCancelable(false);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                    //finish();
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
            GlobalVar.GV().ShowSnackbar(mainRootView,"You have to enter the WaybillNo", GlobalVar.AlertType.Error);
            isValid = false;
        }

        if (secondFragment == null)
        {
            GlobalVar.GV().ShowSnackbar(mainRootView,"You have to scan the piece barcodes", GlobalVar.AlertType.Error);
            isValid = false;
        }

        if (firstFragment != null)
        {
            if (firstFragment.txtWaybillNo.getText().toString().equals(""))
            {
                GlobalVar.GV().ShowSnackbar(mainRootView,"You have to enter the Waybill No", GlobalVar.AlertType.Error);
                isValid = false;
            }

            if (firstFragment.OriginID == 0)
            {
                GlobalVar.GV().ShowSnackbar(mainRootView,"You have to select the origin", GlobalVar.AlertType.Error);
                isValid = false;
            }

            if (firstFragment.DestinationID == 0)
            {
                GlobalVar.GV().ShowSnackbar(mainRootView,"You have to select the destination", GlobalVar.AlertType.Error);
                isValid = false;
            }

            if (firstFragment.txtPiecesCount.getText().toString().equals("") ||
                    GlobalVar.GV().getIntegerFromString(firstFragment.txtPiecesCount.getText().toString()) <= 0)
            {
                GlobalVar.GV().ShowSnackbar(mainRootView,"You have to enter the Pieces Count", GlobalVar.AlertType.Error);
                isValid = false;
            }


            if (firstFragment.txtPiecesCount.getText().toString().equals("") ||
                    GlobalVar.GV().getIntegerFromString(firstFragment.txtPiecesCount.getText().toString()) <= 0)
            {
                GlobalVar.GV().ShowSnackbar(mainRootView,"You have to enter the Pieces Count", GlobalVar.AlertType.Error);
                isValid = false;
            }

            if (firstFragment.txtWeight.getText().toString().equals("") ||
                    GlobalVar.GV().getDoubleFromString(firstFragment.txtWeight.getText().toString()) <= 0)
            {
                GlobalVar.GV().ShowSnackbar(mainRootView,"You have to enter the Weight of the shipment", GlobalVar.AlertType.Error);
                isValid = false;
            }
        }

        if (secondFragment != null)
        {
            if (secondFragment.PickUpBarCodeList.size() <= 0)
            {
                GlobalVar.GV().ShowSnackbar(mainRootView,"You have to scan the piece barcodes", GlobalVar.AlertType.Error);
                isValid = false;
            }

            int piecesCount = secondFragment.PickUpBarCodeList.size();
            if (GlobalVar.GV().getIntegerFromString(firstFragment.txtPiecesCount.getText().toString()) != piecesCount)
            {
                GlobalVar.GV().ShowSnackbar(mainRootView,"Count of pieces is not matching with piece barcodes scanned.", GlobalVar.AlertType.Error);
                isValid = false;
            }


//            if (isValid)
//            {
////                try
////                {
////
////                    int piecesCount = Integer.parseInt(String.valueOf(Double.parseDouble(firstFragment.txtPiecesCount.getText().toString())));
////                    if (secondFragment.PickUpBarCodeList.size() != piecesCount) {
////                        GlobalVar.GV().ShowSnackbar(mainRootView, "Pieces count is not matching.", GlobalVar.AlertType.Error);
////                        isValid = false;
////                    }
////                }
////                catch (Exception ex)
////                {
////                    ex.printStackTrace();
////                }
//            }
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment
    {

        public PlaceholderFragment(){}

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
//        public static PlaceholderFragment newInstance(int sectionNumber)
//        {
//            PlaceholderFragment fragment = new PlaceholderFragment();
//            Bundle args = new Bundle();
//            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
//            fragment.setArguments(args);
//            return fragment;
//        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState)
        {
            return inflater.inflate(R.layout.pickupfirstfragment, container, false);
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    private class SectionsPagerAdapter extends FragmentPagerAdapter
    {
        private SectionsPagerAdapter(FragmentManager fm)
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
                        firstFragment = new PickUpFirstFragment();
                        return firstFragment;
                    }
                    else
                        return firstFragment;
                case 1:
                    if (secondFragment == null)
                    {
                        secondFragment = new PickUpSecondFragment();
                        return secondFragment;
                    }
                    else
                    {
                        return secondFragment;
                    }
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            switch (position)
            {
                case 0:
                    return getResources().getString(R.string.PickUpFirstFragment);
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
        builder.setTitle("Exit PickUp")
                .setMessage("Are you sure you want to exit without saving?")
                .setPositiveButton("OK",new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface,int which)
                    {
                        PickUpActivity.super.onBackPressed();
                    }
                }).setNegativeButton("Cancel",null).setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //--------------Bring Data in PickUp -------------------------------
    public void BringPickUpData(BringPickUpDataRequest bringPickUpDataRequest )
    {
        if (!GlobalVar.GV().HasInternetAccess)
            return;

        String jsonData = JsonSerializerDeserializer.serialize(bringPickUpDataRequest, true);
        new BringPickUpData().execute(jsonData);
    }

    private class BringPickUpData extends AsyncTask<String,Void,String>
    {
        String result = "";
        StringBuffer buffer;

        @Override
        protected String doInBackground(String... params) {
            String jsonData = params[0];
            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try
            {
                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "BringPickUpData");
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
//                int byteCharacters;
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
            super.onPostExecute(String.valueOf(finalJson));
            if (firstFragment!= null)
            {
                BringPickUpDataResult bringPickUpDataResult = new BringPickUpDataResult(finalJson);
                firstFragment.txtClientID.setText(String.valueOf(bringPickUpDataResult.ClientID));

                for(Station station : GlobalVar.GV().StationList)
                {
                    if(station.ID == bringPickUpDataResult.OriginStationID)
                    {
                        firstFragment.OriginID = bringPickUpDataResult.OriginStationID;

                        if (GlobalVar.GV().IsEnglish())
                            firstFragment.txtOrigin.setText(station.Code + " : " + station.Name);
                        else
                            firstFragment.txtOrigin.setText(station.Code + " : " + station.FName);
                        break;
                    }
                }

                for(Station station : GlobalVar.GV().StationList)
                {
                    if(station.ID == bringPickUpDataResult.DestinationStationID)
                    {
                        firstFragment.DestinationID = bringPickUpDataResult.DestinationStationID;

                        if (GlobalVar.GV().IsEnglish())
                            firstFragment.txtDestination.setText(station.Code + " : " + station.Name);
                        else
                            firstFragment.txtDestination.setText(station.Code + " : " + station.FName);
                        break;
                    }
                }
                firstFragment.txtPiecesCount.setText(String.valueOf(bringPickUpDataResult.PiecesCount));
                firstFragment.txtWeight.setText(String.valueOf(bringPickUpDataResult.Weight));
            }
        }
    }
}