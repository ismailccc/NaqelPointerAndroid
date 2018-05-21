package com.naqelexpress.naqelpointer.Activity.MainPage;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.naqelexpress.naqelpointer.Activity.CheckCOD.CODCheckingActivity;
import com.naqelexpress.naqelpointer.Activity.CheckPoints.CheckPointsActivity;
import com.naqelexpress.naqelpointer.Activity.Booking.BookingList;
import com.naqelexpress.naqelpointer.Activity.Booking.BookingDetailActivity;
import com.naqelexpress.naqelpointer.Activity.Delivery.DeliveryActivity;
import com.naqelexpress.naqelpointer.Activity.DeliverySheet.DeliverySheetActivity;
import com.naqelexpress.naqelpointer.Activity.Login.LoginActivity;
import com.naqelexpress.naqelpointer.Activity.MultiDelivery.MultiDeliveryActivity;
import com.naqelexpress.naqelpointer.Activity.MyAccount.MyAccountActivity;
import com.naqelexpress.naqelpointer.Activity.MyRoute.MyRouteActivity;
import com.naqelexpress.naqelpointer.Activity.NotDelivered.NotDeliveredActivity;
import com.naqelexpress.naqelpointer.Activity.PendingMoney.PendingMoneyActivity;
import com.naqelexpress.naqelpointer.Activity.PickUp.PickUpActivity;
import com.naqelexpress.naqelpointer.Activity.Settings.SettingActivity;
import com.naqelexpress.naqelpointer.Classes.NotificationHelper;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.Classes.MainActivity;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.DB.DBObjects.UserSettings;
import com.naqelexpress.naqelpointer.DB.DBObjects.UserMeLogin;
import com.naqelexpress.naqelpointer.JSON.DataSync;
import com.naqelexpress.naqelpointer.R;
import com.naqelexpress.naqelpointer.Activity.WaybillMeasurments.WaybillMeasurementActivity;

import org.joda.time.DateTime;
import org.joda.time.DurationFieldType;
import java.util.Timer;
import java.util.TimerTask;

public class MainPageActivity
        extends MainActivity
{
    private NotificationHelper noti;
    private static final int NOTI_PRIMARY1 = 1100;
    String[] cellTitle;
    private Timer myTimer;
    int cellIcon []= {
            R.drawable.deliverysheet,
            R.drawable.maplist,
            R.drawable.delivery,
            R.drawable.delivery,
            R.drawable.notdelivered,
            R.drawable.pickup,
            R.drawable.waybillmeasurement,
            R.drawable.settings,
            R.drawable.datasync,
            R.drawable.money,
            R.drawable.pendingcod,
            R.drawable.checkpoint,
            R.drawable.checkpoint
    };
    GridView gridView;
    FloatingActionButton btnSignOut;
    public DataSync dataSync;

    //TextView out
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if(Created)
            return;
        GlobalVar.GV().MainContext = this;
        GlobalVar.GV().SignedIn = true;
        Created = true;
        setContentView(R.layout.mainpage);
        GlobalVar.GV().rootViewMainPage = mainRootView = findViewById(android.R.id.content);

        cellTitle = new String[13];
        cellTitle[0] = getResources().getString(R.string.DeliverySheetActivity);
        cellTitle[1] = getResources().getString(R.string.MyRouteActivity);
        cellTitle[2] = getResources().getString(R.string.DeliveryActivity);
        cellTitle[3] = getResources().getString(R.string.MultiDeliveryActivity);
        cellTitle[4] = getResources().getString(R.string.NotDeliveredActivity);
        cellTitle[5] = getResources().getString(R.string.PickUpActivity);
        cellTitle[6] = getResources().getString(R.string.WaybillMeasurementActivity);
        cellTitle[7] = getResources().getString(R.string.SettingsActivity);
        cellTitle[8] = getResources().getString(R.string.DataSync);
        cellTitle[9] = getResources().getString(R.string.CODChecking);
        cellTitle[10] = getResources().getString(R.string.PendingCOD);
        cellTitle[11] = getResources().getString(R.string.CustomsClearance);
        cellTitle[12] = getResources().getString(R.string.BookingList);
        MainPageNavigation();

        GlobalVar.GV().context = this;
        GlobalVar.GV().dbConnections = new DBConnections(GlobalVar.GV().context,mainRootView);

        btnSignOut = (FloatingActionButton) findViewById(R.id.btnSignOut);
        btnSignOut.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(GlobalVar.GV().context);
                builder.setTitle("Logout")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton("OK",new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface,int which)
                            {
                                int id = GlobalVar.GV().dbConnections.getMaxID(" UserMeLogin where LogoutDate is NULL ");
                                UserMeLogin userMeLogin = new UserMeLogin(id);
                                GlobalVar.GV().dbConnections.UpdateUserMeLogout(userMeLogin);
                                finish();
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(intent);
                            }
                        }).setNegativeButton("Cancel",null).setCancelable(false);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        LoadUserSettings();

        Thread loadMasterDataThread = new Thread()
        {
            @Override
            public void run()
            {
                if (GlobalVar.GV().StationList.size() <= 0)
                    GlobalVar.GV().GetStationList(true);

                if (GlobalVar.GV().DeliveryStatusList.size() <= 0)
                    GlobalVar.GV().GetDeliveryStatusList(true);

                if (GlobalVar.GV().CheckPointTypeList.size() <= 0)
                    GlobalVar.GV().GetCheckPointTypeList(true);

                if (GlobalVar.GV().CheckPointTypeDetailList.size() <= 0)
                    GlobalVar.GV().GetCheckPointTypeDetailList(true,0);

                if (GlobalVar.GV().CheckPointTypeDDetailList.size() <= 0)
                    GlobalVar.GV().GetCheckPointTypeDDetailList(true,0);

                if (GlobalVar.GV().NoNeedVolumeReasonList.size() <= 0)
                    GlobalVar.GV().GetNoNeedVolumeReasonList(true);

                if (GlobalVar.GV().myBookingList.size() <= 0)
                    GlobalVar.GV().LoadMyBookingList("BookingDate",true);
            }
        };
        loadMasterDataThread.start();


//        final Handler handler = new Handler();
//        Timer timer = new Timer();
//        TimerTask doAsynchronousTask = new TimerTask() {
//            @Override
//            public void run() {
//                handler.post(new Runnable() {
//                    public void run() {
//                        try {
//                            //LoadNotification();
//                        } catch (Exception e) {
//                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT)
//                                    .show();
//                        }
//                    }
//                });
//            }
//        };
//        timer.schedule(doAsynchronousTask, 0, 50000);
    }

    private void LoadUserSettings()
    {
        int Count = GlobalVar.GV().dbConnections.getCount("UserSettings"," EmployID = " + String.valueOf(GlobalVar.GV().EmployID));
        if ( Count > 0 )
        {
            Cursor result = GlobalVar.GV().dbConnections.Fill("select * from UserSettings where EmployID = " + String.valueOf(GlobalVar.GV().EmployID));
            if (result.getCount() > 0)
            {
                result.moveToFirst();
                do
                {
                    int ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
                    int EmployID = Integer.parseInt(result.getString(result.getColumnIndex("EmployID")));
                    String IPAddress = result.getString(result.getColumnIndex("IPAddress"));
                    boolean ShowScaningCamera = Boolean.parseBoolean(result.getString(result.getColumnIndex("ShowScaningCamera")));
                    DateTime dateTime = DateTime.now().withFieldAdded(DurationFieldType.days(),-30);
                    dateTime = DateTime.parse(result.getString(result.getColumnIndex("LastBringMasterData")));

                    GlobalVar.GV().currentSettings = new UserSettings(ID,EmployID,IPAddress,ShowScaningCamera,dateTime);
                }
                while (result.moveToNext());
            }
        }
        else
        {
            GlobalVar.GV().currentSettings = new UserSettings("212.93.160.150", true);
            GlobalVar.GV().dbConnections.InsertSettings(GlobalVar.GV().currentSettings);
            GlobalVar.GV().currentSettings.ID = GlobalVar.GV().dbConnections.getMaxID("UserSettings");
        }
    }


    public void LoadNotification()
    {

        try{
            noti = new NotificationHelper(this);
            Notification.Builder nb = noti.getNotification1("dfsdfsdf", "Test Notification");
            Intent intent = new Intent(this, BookingDetailActivity.class);

            Bundle bundle = new Bundle();
            bundle.putString("ID", String.valueOf(25455));
            bundle.putString("WaybillNo","25464");
            intent.putExtras(bundle);


            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
            nb.setContentIntent(pendingIntent);
            noti.notify(NOTI_PRIMARY1, nb);
        }
        catch(Exception ex)
        {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT)
                    .show();
        }
    }



    public void MainPageNavigation()
    {
        gridView = (GridView) findViewById(R.id.gridView);
        MainPageCellAdapter adapter = new MainPageCellAdapter(MainPageActivity.this, cellIcon, cellTitle);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                switch (position)
                {
                    case 0:
                        Intent deliverySheet = new Intent(getApplicationContext(), DeliverySheetActivity.class);
                        startActivity(deliverySheet);
                        break;
                    case 1:
                        if (GlobalVar.GV().HasInternetAccess)
                        {
                            Intent mapList = new Intent(getApplicationContext(),MyRouteActivity.class);
                            startActivity(mapList);
                        }
                        else
                            GlobalVar.GV().ShowSnackbar(mainRootView,getString(R.string.NoInternetConnection), GlobalVar.AlertType.Error);
                        break;
                    case 2:
                        Intent delivery = new Intent(getApplicationContext(),DeliveryActivity.class);
                        startActivity(delivery);
                        break;
                    case 3:
                        Intent multiDelivery = new Intent(getApplicationContext(),MultiDeliveryActivity.class);
                        startActivity(multiDelivery);
                        break;
                    case 4:
                        Intent notDelivered = new Intent(getApplicationContext(),NotDeliveredActivity.class);
                        startActivity(notDelivered);
                        break;
                    case 5:
                        Intent pickup = new Intent(getApplicationContext(),PickUpActivity.class);
                        startActivity(pickup);
                        break;
                    case 6:
                        Intent waybillMeasurement = new Intent(getApplicationContext(),WaybillMeasurementActivity.class);
                        startActivity(waybillMeasurement);
                        //GlobalVar.GV().ShowSnackbar(mainRootView,"Under Constraction",GlobalVar.AlertType.Info);
                        break;
                    case 7:
                        Intent settings = new Intent(getApplicationContext(),SettingActivity.class);
                        startActivity(settings);
                        break;
                    case 8:
                        GlobalVar.GV().SyncData(GlobalVar.GV().context,mainRootView);
                        break;
                    case 9:
                        Intent checkCOD = new Intent(getApplicationContext(),CODCheckingActivity.class);
                        startActivity(checkCOD);
                        break;
                    case 10:
                        Intent pendingMoney = new Intent(getApplicationContext(),PendingMoneyActivity.class);
                        startActivity(pendingMoney);
                        break;
                    case 11:
                        Intent checkPoint = new Intent(getApplicationContext(),CheckPointsActivity.class);
                        startActivity(checkPoint);
                        break;
                    case 12:
                        Intent bookingList = new Intent(getApplicationContext(),BookingList.class);
                        startActivity(bookingList);
                        break;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.myAccount:
                Intent intent = new Intent(getApplicationContext(), MyAccountActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void ShowAlertMessage()
    {
        AlertDialog alertDialog = new AlertDialog.Builder(MainPageActivity.this).create();
        alertDialog.setTitle(getResources().getString(R.string.app_name));
        alertDialog.setMessage("Under Construction");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    @Override
    public void onBackPressed()
    {
        CloseActivity();
    }
}
