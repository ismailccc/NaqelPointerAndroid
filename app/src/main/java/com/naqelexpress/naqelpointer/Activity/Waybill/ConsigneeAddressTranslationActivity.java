package com.naqelexpress.naqelpointer.Activity.Waybill;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.naqelexpress.naqelpointer.Classes.JsonSerializerDeserializer;
import com.naqelexpress.naqelpointer.Classes.MainActivity;
import com.naqelexpress.naqelpointer.Classes.OnSpinerItemClick;
import com.naqelexpress.naqelpointer.Classes.OnUpdateListener;
import com.naqelexpress.naqelpointer.Classes.SpinnerDialog;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.JSON.ProjectAsyncTask;
import com.naqelexpress.naqelpointer.JSON.Request.GTranslation;
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

/**
 * Created by sofan on 12/03/2018.
 */

public class ConsigneeAddressTranslationActivity
        extends MainActivity
{
    SpinnerDialog spinnerDialog;
    TextView txtTargetLanguage;
    private Bundle bundle;
    private String languageCode = "en";
    private TextView txtConsigneeName;
    private TextView txtAddress;
    private TextView txtSecondAddress;
    private TextView txtNear;
    private TextView txtMobileNo;
    private TextView txtPhoneNo;
    private TextView txtTranslationResult;
    private String AddressText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if(Created)
            return;
        Created = true;
        setContentView(R.layout.consigneeaddresstranslation);
        bundle = getIntent().getExtras();

        if (bundle != null)
        {
            int bid = Integer.parseInt(bundle.getString("ID"));
            String sWaybill = bundle.getString("WaybillNo");

            GetWaybillDetailsRequest getWaybillDetailsRequest = new GetWaybillDetailsRequest();
            getWaybillDetailsRequest.WaybillNo = Integer.parseInt(sWaybill.toString());

            JSONObject jsonObject = new JSONObject();
            try
            {
                if (GlobalVar.GV().HasInternetAccess) {
                    jsonObject.put("WaybillNo", getWaybillDetailsRequest.WaybillNo);

                    jsonObject.put("AppTypeID", getWaybillDetailsRequest.AppTypeID);
                    jsonObject.put("AppVersion", getWaybillDetailsRequest.AppVersion);
                    jsonObject.put("LanguageID", getWaybillDetailsRequest.LanguageID);
                    String jsonData = jsonObject.toString();

                    new GetWaybillDetailsInfos().execute(jsonData);
                }
//                else
//                    GlobalVar.GV().ShowSnackbar(rootView, getString(R.string.NoInternetConnection), GlobalVar.AlertType.Error);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        txtConsigneeName = (TextView) findViewById(R.id.txtConsigneeName);
        txtAddress = (TextView) findViewById(R.id.txtAddress);
        txtSecondAddress = (TextView) findViewById(R.id.txtSecondAddress);
        txtNear = (TextView) findViewById(R.id.txtNear);
        txtMobileNo = (TextView) findViewById(R.id.txtMobileNo);
        txtPhoneNo = (TextView) findViewById(R.id.txtPhoneNo);
        txtTranslationResult = (TextView) findViewById(R.id.txtTranslationResult);

        txtTargetLanguage = (TextView) findViewById(R.id.txtTargetLanguage);
        txtTargetLanguage.setInputType(InputType.TYPE_NULL);
        txtTargetLanguage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                spinnerDialog.showSpinerDialog(false);
            }
        });

//        txtTargetLanguage.setOnFocusChangeListener(new View.OnFocusChangeListener()
//        {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus)
//            {
//                if (hasFocus)
//                    spinnerDialog.showSpinerDialog(false);
//            }
//        });

        Button btnTranslate = (Button) findViewById(R.id.btnTranslate);
        btnTranslate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                GTranslation gTranslation = new GTranslation(AddressText,languageCode);
                String jsonData = JsonSerializerDeserializer.serialize(gTranslation, true);
                ProjectAsyncTask task = new ProjectAsyncTask("Common/getGoogleTranslation", "Post",jsonData);
                task.setUpdateListener(new OnUpdateListener()
                {
                    public void onPostExecuteUpdate(String obj)
                    {
                        txtTranslationResult.setText(obj);
                    }

                    public void onPreExecuteUpdate()
                    {
                        GlobalVar.GV().ShowSnackbar(GlobalVar.GV().rootView, "Start Translation Address Data", GlobalVar.AlertType.Info);
                    }
                });
                task.execute();
            }
        });

        spinnerDialog = new SpinnerDialog(GlobalVar.GV().activity,GlobalVar.GV().LanguageNameList ,"Select Language",R.style.DialogAnimations_SmileWindow);
        spinnerDialog.bindOnSpinerListener(new OnSpinerItemClick()
        {
            @Override
            public void onClick(String item, int position)
            {
                txtTargetLanguage.setText(GlobalVar.GV().LanguageNameList.get(position));
                languageCode =  GlobalVar.GV().LanguageList.get(position).Code;
            }
        });

        //btnTranslate
    }

    private class GetWaybillDetailsInfos extends AsyncTask<String,Void,String>
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
                try
                {
                    if (ist != null)
                        ist.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                try
                {
                    if (dos != null)
                        dos.close();
                }
                catch (IOException e)
                {
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

            if (GlobalVar.GV().IsEnglish())
                txtConsigneeName.setText(getResources().getString(R.string.txtConsigneeName) + waybillDetailsResult.ConsigneeName );
            else
                txtConsigneeName.setText(getResources().getString(R.string.txtConsigneeName) + waybillDetailsResult.ConsigneeName );
            txtAddress.setText(getResources().getString(R.string.txtAddress) + waybillDetailsResult.Address);

            txtSecondAddress.setText(getResources().getString(R.string.txtSecondAddress) + waybillDetailsResult.SecondLine);
            txtNear.setText(getResources().getString(R.string.txtNear) + waybillDetailsResult.Near);
            txtMobileNo.setText(getResources().getString(R.string.txtMobileNo) + waybillDetailsResult.MobileNo);
            txtPhoneNo.setText(getResources().getString(R.string.txtPhoneNo) + waybillDetailsResult.PhoneNo);

            AddressText = txtAddress.getText().toString();
            AddressText += " " + txtSecondAddress.getText().toString();
            AddressText += " " + txtNear.getText().toString();

            super.onPostExecute(String.valueOf(finalJson));
        }
    }
}