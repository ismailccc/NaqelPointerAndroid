package com.naqelexpress.naqelpointer.Activity.Login;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.view.WindowManager;
import android.widget.ImageView;
import com.naqelexpress.naqelpointer.Activity.MainPage.MainPageActivity;
import com.naqelexpress.naqelpointer.Classes.OnUpdateListener;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.Classes.MainActivity;
import com.naqelexpress.naqelpointer.DB.DBObjects.UserMeLogin;
import com.naqelexpress.naqelpointer.JSON.ProjectAsyncTask;
import com.naqelexpress.naqelpointer.R;
import org.joda.time.DateTime;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class SplashScreenActivity
        extends MainActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splashscreen);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ImageView imageView = (ImageView) findViewById(R.id.imageView);

        if (GlobalVar.GV().IsEnglish())
            imageView.setImageResource(R.drawable.naqellogowhite);
        else
            imageView.setImageResource(R.drawable.naqellogowhitear);

        Thread myThread = new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    ProjectAsyncTask task = new ProjectAsyncTask("SystemVariable/GetByVariableKey?VariableKey=NaqelPointerWebAPIURL", "Get","");
                    task.setUpdateListener(new OnUpdateListener()
                    {
                        public void onPostExecuteUpdate(String obj)
                        {
                            if(obj != "" && (obj.contains("http://") || obj.contains("https://")))
                            {
                                obj = obj.substring(1,obj.length() - 1);
                                GlobalVar.GV().NaqelPointerWebAPILink = obj;
                            }
                        }

                        public void onPreExecuteUpdate()
                        {
                            GlobalVar.GV().ShowSnackbar(GlobalVar.GV().rootView, "Checking Server Connection", GlobalVar.AlertType.Info);
                        }
                    });
                    task.execute();

                    sleep(2000);
                    loginPage();
                }
                catch(InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        };
        myThread.start();
    }

    void loginPage()
    {
        if (GlobalVar.GV().dbConnections.isColumnExist("UserMeLogin","LogedOut"))
        {
            Cursor result = GlobalVar.GV().dbConnections.Fill("select * from UserMeLogin where LogedOut is NULL or LogedOut = 0");
            if (result.getCount() > 1)
            {
                result.moveToFirst();
                do
                {
                    int ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
                    UserMeLogin userMeLogin = new UserMeLogin(ID);
                    GlobalVar.GV().dbConnections.UpdateUserMeLogout(userMeLogin);
                }
                while (result.moveToNext());

                OpenLoginPage();
            }
            else
                if (result.getCount() == 1)
                {
                    result.moveToFirst();
                    try
                    {
                        if(result.getPosition() != -1)
                        {
                            int ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
                            int EmployID = Integer.parseInt(result.getString(result.getColumnIndex("EmployID")));
                            DateTime Date = DateTime.parse(result.getString(result.getColumnIndex("Date")));
                            int counts =  DateTime.now().getDayOfMonth() - Date.getDayOfMonth();
                            if(counts >= 1)
                            {
                                UserMeLogin userMeLogin = new UserMeLogin(ID);
                                GlobalVar.GV().dbConnections.UpdateUserMeLogout(userMeLogin);
                                OpenLoginPage();
                            }
                            else
                            {
                                GlobalVar.GV().EmployID = EmployID;

                                Cursor cursor = GlobalVar.GV().dbConnections.Fill("select * from UserME where EmployID = " + EmployID);
                                if (cursor.getCount() > 0) {
                                    cursor.moveToFirst();
                                    do {
                                        GlobalVar.GV().UserID = Integer.parseInt(cursor.getString(cursor.getColumnIndex("ID")));
                                        GlobalVar.GV().StationID = Integer.parseInt(cursor.getString(cursor.getColumnIndex("StationID")));
                                        GlobalVar.GV().EmployMobileNo = cursor.getString(cursor.getColumnIndex("MobileNo"));
                                        if (GlobalVar.GV().IsEnglish()) {
                                            GlobalVar.GV().EmployName = cursor.getString(cursor.getColumnIndex("EmployName"));
                                            GlobalVar.GV().EmployStation = cursor.getString(cursor.getColumnIndex("StationName"));
                                        } else {
                                            GlobalVar.GV().EmployName = cursor.getString(cursor.getColumnIndex("EmployFName"));
                                            GlobalVar.GV().EmployStation = cursor.getString(cursor.getColumnIndex("StationFName"));
                                        }
                                    }
                                    while (cursor.moveToNext());

                                    GlobalVar.GV().GetUserPassword();
                                    Intent intent = new Intent(getApplicationContext(), MainPageActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else
                                    OpenLoginPage();
                            }
                        }
                        else
                            OpenLoginPage();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                else
                    OpenLoginPage();
        }
        else
            OpenLoginPage();
    }

    private void OpenLoginPage()
    {

        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }
}