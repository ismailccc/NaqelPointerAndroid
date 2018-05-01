package com.naqelexpress.naqelpointer.Activity.Delivery;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.Classes.NewBarCodeScanner;
import com.naqelexpress.naqelpointer.JSON.Request.GetWaybillDetailsRequest;
import com.naqelexpress.naqelpointer.JSON.Results.WaybillDetailsResult;
import com.naqelexpress.naqelpointer.R;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import static android.app.Activity.RESULT_OK;

public class DeliveryFirstFragment
        extends Fragment
{
    public ArrayList<String> ShipmentBarCodeList = new ArrayList<>();
    public EditText txtWaybillNo;
    private TextView txtConsigneeName;
    private TextView txtBillingType;
    private TextView txtCODAmount;
    private TextView txtWeight;
    private TextView txtPiecesCount;
    private TextView txtAddress;
    private TextView txtSecondAddress;
    private TextView txtNear;
    private TextView txtMobileNo;
    private TextView txtPhoneNo;
    private View rootView;
    Button btnOpenCamera;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.deliveryfirstfragment, container, false);
            txtWaybillNo = (EditText) rootView.findViewById(R.id.txtWaybilll);
            txtConsigneeName = (TextView) rootView.findViewById(R.id.txtConsigneeName);
            txtBillingType = (TextView) rootView.findViewById(R.id.txtBillingType);
            txtCODAmount = (TextView) rootView.findViewById(R.id.txtCODAmount);
            txtWeight = (TextView) rootView.findViewById(R.id.txtWeight);
            txtPiecesCount = (TextView) rootView.findViewById(R.id.txtPiecesCount);
            txtAddress = (TextView) rootView.findViewById(R.id.txtAddress);
            txtSecondAddress = (TextView) rootView.findViewById(R.id.txtSecondAddress);
            txtNear = (TextView) rootView.findViewById(R.id.txtNear);
            txtMobileNo = (TextView) rootView.findViewById(R.id.txtMobileNo);
            txtPhoneNo = (TextView) rootView.findViewById(R.id.txtPhoneNo);
            btnOpenCamera = (Button) rootView.findViewById(R.id.btnOpenCamera);

            Bundle bundle = getArguments();
            if (bundle != null) {
                txtWaybillNo.setText(bundle.getString("WaybillNo"));
                txtWaybillNo.setEnabled(false);
                btnOpenCamera.setVisibility(View.GONE);
            }
            Button btnGetWaybillDetails = (Button) rootView.findViewById(R.id.btnGetWaybillDetails);
            btnGetWaybillDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!txtWaybillNo.getText().toString().equals("") &&
                            txtWaybillNo.getText().length() > 7)
                    {
                        GetWaybillDetailsRequest getWaybillDetailsRequest = new GetWaybillDetailsRequest();
                        getWaybillDetailsRequest.WaybillNo = Integer.parseInt(txtWaybillNo.getText().toString());

                        JSONObject jsonObject = new JSONObject();
                        try {
                            if (GlobalVar.GV().HasInternetAccess) {
                                jsonObject.put("WaybillNo", getWaybillDetailsRequest.WaybillNo);

                                jsonObject.put("AppTypeID", getWaybillDetailsRequest.AppTypeID);
                                jsonObject.put("AppVersion", getWaybillDetailsRequest.AppVersion);
                                jsonObject.put("LanguageID", getWaybillDetailsRequest.LanguageID);
                                String jsonData = jsonObject.toString();

                                new GetWaybillDetailsInfo().execute(jsonData);
                            } else
                                GlobalVar.GV().ShowSnackbar(rootView, getString(R.string.NoInternetConnection), GlobalVar.AlertType.Error);
                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            });


            btnOpenCamera.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (!GlobalVar.GV().checkPermission(getActivity(), GlobalVar.PermissionType.Camera)) {
                        GlobalVar.GV().ShowSnackbar(rootView, getString(R.string.NeedCameraPermission), GlobalVar.AlertType.Error);
                        GlobalVar.GV().askPermission(getActivity(), GlobalVar.PermissionType.Camera);
                    } else {
                        Intent intent = new Intent(getContext().getApplicationContext(), NewBarCodeScanner.class);
                        startActivityForResult(intent, GlobalVar.GV().CAMERA_PERMISSION_REQUEST);
                    }
                }
            });

            initViews();
            GlobalVar.GV().activity.setRequestedOrientation(getResources().getConfiguration().orientation);
        }

        return rootView;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        GlobalVar.GV().activity.setRequestedOrientation(getResources().getConfiguration().orientation);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        GlobalVar.GV().activity.setRequestedOrientation(getResources().getConfiguration().orientation);
    }

    private void initViews()
    {
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.shipmentBarCodes);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        DataAdapter adapter = new DataAdapter(ShipmentBarCodeList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == GlobalVar.GV().CAMERA_PERMISSION_REQUEST && resultCode == RESULT_OK)
        {
            if (data != null)
            {
                Bundle extras = data.getExtras();
                if (extras != null)
                {
                    if (extras.containsKey("barcode"))
                    {
                        String barcode = extras.getString("barcode");
                        if(barcode.length() > 8)
                            barcode =  barcode.substring(0,8);
                        txtWaybillNo.setText(barcode);
                        GlobalVar.GV().MakeSound(this.getContext(), R.raw.barcodescanned);
                    }
                }

//                final Barcode barcode = data.getParcelableExtra("barcode");
//                txtWaybillNo.post(new Runnable()
//                {
//                    @Override
//                    public void run()
//                    {
//                        GlobalVar.GV().MakeSound(GlobalVar.GV().context, R.raw.barcodescanned);
//                        txtWaybillNo.setText(barcode.displayValue);
//                    }
//                });
            }
        }
    }

    private class GetWaybillDetailsInfo extends AsyncTask<String,Void,String>
    {
        String result = "";
        StringBuffer buffer;
        @Override
        protected String doInBackground(String... params)
        {
            String jsonData = params[0];
            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try
            {
                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "GetWaybillDetails");
                httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type","application/json; charset=UTF-8");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.connect();

                dos = httpURLConnection.getOutputStream();
                httpURLConnection.getOutputStream();
                dos.write(jsonData.getBytes());

                ist = httpURLConnection.getInputStream();
                String line ;
                BufferedReader reader = new BufferedReader(new InputStreamReader(ist));
                buffer = new StringBuffer();

                while((line = reader.readLine())!= null)
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
                if (httpURLConnection!=null)
                    httpURLConnection.disconnect();
                result = String.valueOf(buffer);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String finalJson)
        {
            WaybillDetailsResult waybillDetailsResult = new WaybillDetailsResult(finalJson);

            txtBillingType.setText( getResources().getString(R.string.txtBillingType ) + waybillDetailsResult.BillingType);
            txtCODAmount.setText(getResources().getString(R.string.txtCODAmount) + waybillDetailsResult.CODAmount);
            txtWeight.setText(getResources().getString(R.string.txtWeight) + waybillDetailsResult.Weight);
            txtPiecesCount.setText(getResources().getString(R.string.txtPiecesCount) + waybillDetailsResult.PiecesCount);

            if (GlobalVar.GV().IsEnglish())
                txtConsigneeName.setText(getResources().getString(R.string.txtConsigneeName) + waybillDetailsResult.ConsigneeName );
            else
                txtConsigneeName.setText(getResources().getString(R.string.txtConsigneeName) + waybillDetailsResult.ConsigneeName );
            txtAddress.setText(getResources().getString(R.string.txtAddress) + waybillDetailsResult.Address);

            txtSecondAddress.setText(getResources().getString(R.string.txtSecondAddress) + waybillDetailsResult.SecondLine);
            txtNear.setText(getResources().getString(R.string.txtNear) + waybillDetailsResult.Near);
            txtMobileNo.setText(getResources().getString(R.string.txtMobileNo) + waybillDetailsResult.MobileNo);
            txtPhoneNo.setText(getResources().getString(R.string.txtPhoneNo) + waybillDetailsResult.PhoneNo);

            ShipmentBarCodeList = new ArrayList<>();
            for (int i = 0;i<waybillDetailsResult.BarCodeList.size();i++)
            {
                ShipmentBarCodeList.add(waybillDetailsResult.BarCodeList.get(i));
            }

            initViews();
            super.onPostExecute(String.valueOf(finalJson));
        }
    }
}
