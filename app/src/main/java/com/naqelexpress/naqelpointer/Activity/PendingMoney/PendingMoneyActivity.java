package com.naqelexpress.naqelpointer.Activity.PendingMoney;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.Classes.JsonSerializerDeserializer;
import com.naqelexpress.naqelpointer.Classes.MainActivity;
import com.naqelexpress.naqelpointer.JSON.Request.CheckPendingCODRequest;
import com.naqelexpress.naqelpointer.JSON.Results.CheckPendingCODResult;
import com.naqelexpress.naqelpointer.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class PendingMoneyActivity
        extends MainActivity
{
    private SwipeMenuListView listview;
    private PendingListAdapter adapter;
    Button btnCheck;
    EditText txtEmploy;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if(Created)
            return;
        Created = true;
        setContentView(R.layout.pendingmoney);

        listview = (SwipeMenuListView) findViewById(R.id.pendingCODView);
//        adapter = new PendingListAdapter(getApplicationContext(), GlobalVar.GV().checkPendingCODList);
//        listview.setAdapter(adapter);

        txtEmploy = (EditText) findViewById(R.id.txtEmployID);
        txtEmploy.setText(String.valueOf(GlobalVar.GV().EmployID));
        btnCheck = (Button) findViewById(R.id.btnCheck);
        btnCheck.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                GlobalVar.GV().checkPendingCODList = new ArrayList<>();
                if(!txtEmploy.getText().toString().equals(""))
                {
                    CheckPendingCODRequest checkPendingCODRequest = new CheckPendingCODRequest();
                    checkPendingCODRequest.EmployID = Integer.parseInt(txtEmploy.getText().toString());
                    BringPendingMoney(checkPendingCODRequest);
                }
            }
        });
    }

    //------------------------Bring Pending Money -------------------------------
    public void BringPendingMoney(CheckPendingCODRequest checkCODPendingRequest)
    {
        if (!GlobalVar.GV().HasInternetAccess)
            return;

        String jsonData = JsonSerializerDeserializer.serialize(checkCODPendingRequest, true);
        new BringPendingMoneyList().execute(jsonData);
    }

    private class BringPendingMoneyList extends AsyncTask<String,Void,String>
    {
        private ProgressDialog progressDialog;
        String result = "";
        StringBuffer buffer;

        @Override
        protected void onPreExecute()
        {
            progressDialog = ProgressDialog.show(GlobalVar.GV().context, "Please wait.", "Downloading Pending Money Details.", true);
        }

        @Override
        protected String doInBackground(String... params)
        {
            String jsonData = params[0];
            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try
            {
                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "CheckPendingCOD");
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
                String line ;
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
            {
                new CheckPendingCODResult(finalJson);

                adapter = new PendingListAdapter(getApplicationContext(), GlobalVar.GV().checkPendingCODList);
                listview.setAdapter(adapter);
            }
            else
                GlobalVar.GV().ShowSnackbar(mainRootView,getString(R.string.NoPendingCOD), GlobalVar.AlertType.Info);
        }
    }
}