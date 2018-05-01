package com.naqelexpress.naqelpointer.Activity.CheckCOD;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.Classes.JsonSerializerDeserializer;
import com.naqelexpress.naqelpointer.Classes.MainActivity;
import com.naqelexpress.naqelpointer.JSON.Request.CheckBeforeSubmitCODRequest;
import com.naqelexpress.naqelpointer.JSON.Results.CheckBeforeSubmitCODResult;
import com.naqelexpress.naqelpointer.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class CODCheckingActivity
        extends MainActivity
{
    EditText lbEmployID, lbDeliverySheetID, lbCashAmount, lbPOSAmount;
    Button btnCheck;
    TextView lbResult;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.codchecking);

        lbEmployID = (EditText) findViewById(R.id.lbEmployID);
        lbDeliverySheetID = (EditText) findViewById(R.id.lbDeliverySheetID);
        lbCashAmount = (EditText) findViewById(R.id.lbCashAmount);
        lbPOSAmount = (EditText) findViewById(R.id.lbPOSAmount);
        btnCheck = (Button) findViewById(R.id.btnCheck);
        lbResult = (TextView) findViewById(R.id.lbResult);

        lbEmployID.setText(String.valueOf(GlobalVar.GV().EmployID));
        btnCheck.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (isValid())
                    CheckCOD();
            }
        });
    }

    private boolean isValid()
    {
        boolean result = true;

        if (lbEmployID.getText().toString().equals(""))
        {
            result = false;
            GlobalVar.GV().ShowSnackbar(GlobalVar.GV().rootView, "You to enter the Employ ID", GlobalVar.AlertType.Error);
        }

        if (lbDeliverySheetID.getText().toString().equals(""))
        {
            result = false;
            GlobalVar.GV().ShowSnackbar(GlobalVar.GV().rootView, "You to enter the Delivery Sheet No", GlobalVar.AlertType.Error);
        }

        if (lbCashAmount.getText().toString().equals(""))
        {
            result = false;
            GlobalVar.GV().ShowSnackbar(GlobalVar.GV().rootView, "You to enter the Cash Amount", GlobalVar.AlertType.Error);
        }

        if (lbPOSAmount.getText().toString().equals(""))
        {
            result = false;
            GlobalVar.GV().ShowSnackbar(GlobalVar.GV().rootView, "You to enter the POS Amount", GlobalVar.AlertType.Error);
        }

        return result;
    }

    //--------------Checking COD -------------------------------
    public void CheckCOD()
    {
        if (!GlobalVar.GV().HasInternetAccess)
            return;

        CheckBeforeSubmitCODRequest checkBeforeSubmitCODRequest = new CheckBeforeSubmitCODRequest();
        checkBeforeSubmitCODRequest.EmployID = GlobalVar.GV().getIntegerFromString(lbEmployID.getText().toString());
        checkBeforeSubmitCODRequest.DeliverySheetID = GlobalVar.GV().getIntegerFromString(lbDeliverySheetID.getText().toString());
        if(!lbCashAmount.getText().toString().equals(""))
            checkBeforeSubmitCODRequest.TotalCash = GlobalVar.GV().getDoubleFromString(lbCashAmount.getText().toString());
        if(!lbPOSAmount.getText().toString().equals(""))
            checkBeforeSubmitCODRequest.TotalPOS = GlobalVar.GV().getDoubleFromString(lbPOSAmount.getText().toString());

        String jsonData = JsonSerializerDeserializer.serialize(checkBeforeSubmitCODRequest, true);
        new CheckCODData().execute(jsonData);
    }

    private class CheckCODData extends AsyncTask<String,Void,String>
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
                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "CheckBeforeSubmitCOD");
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
            CheckBeforeSubmitCODResult result = new CheckBeforeSubmitCODResult(finalJson);
            lbResult.setText(result.Notes);
        }
    }
}