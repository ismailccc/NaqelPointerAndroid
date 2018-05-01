package com.naqelexpress.naqelpointer.Activity.PickUp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.Classes.JsonSerializerDeserializer;
import com.naqelexpress.naqelpointer.Classes.NewBarCodeScanner;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.JSON.Request.CheckWaybillAlreadyPickedUpRequest;
import com.naqelexpress.naqelpointer.Classes.OnSpinerItemClick;
import com.naqelexpress.naqelpointer.R;
import com.naqelexpress.naqelpointer.Classes.SpinnerDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.app.Activity.RESULT_OK;

public class PickUpFirstFragment
        extends Fragment
{
    View rootView;
//    Spinner orgSpinner;
//    Spinner destSpinner;

    SpinnerDialog orgSpinnerDialog,destSpinnerDialog;
    EditText txtOrigin, txtDestination;
    public EditText txtWaybillNo, txtPiecesCount, txtWeight, txtRefNo, txtClientID;
    public int OriginID = 0,DestinationID = 0;

//    ArrayAdapter<String> adapter1;
//    private DataAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        if (rootView == null)
        {
            rootView = inflater.inflate(R.layout.pickupfirstfragment, container, false);
            txtOrigin = (EditText) rootView.findViewById(R.id.txtOrigin);
            txtOrigin.setInputType(InputType.TYPE_NULL);
            OriginID = GlobalVar.GV().StationID;
            txtOrigin.setText(GlobalVar.GV().GetStationByID(GlobalVar.GV().StationID));

            txtDestination = (EditText) rootView.findViewById(R.id.txtDestination);
            txtDestination.setInputType(InputType.TYPE_NULL);
            txtPiecesCount = (EditText) rootView.findViewById(R.id.txtPiecesCount);

            txtWaybillNo = (EditText) rootView.findViewById(R.id.txtWaybilll);
            txtClientID = (EditText) rootView.findViewById(R.id.txtClientID);
            txtPiecesCount = (EditText) rootView.findViewById(R.id.txtPiecesCount);
            txtWeight = (EditText) rootView.findViewById(R.id.txtWeight);
            txtRefNo = (EditText) rootView.findViewById(R.id.txtRefNo);

            txtWaybillNo.addTextChangedListener(new TextWatcher()
            {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count){}

                @Override
                public void afterTextChanged(Editable s)
                {
                    CheckWaybillAlreadyPickedUp();
                }
            });

//          orgSpinner = (Spinner) rootView.findViewById(R.id.comFromStation);
//          destSpinner = (Spinner) rootView.findViewById(R.id.comToStation);

//                GlobalVar.GV().context = getContext();
            GlobalVar.GV().dbConnections = new DBConnections(GlobalVar.GV().context,rootView);
//                GlobalVar.GV().GetStationList();

//                Collections.sort(GlobalVar.GV().StationList);
//                String[] Cs = new String[GlobalVar.GV().StationList.size()];
//                Cs = GlobalVar.GV().StationList.toArray(Cs);

            txtOrigin.setOnFocusChangeListener(new View.OnFocusChangeListener()
            {
                @Override
                public void onFocusChange(View v, boolean hasFocus)
                {
                    if (hasFocus)
                        orgSpinnerDialog.showSpinerDialog(false);
                }
            });

            if (GlobalVar.GV().IsEnglish())
                orgSpinnerDialog = new SpinnerDialog(GlobalVar.GV().activity,GlobalVar.GV().StationNameList ,"Select or Search Origin",R.style.DialogAnimations_SmileWindow);
            else
                orgSpinnerDialog = new SpinnerDialog(GlobalVar.GV().activity,GlobalVar.GV().StationFNameList,"Select or Search Origin",R.style.DialogAnimations_SmileWindow);

            orgSpinnerDialog.bindOnSpinerListener(new OnSpinerItemClick()
            {
                @Override
                public void onClick(String item, int position)
                {
                    if (GlobalVar.GV().IsEnglish())
                        txtOrigin.setText(GlobalVar.GV().StationNameList.get(position));
                    else
                        txtOrigin.setText(GlobalVar.GV().StationFNameList.get(position));
                    txtPiecesCount.requestFocus();
                    OriginID =  GlobalVar.GV().StationList.get(position).ID;
                }
            });

            txtDestination.setOnFocusChangeListener(new View.OnFocusChangeListener()
            {
                @Override
                public void onFocusChange(View v, boolean hasFocus)
                {
                    if (hasFocus)
                        destSpinnerDialog.showSpinerDialog(false);
                }
            });

            if (GlobalVar.GV().IsEnglish())
                destSpinnerDialog = new SpinnerDialog(GlobalVar.GV().activity,GlobalVar.GV().StationNameList ,"Select or Search Destination",R.style.DialogAnimations_SmileWindow);
            else
                destSpinnerDialog = new SpinnerDialog(GlobalVar.GV().activity,GlobalVar.GV().StationFNameList,"Select or Search Destination",R.style.DialogAnimations_SmileWindow);
            destSpinnerDialog.bindOnSpinerListener(new OnSpinerItemClick()
            {
                @Override
                public void onClick(String item, int position)
                {
                    if (GlobalVar.GV().IsEnglish())
                        txtDestination.setText(GlobalVar.GV().StationNameList.get(position));
                    else
                        txtDestination.setText(GlobalVar.GV().StationFNameList.get(position));
                    txtPiecesCount.requestFocus();
                    DestinationID =  GlobalVar.GV().StationList.get(position).ID;
                }
            });

//                adapter1 = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,Cs);
//                orgSpinner.setAdapter(adapter1);
//                destSpinner.setAdapter(adapter1);

//                orgSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                    @Override
//                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
//                    {
//
//                    }
//
//                    @Override
//                    public void onNothingSelected(AdapterView<?> parent)
//                    {
//
//                    }
//                });
        }

        Button btnOpenCamera = (Button) rootView.findViewById(R.id.btnOpenCamera);
        btnOpenCamera.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!GlobalVar.GV().checkPermission(getActivity(), GlobalVar.PermissionType.Camera))
                {
                    GlobalVar.GV().ShowSnackbar(rootView, getString(R.string.NeedCameraPermission), GlobalVar.AlertType.Error);
                    GlobalVar.GV().askPermission(getActivity(), GlobalVar.PermissionType.Camera);
                }
                else
                {
                    Intent intent = new Intent(getContext().getApplicationContext(), NewBarCodeScanner.class);
                    startActivityForResult(intent, GlobalVar.GV().CAMERA_PERMISSION_REQUEST);
                }
            }
        });
        return rootView;
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
//                //final MediaPlayer barcodeSound = MediaPlayer.create(getContext().getApplicationContext(),R.raw.barcodescanned);
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

    //--------------Check if Waybill Already Picked Up -------------------------------
    public void CheckWaybillAlreadyPickedUp()
    {
        if (!GlobalVar.GV().HasInternetAccess)
            return;

        String WaybillNo = txtWaybillNo.getText().toString();
        if (WaybillNo.length() > 7)
        {
            CheckWaybillAlreadyPickedUpRequest checkWaybillAlreadyPickedUpRequest = new CheckWaybillAlreadyPickedUpRequest();
            checkWaybillAlreadyPickedUpRequest.WaybillNo = Integer.parseInt(WaybillNo);
            String jsonData = JsonSerializerDeserializer.serialize(checkWaybillAlreadyPickedUpRequest, true);
            new CheckWaybillAlreadyPickedUpInSystem().execute(jsonData);
        }
    }

    private class CheckWaybillAlreadyPickedUpInSystem extends AsyncTask<String,Void,String>
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
                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "CheckWaybillAlreadyPickedUp");
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
            super.onPostExecute(String.valueOf(finalJson));

            JSONObject jsonObject ;
            try
            {
                jsonObject = new JSONObject(finalJson);
                if (Boolean.parseBoolean(jsonObject.getString("hasPickedUp")))
                {
                    GlobalVar.GV().ShowSnackbar(GlobalVar.GV().rootView, "Already Picked Up", GlobalVar.AlertType.Error,true);
                    txtWaybillNo.setText("");
                }
                //HasError = Boolean.parseBoolean(jsonObject.getString("HasError"));
                //ErrorMessage = jsonObject.getString("ErrorMessage");
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }
}