package com.naqelexpress.naqelpointer.Activity.Login;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.google.android.gms.vision.barcode.Barcode;
import com.naqelexpress.naqelpointer.Activity.MainPage.MainPageActivity;
import com.naqelexpress.naqelpointer.Classes.BarcodeScan;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.Classes.MainActivity;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.DB.DBObjects.UserMeLogin;
import com.naqelexpress.naqelpointer.JSON.DataSync;
import com.naqelexpress.naqelpointer.JSON.Request.CheckNewVersionRequest;
import com.naqelexpress.naqelpointer.JSON.Results.CheckNewVersionResult;
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
import java.text.ParseException;

public class LoginActivity
        extends MainActivity
{
    DBConnections dbConnections;
    Context context;
    TextView lbVersion;
    Button btnLogin, btnForgotPassword, btnScan;
    EditText txtEmployID, txtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if(Created)
            return;
        Created = true;
        setContentView(R.layout.login);
        GlobalVar.GV().rootViewMainPage = mainRootView = findViewById(android.R.id.content);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnForgotPassword = (Button) findViewById(R.id.btnForgotPassword);
        btnScan = (Button) findViewById(R.id.btnScan);
        btnScan.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ScanEmployBarCode();
            }
        });

        context = this;
        lbVersion = (TextView) findViewById(R.id.lbVersion);
        String Version = getString(R.string.lbVersion) + GlobalVar.GV().AppVersion;
        lbVersion.setText(Version);
        this.dbConnections = new DBConnections(this,mainRootView);

        txtEmployID = (EditText) findViewById(R.id.txtEmployID);
        txtPassword = (EditText) findViewById(R.id.txtPassword);

        //TODO remove user name and password
       txtEmployID.setText("18110");
       txtPassword.setText("12345");
//        txtEmployID.setText("15304");
//        txtPassword.setText("123456");

        JSONObject jsonObject = new JSONObject();
        try
        {
            if (GlobalVar.GV().HasInternetAccess)
            {
                CheckNewVersionRequest checkNewVersionRequest = new CheckNewVersionRequest();
                jsonObject.put("AppSystemSettingID",checkNewVersionRequest.AppSystemSettingID);
                jsonObject.put("CurrentVersion",checkNewVersionRequest.CurrentVersion);
                jsonObject.put("AppTypeID",checkNewVersionRequest.AppTypeID);
                jsonObject.put("AppVersion",checkNewVersionRequest.AppVersion);
                jsonObject.put("LanguageID",checkNewVersionRequest.LanguageID);
                String jsonData = jsonObject.toString();

                new CheckNewVersion().execute(jsonData);
            }
            else
                GlobalVar.GV().ShowSnackbar(mainRootView,getString(R.string.NoInternetConnection), GlobalVar.AlertType.Warning);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        txtEmployID.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s)
            {
//                //#4#-1#Admin#RUH#Yemen#Correct#
//                int UserID = 0, EmployID = -5;
//                if (txtEmployID.getText().toString().startsWith("#") &&
//                        txtEmployID.getText().toString().endsWith("#Correct#"))
//                {
//                    String[] txt = txtEmployID.getText().toString().split("#");
//                    UserID = Integer.parseInt(txt[1]);
//                    EmployID = Integer.parseInt(txt[2]);
//
//                    if (EmployID >= -1 && UserID > 0)
//                    {
//                        Cursor result = dbConnections.Fill("select * from UserME where StatusID <> 3 and EmployID = " + String.valueOf(EmployID) );//+ " and ID =" + String.valueOf(UserID));
//                        if (result.getCount() > 0)
//                        {
//                            result.moveToFirst();
//                            GlobalVar.GV().UserID = Integer.valueOf(result.getString(result.getColumnIndex("ID")));
//                            GlobalVar.GV().EmployID = Integer.valueOf(result.getString(result.getColumnIndex("EmployID")));
//                            GlobalVar.GV().StationID = Integer.valueOf(result.getString(result.getColumnIndex("StationID")));
//
//                            UserMeLogin userMeLogin = new UserMeLogin(GlobalVar.GV().EmployID,1);
//                            dbConnections.InsertUserMeLogin(userMeLogin);
//                            dataSync.SendUserMeLoginsData();
//
//                           OpenMainPage();
//                        }
//                        else
//                            {
//                                dataSync.GetUserMEData(EmployID,"NoPass");
//                                GlobalVar.GV().ShowSnackbar(mainRootView,"Please check your barcode", GlobalVar.AlertType.Error);
//                            }
//                    }
//                    else
//                    {
//                        dataSync.GetUserMEData(EmployID,"NoPass");
//                        GlobalVar.GV().ShowSnackbar(mainRootView,"Please check your barcode", GlobalVar.AlertType.Error);
//                    }
//                }
//                else
//                {
//                    dataSync.GetUserMEData(Integer.parseInt(txtEmployID.getText().toString()),txtPassword.getText().toString());
//                    GlobalVar.GV().ShowSnackbar(mainRootView, "Please Check Your Employ ID and Password", GlobalVar.AlertType.Error);
//                }
            }
        });
    }

    public void OpenForgotPasswordActivity(View view)
    {
        Intent intent = new Intent(getApplicationContext(),ForgotPasswordActivity.class);
        startActivity(intent);
    }

    public void Login(View view) throws ParseException
    {
        GlobalVar.GV().UserPassword = txtPassword.getText().toString();
        if (GlobalVar.GV().ThereIsMandtoryVersion)
        {
            GlobalVar.GV().ShowSnackbar(mainRootView, "There is a new version, you have to install it first. Contact IT department if you need any support.", GlobalVar.AlertType.Warning);
            return;
        }

        if (txtEmployID.getText().toString().equals(""))
        {
            GlobalVar.GV().ShowSnackbar(mainRootView, "You have to enter the Employee ID", GlobalVar.AlertType.Warning);
            return;
        }

        if (txtPassword.getText().toString().equals(""))
        {
            GlobalVar.GV().ShowSnackbar(mainRootView, "You have to enter your password", GlobalVar.AlertType.Warning);
            return;
        }

        LoginIntoOpenMainPage();
    }

    private void LoginIntoOpenMainPage()
    {
        String empID = txtEmployID.getText().toString();
        String Password = txtPassword.getText().toString();

        Cursor result = GlobalVar.GV().dbConnections.Fill("select * from UserME where StatusID <> 3 and EmployID = " + empID + " and Password ='" + Password+"'");
        if (result.getCount() > 0)
        {
            result.moveToFirst();
//            int x1 = Integer.valueOf(result.getString(result.getColumnIndex("ID")));
            GlobalVar.GV().UserID = Integer.valueOf(result.getString(result.getColumnIndex("ID")));
            GlobalVar.GV().EmployID = Integer.valueOf(result.getString(result.getColumnIndex("EmployID")));
            GlobalVar.GV().StationID = Integer.valueOf(result.getString(result.getColumnIndex("StationID")));

//            try
//            {
//                String x = result.getString(result.getColumnIndex("MobileNo"));
//            }
//            catch (Exception e)
//            {
//                e.printStackTrace();
//            }

            try
            {
                GlobalVar.GV().EmployMobileNo = result.getString(result.getColumnIndex("MobileNo"));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }


            try
            {

                if (GlobalVar.GV().IsEnglish())
                {
                    GlobalVar.GV().EmployName = result.getString(result.getColumnIndex("EmployName"));
                    GlobalVar.GV().EmployStation = result.getString(result.getColumnIndex("StationName"));
                }
                else
                    {
                        GlobalVar.GV().EmployName = result.getString(result.getColumnIndex("EmployFName"));
                        GlobalVar.GV().EmployStation = result.getString(result.getColumnIndex("StationFName"));
                    }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            LoginToMainPage();
        }
        else
        {
            if ( GlobalVar.GV().HasInternetAccess )
            {
                DataSync dataSync = new DataSync();
                dataSync.GetUserMEData(Integer.parseInt(txtEmployID.getText().toString()), txtPassword.getText().toString());

                GlobalVar.GV().ShowSnackbar(mainRootView, "Please Check Your Employ ID and Password", GlobalVar.AlertType.Error);
            }
            else
                GlobalVar.GV().ShowSnackbar(mainRootView,"Please check the internet connection", GlobalVar.AlertType.Warning);
        }
    }

    private void LoginToMainPage()
    {
        UserMeLogin userMeLogin = new UserMeLogin(GlobalVar.GV().EmployID,1);
        dbConnections.InsertUserMeLogin(userMeLogin);
        OpenMainPage();
    }

    private void OpenMainPage()
    {
        if (GlobalVar.GV().ThereIsMandtoryVersion)
            GlobalVar.GV().ShowDialog(context,"New Version","There is a new version, Please update the system, or cordiante with IT department for updating your system.",true);
        else
        {
            Intent intent = new Intent(getApplicationContext(), MainPageActivity.class);
            startActivity(intent);

            finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == GlobalVar.GV().CAMERA_PERMISSION_REQUEST && resultCode == RESULT_OK)
        {
            if (data != null)
            {
                final Barcode barcode = data.getParcelableExtra("barcode");
                txtEmployID.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        String resultBarcode = barcode.displayValue;
                        GlobalVar.GV().MakeSound(context,R.raw.barcodescanned);

                        //#4#-1#Admin#RUH#Yemen#Correct#
                        int UserID, EmployID;
                        if (resultBarcode.startsWith("#") &&
                                resultBarcode.endsWith("#Correct#"))
                        {
                            String[] txt = resultBarcode.split("#");
                            UserID = Integer.parseInt(txt[1]);
                            EmployID = Integer.parseInt(txt[2]);

                            if (EmployID >= -1 && UserID > 0)
                            {
                                if (GlobalVar.GV().ThereIsMandtoryVersion)
                                {
                                    GlobalVar.GV().ShowDialog(context, "New Version", "There is a new version, Please update the system, or cordiante with IT department for updating your system.",false);
                                    return;
                                }

                                Cursor result = dbConnections.Fill("select * from UserME where StatusID <> 3 and EmployID = " + String.valueOf(EmployID) );//+ " and ID =" + String.valueOf(UserID));
                                if (result.getCount() > 0)
                                {
                                    result.moveToFirst();
                                    GlobalVar.GV().UserID = Integer.valueOf(result.getString(result.getColumnIndex("ID")));
                                    GlobalVar.GV().EmployID = Integer.valueOf(result.getString(result.getColumnIndex("EmployID")));
                                    GlobalVar.GV().StationID = Integer.valueOf(result.getString(result.getColumnIndex("StationID")));
                                    GlobalVar.GV().EmployMobileNo = result.getString(result.getColumnIndex("MobileNo"));
                                    if(GlobalVar.GV().IsEnglish())
                                    {
                                        GlobalVar.GV().EmployName = result.getString(result.getColumnIndex("EmployName"));
                                        GlobalVar.GV().EmployStation = result.getString(result.getColumnIndex("StationName"));
                                    }
                                    else
                                    {
                                        GlobalVar.GV().EmployName = result.getString(result.getColumnIndex("EmployFName"));
                                        GlobalVar.GV().EmployStation = result.getString(result.getColumnIndex("StationFName"));
                                    }

                                    LoginToMainPage();
                                }
                                else
                                {
                                    DataSync dataSync = new DataSync();
                                    dataSync.GetUserMEData(EmployID,"NoPass");
                                    GlobalVar.GV().ShowSnackbar(mainRootView,"Please check your barcode", GlobalVar.AlertType.Error);
                                }
                            }
                            else
                            {
                                DataSync dataSync = new DataSync();
                                dataSync.GetUserMEData(EmployID,"NoPass");
                                GlobalVar.GV().ShowSnackbar(mainRootView,"Please check your barcode", GlobalVar.AlertType.Error);
                            }
                        }
                    }
                });
            }
        }
    }

    public void ScanEmployBarCode()
    {
        if (!GlobalVar.GV().checkPermission(this, GlobalVar.PermissionType.Camera))
            GlobalVar.GV().askPermission(this, GlobalVar.PermissionType.Camera);
        else
        {
            Intent intent = new Intent(getApplicationContext(), BarcodeScan.class);
            startActivityForResult(intent, GlobalVar.GV().CAMERA_PERMISSION_REQUEST);
        }
    }

    private class CheckNewVersion extends AsyncTask<String,Void,String>
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
                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "CheckNewVersion");
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
                String line;
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
            CheckNewVersionResult checkNewVersionResult = new CheckNewVersionResult(finalJson);

            if(checkNewVersionResult.HasNewVersion)
                GlobalVar.GV().ShowDialog(context,"New Version","There is a new version, Please update the system, or cordiante with IT department for updating your system.",true);

            if (checkNewVersionResult.IsMandatory)
                GlobalVar.GV().ThereIsMandtoryVersion = true;

//                btnLogin.setVisibility(View.INVISIBLE);
//                btnScan.setVisibility(View.INVISIBLE);
//                txtEmployID.setVisibility(View.INVISIBLE);
//                txtPassword.setVisibility(View.INVISIBLE);
//                btnForgotPassword.setVisibility(View.INVISIBLE);
//            }

            super.onPostExecute(String.valueOf(finalJson));
        }
    }
}