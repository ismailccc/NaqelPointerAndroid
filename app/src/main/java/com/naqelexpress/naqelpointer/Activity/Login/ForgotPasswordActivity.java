package com.naqelexpress.naqelpointer.Activity.Login;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.Classes.JsonSerializerDeserializer;
import com.naqelexpress.naqelpointer.Classes.MainActivity;
import com.naqelexpress.naqelpointer.JSON.Request.GetPasswordRequest;
import com.naqelexpress.naqelpointer.JSON.Results.DefaultResult;
import com.naqelexpress.naqelpointer.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ForgotPasswordActivity extends MainActivity
{
    private EditText txtEmployID;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgotpassword);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        txtEmployID = (EditText) findViewById(R.id.txtEmployID);
    }

    public void GetPassword(View view)
    {
        if (txtEmployID.getText().toString().equals(""))
        {
            GlobalVar.GV().ShowSnackbar(mainRootView,getString(R.string.EnterEmployID), GlobalVar.AlertType.Error);
            return;
        }

        GetPassword(Integer.parseInt(txtEmployID.getText().toString()));
    }

    //-------------Get Password ------------------------------------------
    public void GetPassword(int EmployID)
    {
        GetPasswordRequest getPasswordRequest = new GetPasswordRequest();
        getPasswordRequest.EmployID = EmployID;

        String jsonData = JsonSerializerDeserializer.serialize(getPasswordRequest, true);
        if (GlobalVar.GV().HasInternetAccess)
            new GetPasswordFromServer().execute(jsonData);
        else
            GlobalVar.GV().ShowSnackbar(mainRootView,getString(R.string.NoInternetConnection), GlobalVar.AlertType.Error);
    }

    private class GetPasswordFromServer extends AsyncTask<String,Void,String>
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
                URL url = new URL( GlobalVar.GV().NaqelPointerAPILink + "GetPassword");
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
//                int byteCharacters;
                String line;
                BufferedReader reader = new BufferedReader(new InputStreamReader(ist));
                buffer = new StringBuffer();

                while((line = reader.readLine())!= null)
                {
                    buffer.append(line);
                }
                return String.valueOf(buffer);
            }
            catch (Exception ignored)
            {
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
            DefaultResult defaultResult = new DefaultResult(finalJson);

            AlertDialog alertDialog = new AlertDialog.Builder(ForgotPasswordActivity.this).create();
            alertDialog.setTitle(getResources().getString(R.string.app_name));
            alertDialog.setMessage(defaultResult.ErrorMessage);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.dismiss();
                            finish();
                        }
                    });
            alertDialog.show();

            super.onPostExecute(String.valueOf(finalJson));
        }
    }
}