package com.naqelexpress.naqelpointer;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.naqelexpress.naqelpointer.Classes.JsonSerializerDeserializer;
import com.naqelexpress.naqelpointer.Classes.Languages;
import com.naqelexpress.naqelpointer.Classes.OnUpdateListener;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.DB.DBObjects.Booking;
import com.naqelexpress.naqelpointer.DB.DBObjects.CheckPointType;
import com.naqelexpress.naqelpointer.DB.DBObjects.CheckPointTypeDDetail;
import com.naqelexpress.naqelpointer.DB.DBObjects.CheckPointTypeDetail;
import com.naqelexpress.naqelpointer.DB.DBObjects.DeliveryStatus;
import com.naqelexpress.naqelpointer.DB.DBObjects.MyRouteShipments;
import com.naqelexpress.naqelpointer.DB.DBObjects.NoNeedVolumeReason;
import com.naqelexpress.naqelpointer.DB.DBObjects.Station;
import com.naqelexpress.naqelpointer.DB.DBObjects.UserSettings;
import com.naqelexpress.naqelpointer.JSON.DataSync;
import com.naqelexpress.naqelpointer.JSON.ProjectAsyncTask;
import com.naqelexpress.naqelpointer.JSON.Request.DataTableParameters;
import com.naqelexpress.naqelpointer.JSON.Results.CheckPendingCODResult;
import com.naqelexpress.naqelpointer.JSON.Results.CheckPointTypeResult;
import com.naqelexpress.naqelpointer.JSON.Results.GetShipmentForPickingResult;

import org.joda.time.DateTime;

import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Calendar;

public class GlobalVar
{
    public UserSettings currentSettings;
    public String AppVersion = "1.0";
    private String WebServiceVersion = "2.0";
    public int AppID = 6;
    public int AppTypeID = 1;
    public boolean ThereIsMandtoryVersion = false;
    public String NaqelPointerAPILink = "http://212.93.160.150/NaqelAPIServices/RouteOptimization/2.0/WCFRouteOptimization.svc/";
    //public String NaqelPointerWebAPILink = "http://212.93.160.150/NaqelAPIServices/InfoTrackWebAPI/1.0/API/";
    public String NaqelPointerWebAPILink = "https://infotrack.naqelexpress.com/NaqelAPIServices/RouteOptimization/3.3/API/";
    public int CourierDailyRouteID = 0;
    public String EmployName = "";
    public String EmployMobileNo = "";
    public String EmployStation = "";
    public boolean SignedIn = false;

    public int EmployID = 0;
    public int UserID = 0;

    public String UserPassword = "";
    public int StationID = 0;

    public LatLng currentLocation = new LatLng(0, 0);
    public String MachineID = "SmartPhone";
    public boolean HasInternetAccess = false;
    public final int CAMERA_PERMISSION_REQUEST = 100;

    public DBConnections dbConnections;
    public Context context;
    public Context MainContext;
    public View rootView;
    public View rootViewMainPage;
    public Activity activity;
    private static GlobalVar gv;
//    private ArrayList<String> DataTypeList = new ArrayList<>();

    public static GlobalVar GV()
    {
        if (GlobalVar.gv == null)
        {
            GlobalVar.gv = new GlobalVar();
            GlobalVar.gv.Init();
        }
        return gv;
    }

    public ArrayList<Languages> LanguageList = new ArrayList<>();
    public ArrayList<String> LanguageNameList = new ArrayList<>();
    public ArrayList<Booking> myBookingList;
    public ArrayList<MyRouteShipments> myRouteShipmentList;
    public ArrayList<CheckPendingCODResult> checkPendingCODList;
    public ArrayList<String> optimizedOutOfDeliveryShipmentList;
    public ArrayList<CheckPointTypeResult> checkPointTypeResultsList;
    private void Init()
    {
        GlobalVar.GV().MachineID = GlobalVar.GV().getManufacturerSerialNumber();
        GlobalVar.GV().myBookingList = new ArrayList<Booking>();
       GlobalVar.GV().myRouteShipmentList = new ArrayList<>();
//        GlobalVar.GV().checkPendingCODList = new ArrayList<>();
        GlobalVar.GV().NaqelPointerAPILink = "http://212.93.160.150/NaqelAPIServices/RouteOptimization/"+GlobalVar.GV().WebServiceVersion+"/WCFRouteOptimization.svc/";
//        GlobalVar.GV().DataTypeList.add("Delivery");
//        GlobalVar.GV().DataTypeList.add("NotDelivered");

        GlobalVar.GV().LanguageList.add(new Languages("ar","عربي"));
        GlobalVar.GV().LanguageList.add(new Languages("en","English"));
        GlobalVar.GV().LanguageList.add(new Languages("tl","Filipino"));
        GlobalVar.GV().LanguageList.add(new Languages("hi","Hindi"));
        GlobalVar.GV().LanguageList.add(new Languages("ne","Nepali"));
        GlobalVar.GV().LanguageList.add(new Languages("ta","Tamil"));
        GlobalVar.GV().LanguageList.add(new Languages("ur","Urdu"));
    }

    private String getManufacturerSerialNumber()
    {
        String serial = null;
        try
        {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class, String.class);
            serial = (String) get.invoke(c, "ril.serialnumber", "unknown");
        }
        catch (Exception ignored){}
        return serial;
    }

    public boolean IsEnglish()
    {
        return Locale.getDefault().getLanguage().equals("en");
    }

    public int GetLanguageID()
    {
        if (IsEnglish())
            return 1;
        else
            return 2;
    }

    public enum AlertType
    {
        Info,
        Warning,
        Error
    }

//    public void ShowMessage(Context context, String Message, AlertType alertType) {
//        Toast toast = Toast.makeText(context, Message, Toast.LENGTH_LONG);
//        View view = toast.getView();
//        TextView text = (TextView) view.findViewById(android.R.id.message);
//
//        if (alertType == AlertType.Error) {
//            view.setBackgroundResource(R.color.NaqelRed);
//            text.setTextColor(Color.WHITE);
//        } else if (alertType == AlertType.Info) {
//            view.setBackgroundResource(R.color.NaqelBlue);
//            text.setTextColor(Color.WHITE);
//        } else if (alertType == AlertType.Warning) {
//            view.setBackgroundResource(R.color.NaqelRed);
//            text.setTextColor(Color.WHITE);
//        }
//        toast.show();
//    }

    public void ShowSnackbar(View view, String Message, AlertType alertType)
    {
        Snackbar snackbar = Snackbar.make(view, Message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
//        TextView text = (TextView) view.findViewById(android.R.id.message);

        if (alertType == AlertType.Error)
        {
            sbView.setBackgroundResource(R.color.NaqelRed);
        } else if (alertType == AlertType.Info) {
            sbView.setBackgroundResource(R.color.NaqelBlue);
        } else if (alertType == AlertType.Warning) {
            sbView.setBackgroundResource(R.color.NaqelBlue);
        }
        snackbar.show();
    }

    public void ShowSnackbar(View view, String Message, AlertType alertType, boolean Playsound)
    {
        Snackbar snackbar = Snackbar.make(view, Message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();

        if (alertType == AlertType.Error)
        {
            sbView.setBackgroundResource(R.color.NaqelRed);
            if (Playsound)
                MakeSound(GlobalVar.GV().context, R.raw.wrongbarcodescan);
        }
        else
            if (alertType == AlertType.Info)
            {
                sbView.setBackgroundResource(R.color.NaqelBlue);
            }
            else
                if (alertType == AlertType.Warning)
                {
                    sbView.setBackgroundResource(R.color.NaqelRed);
                }
        snackbar.show();
    }

    public void ShowDialog(Context context, String title, String Message, boolean Cancelable)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(Cancelable);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }

//    public void ShowDialogCustom(Context context, String title, String Message)
//    {
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setCancelable(true);
//        builder.setTitle(title);
//        builder.setMessage(Message);
//        View view = LayoutInflater.from(context).inflate(R.layout.activity_dialog, null);
//        builder.setView(view);
//        builder.show();
//    }

    public void MakeSound(Context context, int url)
    {
        MediaPlayer barcodeSound = MediaPlayer.create(context, url);
        barcodeSound.start();
    }

    public enum PermissionType
    {
        AccessFindLocation,
        Camera,
        Phone,
        Storage
    }

    // Check for permission to access Location
    public boolean checkPermission(Activity activity,PermissionType permissionType)
    {
        if (permissionType == PermissionType.AccessFindLocation)
        {
            return (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) &&
                    (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED);
        }
        else
            if (permissionType == PermissionType.Camera)
            {
                return (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED);
            }
            else
                if (permissionType == PermissionType.Phone)
                {
                    return (ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE)
                            == PackageManager.PERMISSION_GRANTED);
                }
                else
                if (permissionType == PermissionType.Storage)
                {
                    return (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) &&
                            (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                                    == PackageManager.PERMISSION_GRANTED);
                }
        return false;
    }

    // Asks for permission
    public void askPermission(Activity activity, PermissionType permissionType)
    {
        if (permissionType == PermissionType.AccessFindLocation)
        {
            int LOCATION_PERMISSION_REQUEST = 200;
            ActivityCompat.requestPermissions(
                    activity,
                    new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                    LOCATION_PERMISSION_REQUEST
            );
        }
        else
            if (permissionType == PermissionType.Camera)
            {
                ActivityCompat.requestPermissions(
                        activity,
                        new String[] { Manifest.permission.CAMERA },
                        CAMERA_PERMISSION_REQUEST
                );
            }
            else
                if (permissionType == PermissionType.Phone)
                {
                    int PHONE_PERMISSION_REQUEST = 300;
                    ActivityCompat.requestPermissions(
                            activity,
                            new String[] { Manifest.permission.CALL_PHONE},
                            PHONE_PERMISSION_REQUEST
                    );
                }
    }

    public String GetStationByID(int stationID)
    {
        String result = "";

        for (int i = 0;i < GlobalVar.GV().StationNameList.size();i++)
        {
            if (GlobalVar.GV().StationList.get(i).ID == stationID)
            {
                result = GlobalVar.GV().StationList.get(i).Code + " : " + GlobalVar.GV().StationList.get(i).Name;
                break;
            }
        }

        return result;
    }

    public ArrayList<GetShipmentForPickingResult> GetShipmentForPickingResultList;
    public ArrayList<Station> StationList = new ArrayList<>();
    public ArrayList<String> StationNameList = new ArrayList<>();
    public ArrayList<String> StationFNameList = new ArrayList<>();
    public void GetStationList(boolean bringFromServer)
    {
        GlobalVar.GV().StationList.clear();// = new ArrayList<>();
        GlobalVar.GV().StationNameList.clear();// = new ArrayList<>();
        GlobalVar.GV().StationFNameList.clear();// = new ArrayList<>();
        Cursor result = dbConnections.Fill("select * from Station");
        if (result.getCount() > 0)
        {
            result.moveToFirst();
            do
            {
                int ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
                String Code = result.getString(result.getColumnIndex("Code"));
                String Name = result.getString(result.getColumnIndex("Name"));
                String FName = result.getString(result.getColumnIndex("FName"));
                int CountryID = Integer.parseInt(result.getString(result.getColumnIndex("CountryID")));

                Station instance = new Station(ID,Code,Name,FName,CountryID);
                GlobalVar.GV().StationList.add(instance);
                GlobalVar.GV().StationNameList.add(Code + " : " + Name);
                GlobalVar.GV().StationFNameList.add(FName);
            }
            while (result.moveToNext());
        }

        com.naqelexpress.naqelpointer.JSON.DataSync dataSync = new DataSync();
        DateTime dt = DateTime.now();
        int dayOfMonth = dt.getDayOfMonth();
        if ( GlobalVar.GV().StationList.size() <= 0 || dayOfMonth % 3 == 0)
            if ( GlobalVar.GV().StationList.size() <= 0 || (bringFromServer && LastBringMasterData() > 0))
            {
                if (!GlobalVar.GV().HasInternetAccess)
                    return;
                //dataSync.GetStation();
                DataTableParameters dataTableParameters = new DataTableParameters();
                dataTableParameters.AppID = GlobalVar.GV().AppID;
                dataTableParameters.FilterString = "ID > 0";
                dataTableParameters.Length = 5000;
                dataTableParameters.Source = "ViwStation";
                dataTableParameters.Start = 0;

                String jsonData = JsonSerializerDeserializer.serialize(dataTableParameters, true);
                ProjectAsyncTask task = new ProjectAsyncTask("View/GetData", "Post",jsonData);
                task.setUpdateListener(new OnUpdateListener()
                {
                    public void onPostExecuteUpdate(String obj)
                    {
                        new Station(obj,GlobalVar.GV().rootViewMainPage);
                    }

                    public void onPreExecuteUpdate()
                    {
                        GlobalVar.GV().ShowSnackbar(GlobalVar.GV().rootView, "Loading Station List", AlertType.Info);
                    }
                });
                task.execute();
            }
    }

    /*InputStream ByGetMethod(String ServerURL)
    {
        InputStream DataInputStream = null;
        try {

            URL url = new URL(ServerURL);
            HttpURLConnection cc = (HttpURLConnection)
                    url.openConnection();
            //set timeout for reading InputStream
            cc.setReadTimeout(5000);
            // set timeout for connection
            cc.setConnectTimeout(5000);
            //set HTTP method to GET
            //cc.setRequestMethod("GET");
            //set it to true as we are connecting for input
            cc.setDoInput(true);

            //reading HTTP response code
            int response = cc.getResponseCode();

            //if response code is 200 / OK then read Inputstream
            if (response == HttpURLConnection.HTTP_OK) {
                DataInputStream = cc.getInputStream();
            }

        }
        catch (Exception e)
        {
            System.out.println(e);
            //Log.e(LOG_TAG, "Error in GetData", e);
        }
        return DataInputStream;

    }

    InputStream ByPostMethod(String ServerURL)
    {
        InputStream DataInputStream = null;
        try {

            //Post parameters
            String PostParam = "first_name=android&amp;last_name=pala";

            //Preparing
            URL url = new URL(ServerURL);

            HttpURLConnection cc = (HttpURLConnection)
                    url.openConnection();
            //set timeout for reading InputStream
            cc.setReadTimeout(5000);
            // set timeout for connection
            cc.setConnectTimeout(5000);
            //set HTTP method to POST
            cc.setRequestMethod("POST");
            //set it to true as we are connecting for input
            cc.setDoInput(true);
            //opens the communication link
            cc.connect();

            //Writing data (bytes) to the data output stream
            DataOutputStream dos = new DataOutputStream(cc.getOutputStream());
            dos.writeBytes(PostParam);
            //flushes data output stream.
            dos.flush();
            dos.close();

            //Getting HTTP response code
            int response = cc.getResponseCode();

            //if response code is 200 / OK then read Inputstream
            //HttpURLConnection.HTTP_OK is equal to 200
            if(response == HttpURLConnection.HTTP_OK)
            {
                DataInputStream = cc.getInputStream();
            }

        }
        catch (Exception e)
        {
            //Log.e(LOG_TAG, "Error in GetData", e);
        }
        return DataInputStream;

    }

    String ConvertStreamToString(InputStream stream)
    {

        InputStreamReader isr = new InputStreamReader(stream);
        BufferedReader reader = new BufferedReader(isr);
        StringBuilder response = new StringBuilder();

        String line = null;
        try {

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

        }
        catch (IOException e)
        {
            //Log.e(LOG_TAG, "Error in ConvertStreamToString", e);
        }
        catch (Exception e)
        {
            //Log.e(LOG_TAG, "Error in ConvertStreamToString", e);
        }
        finally
        {

            try {
                stream.close();

            } catch (IOException e)
            {
                //Log.e(LOG_TAG, "Error in ConvertStreamToString", e);

            } catch (Exception e)
            {
                //Log.e(LOG_TAG, "Error in ConvertStreamToString", e);
            }
        }
        return response.toString();


    }

    public void DisplayMessage(String a)
    {
        //TxtResult = (TextView) findViewById(R.id.response);
        //TxtResult.setText(a);
    }

    public class MakeNetworkCall extends AsyncTask<String, Void, String>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            DisplayMessage("Please Wait ...");
        }

        @Override
        protected String doInBackground(String... arg)
        {
            InputStream is = null;
            String URL = arg[0];
            String res = "";

            if (arg[1].equals("Post"))
            {
                is = ByPostMethod(URL);
            }
            else
            {
                is = ByGetMethod(URL);
            }

            if (is != null)
            {
                res = ConvertStreamToString(is);
            }
            else
            {
                res = "Something went wrong";
            }

            return res;
        }

        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);

            DisplayMessage(result);
        }
    }*/

    public ArrayList<DeliveryStatus> DeliveryStatusList = new ArrayList<>();
    public ArrayList<String> DeliveryStatusNameList = new ArrayList<>();
    public ArrayList<String> DeliveryStatusFNameList = new ArrayList<>();
    public void GetDeliveryStatusList(boolean bringFromServer)
    {
        GlobalVar.GV().DeliveryStatusList.clear();// = new ArrayList<>();
        GlobalVar.GV().DeliveryStatusNameList.clear();// = new ArrayList<>();
        GlobalVar.GV().DeliveryStatusFNameList.clear();// = new ArrayList<>();

        Cursor result = dbConnections.Fill("select * from DeliveryStatus");
        if (result.getCount() > 0)
        {
            result.moveToFirst();
            do
            {
                int ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
                String Code = result.getString(result.getColumnIndex("Code"));
                String Name = result.getString(result.getColumnIndex("Name"));
                String FName = result.getString(result.getColumnIndex("FName"));

                DeliveryStatus instance = new DeliveryStatus(ID,Code,Name,FName);
                GlobalVar.GV().DeliveryStatusList.add(instance);
                GlobalVar.GV().DeliveryStatusNameList.add(Name);
                GlobalVar.GV().DeliveryStatusFNameList.add(FName);
            }
            while (result.moveToNext());
        }

        com.naqelexpress.naqelpointer.JSON.DataSync dataSync = new DataSync();
        if( GlobalVar.GV().DeliveryStatusList.size() <= 0 || (bringFromServer && LastBringMasterData() > 0))
            dataSync.GetDeliveryStatus();
    }

    public ArrayList<CheckPointType> CheckPointTypeList = new ArrayList<>();
    public ArrayList<String> CheckPointTypeNameList = new ArrayList<>();
    public ArrayList<String> CheckPointTypeFNameList = new ArrayList<>();
    public void GetCheckPointTypeList(boolean bringFromServer)
    {
        GlobalVar.GV().CheckPointTypeList.clear();// = new ArrayList<>();
        GlobalVar.GV().CheckPointTypeNameList.clear();// = new ArrayList<>();
        GlobalVar.GV().CheckPointTypeFNameList.clear();// = new ArrayList<>();

        Cursor result = dbConnections.Fill("select * from CheckPointType");
        if (result.getCount() > 0)
        {
            result.moveToFirst();
            do
            {
                int ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
                String Name = result.getString(result.getColumnIndex("Name"));
                String FName = result.getString(result.getColumnIndex("FName"));

                CheckPointType instance = new CheckPointType(ID,Name,FName);
                GlobalVar.GV().CheckPointTypeList.add(instance);
                GlobalVar.GV().CheckPointTypeNameList.add(Name);
                GlobalVar.GV().CheckPointTypeFNameList.add(FName);
            }
            while (result.moveToNext());
        }

        com.naqelexpress.naqelpointer.JSON.DataSync dataSync = new DataSync();
        if( GlobalVar.GV().CheckPointTypeList.size() <= 0 || (bringFromServer && LastBringMasterData() > 0))
        {
            if (!GlobalVar.GV().HasInternetAccess)
                return;
            //dataSync.GetCheckPointType();

            ProjectAsyncTask task = new ProjectAsyncTask("CheckPointType/Get", "Get");
            task.setUpdateListener(new OnUpdateListener()
            {
                public void onPostExecuteUpdate(String obj)
                {
                    new CheckPointType(obj,GlobalVar.GV().rootViewMainPage);
                }

                public void onPreExecuteUpdate(){}
            });
            task.execute();
        }
    }

    public ArrayList<CheckPointTypeDetail> CheckPointTypeDetailList = new ArrayList<>();
    public ArrayList<String> CheckPointTypeDetailNameList = new ArrayList<>();
    public ArrayList<String> CheckPointTypeDetailFNameList = new ArrayList<>();
    public void GetCheckPointTypeDetailList(boolean bringFromServer, int checkPointTypeID)
    {
        GlobalVar.GV().CheckPointTypeDetailList.clear();// = new ArrayList<>();
        GlobalVar.GV().CheckPointTypeDetailNameList.clear();// = new ArrayList<>();
        GlobalVar.GV().CheckPointTypeDetailFNameList.clear();// = new ArrayList<>();

        String selectCommand = "select * from CheckPointTypeDetail";
        if (checkPointTypeID > 0)
            selectCommand+= " where CheckPointTypeID=" + checkPointTypeID;

        Cursor result = dbConnections.Fill(selectCommand);
        if ( result.getCount() > 0 )
        {
            result.moveToFirst();
            do
            {
                int ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
                String Name = result.getString(result.getColumnIndex("Name"));
                String FName = result.getString(result.getColumnIndex("FName"));
                int CheckPointTypeID = Integer.parseInt(result.getString(result.getColumnIndex("CheckPointTypeID")));

                CheckPointTypeDetail instance = new CheckPointTypeDetail(ID,Name,FName,CheckPointTypeID);
                GlobalVar.GV().CheckPointTypeDetailList.add(instance);
                GlobalVar.GV().CheckPointTypeDetailNameList.add(Name);
                GlobalVar.GV().CheckPointTypeDetailFNameList.add(FName);
            }
            while (result.moveToNext());
        }

        com.naqelexpress.naqelpointer.JSON.DataSync dataSync = new DataSync();
        if( GlobalVar.GV().CheckPointTypeDetailList.size() <= 0 || (bringFromServer && LastBringMasterData() > 0))
        {
            if (!GlobalVar.GV().HasInternetAccess)
                return;
            //dataSync.GetCheckPointTypeDetail();

            ProjectAsyncTask task = new ProjectAsyncTask("CheckPointTypeDetail/Get", "Get");
            task.setUpdateListener(new OnUpdateListener()
            {
                public void onPostExecuteUpdate(String obj)
                {
                    new CheckPointTypeDetail(obj,GlobalVar.GV().rootViewMainPage);
                }

                public void onPreExecuteUpdate(){}
            });
            task.execute();
        }
    }

    public ArrayList<CheckPointTypeDDetail> CheckPointTypeDDetailList = new ArrayList<>();
    public ArrayList<String> CheckPointTypeDDetailNameList = new ArrayList<>();
    public ArrayList<String> CheckPointTypeDDetailFNameList = new ArrayList<>();
    public void GetCheckPointTypeDDetailList(boolean bringFromServer, int checkPointTypeDetailID)
    {
        GlobalVar.GV().CheckPointTypeDDetailList.clear();// = new ArrayList<>();
        GlobalVar.GV().CheckPointTypeDDetailNameList.clear();// = new ArrayList<>();
        GlobalVar.GV().CheckPointTypeDDetailFNameList.clear();// = new ArrayList<>();

        String selectCommand = "select * from CheckPointTypeDDetail";
        if (checkPointTypeDetailID > 0)
            selectCommand+= " where CheckPointTypeDetailID=" + checkPointTypeDetailID;

        Cursor result = dbConnections.Fill(selectCommand);
        if (result.getCount() > 0)
        {
            result.moveToFirst();
            do
            {
                int ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
                String Name = result.getString(result.getColumnIndex("Name"));
                String FName = result.getString(result.getColumnIndex("FName"));
                int CheckPointTypeDetailID = Integer.parseInt(result.getString(result.getColumnIndex("CheckPointTypeDetailID")));

                CheckPointTypeDDetail instance = new CheckPointTypeDDetail(ID,Name,FName,CheckPointTypeDetailID);
                GlobalVar.GV().CheckPointTypeDDetailList.add(instance);
                GlobalVar.GV().CheckPointTypeDDetailNameList.add(Name);
                GlobalVar.GV().CheckPointTypeDDetailFNameList.add(FName);
            }
            while (result.moveToNext());
        }

        com.naqelexpress.naqelpointer.JSON.DataSync dataSync = new DataSync();
        if( GlobalVar.GV().CheckPointTypeDDetailList.size() <= 0 || (bringFromServer && LastBringMasterData() > 0))
        {
            if (!GlobalVar.GV().HasInternetAccess)
                return;
            //dataSync.GetCheckPointTypeDDetail();

            ProjectAsyncTask task = new ProjectAsyncTask("CheckPointTypeDDetail/Get", "Get");
            task.setUpdateListener(new OnUpdateListener()
            {
                public void onPostExecuteUpdate(String obj)
                {
                    new CheckPointTypeDDetail(obj,GlobalVar.GV().rootViewMainPage);
                }

                public void onPreExecuteUpdate(){}
            });
            task.execute();
        }
    }

    public ArrayList<NoNeedVolumeReason> NoNeedVolumeReasonList = new ArrayList<>();
    public ArrayList<String> NoNeedVolumeReasonNameList = new ArrayList<>();
    public ArrayList<String> NoNeedVolumeReasonFNameList = new ArrayList<>();
    public void GetNoNeedVolumeReasonList(boolean bringFromServer)
    {
        GlobalVar.GV().NoNeedVolumeReasonList.clear();// = new ArrayList<>();
        GlobalVar.GV().NoNeedVolumeReasonNameList = new ArrayList<>();
        GlobalVar.GV().NoNeedVolumeReasonFNameList = new ArrayList<>();

        Cursor result = dbConnections.Fill("select * from NoNeedVolumeReason");
        if (result.getCount() > 0)
        {
            result.moveToFirst();
            do
            {
                int ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
                String Name = result.getString(result.getColumnIndex("Name"));
                String FName = result.getString(result.getColumnIndex("FName"));

                NoNeedVolumeReason instance = new NoNeedVolumeReason(ID,Name,FName);
                GlobalVar.GV().NoNeedVolumeReasonList.add(instance);
                GlobalVar.GV().NoNeedVolumeReasonNameList.add(Name);
                GlobalVar.GV().NoNeedVolumeReasonFNameList.add(FName);
            }
            while (result.moveToNext());
        }

        com.naqelexpress.naqelpointer.JSON.DataSync dataSync = new DataSync();
        if( GlobalVar.GV().NoNeedVolumeReasonList.size() <= 0 || (bringFromServer && LastBringMasterData() > 0))
        {
            if (!GlobalVar.GV().HasInternetAccess)
                return;
            ProjectAsyncTask task = new ProjectAsyncTask("NoNeedVolumeReason/Get", "Get");
                task.setUpdateListener(new OnUpdateListener()
                {
                    public void onPostExecuteUpdate(String obj)
                    {
                        new NoNeedVolumeReason(obj,GlobalVar.GV().rootViewMainPage);
                    }

                    public void onPreExecuteUpdate(){}
                });
                task.execute();
        }
    }

    private int LastBringMasterData()
    {
        int value = 1;

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
                    DateTime dateTime = DateTime.now();//.withFieldAdded(DurationFieldType.days(),-30);
                    if(GlobalVar.GV().dbConnections.isColumnExist("UserSettings","LastBringMasterData"))
                        dateTime = DateTime.parse(result.getString(result.getColumnIndex("LastBringMasterData")));

                    try
                    {
                        DateTime LastBringMasterData = DateTime.parse(result.getString(result.getColumnIndex("LastBringMasterData")));
                        int counts =  DateTime.now().getDayOfMonth() - LastBringMasterData.getDayOfMonth();
                        if (counts == 0)
                            value = 0;
                    }
                    catch (Exception e)
                    {
                        value = 0;

                        GlobalVar.GV().dbConnections.UpdateSettings(new UserSettings(ID,EmployID,IPAddress,ShowScaningCamera,dateTime));
                        e.printStackTrace();
                    }
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

        return value;
    }

    public void makeCall(String MobileNo)
    {
        Intent intent = new Intent(Intent.ACTION_CALL);

        intent.setData(Uri.parse("tel:" + MobileNo));
        if (!GlobalVar.GV().checkPermission(GlobalVar.GV().activity, PermissionType.Phone))
        {
            GlobalVar.GV().ShowSnackbar(GlobalVar.GV().rootView, context.getString(R.string.NeedPhonePermission), GlobalVar.AlertType.Error);
            GlobalVar.GV().askPermission(GlobalVar.GV().activity, PermissionType.Phone);
        }
        else
            GlobalVar.GV().context.startActivity(intent);
    }

    public void ChangeMapSettings(GoogleMap mMap)
    {
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setIndoorLevelPickerEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabled(true);
        mMap.getUiSettings().setTiltGesturesEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);

        if (!GlobalVar.GV().checkPermission(GlobalVar.GV().activity, GlobalVar.PermissionType.AccessFindLocation) )
        {
            GlobalVar.GV().ShowSnackbar(GlobalVar.GV().rootView,GlobalVar.GV().activity.getString(R.string.NeedLocationPermision), GlobalVar.AlertType.Error);
            GlobalVar.GV().askPermission(GlobalVar.GV().activity, GlobalVar.PermissionType.AccessFindLocation);
        }
        else
            {
                mMap.setMyLocationEnabled(true);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,10));
            }
    }

    public void SyncData(Context context,View view)
    {
        if (GlobalVar.GV().HasInternetAccess)
        {
            GlobalVar.GV().ShowSnackbar(view,context.getString(R.string.SyncingStarted), GlobalVar.AlertType.Info);
            com.naqelexpress.naqelpointer.JSON.DataSync dataSync = new DataSync();
            dataSync.SendPickUpData();
            dataSync.SendOnDliveryData();
            dataSync.SendNotDliveryData();
            dataSync.SendUserMeLoginsData();
            dataSync.SendOnCloadingForDData();
            dataSync.SendMultiDeliveryData();
            dataSync.SendWaybillMeasurementDataData();
            dataSync.SendCheckPointData();
            GlobalVar.GV().ShowSnackbar(view,"Syncing Finish Successfully", GlobalVar.AlertType.Info);
        }
        else
            GlobalVar.GV().ShowSnackbar(view,context.getString(R.string.NoInternetConnection), GlobalVar.AlertType.Warning);
    }

    public int getIntegerFromString(String str)
    {
        int i = 0;
        try
        {
            i = NumberFormat.getInstance().parse(str).intValue();
        }
        catch(Exception ignored){}
        return i;
    }

    public double getDoubleFromString(String str)
    {
        double i = 0;
        try
        {
            i = NumberFormat.getInstance().parse(str).doubleValue();
        }
        catch(Exception ignored){}
        return i;
    }

    public void GetUserPassword()
    {
        if(GlobalVar.GV().EmployID > 0 )
        {
            int Count = GlobalVar.GV().dbConnections.getCount("UserME"," EmployID = " + String.valueOf(GlobalVar.GV().EmployID));
            if ( Count > 0 )
            {
                Cursor result = GlobalVar.GV().dbConnections.Fill("select * from UserME where EmployID = " + String.valueOf(GlobalVar.GV().EmployID));
                if (result.getCount() > 0)
                {
                    result.moveToFirst();
                    do
                    {
                        String Password = result.getString(result.getColumnIndex("Password"));
                        GlobalVar.GV().UserPassword = Password;
                    }
                    while (result.moveToNext());
                }
            }
        }
    }

    public void LoadMyRouteShipments(String orderBy, boolean CheckComplaintandDeliveryRequest)
    {
        if (GlobalVar.GV().CourierDailyRouteID > 0)
        {
            Cursor result = dbConnections.Fill("select * from MyRouteShipments Where CourierDailyRouteID = "+ GlobalVar.GV().CourierDailyRouteID + " order by " + orderBy);
            if (result.getCount() > 0)
            {
                GlobalVar.GV().myRouteShipmentList = new ArrayList<>();

                result.moveToFirst();
                do
                {
                    MyRouteShipments myRouteShipments= new MyRouteShipments();
                    myRouteShipments.ID = Integer.parseInt(result.getString(result.getColumnIndex("ID")));
                    myRouteShipments.BillingType = result.getString(result.getColumnIndex("BillingType"));
                    myRouteShipments.OrderNo = Integer.parseInt(result.getString(result.getColumnIndex("OrderNo")));
                    myRouteShipments.ItemNo = result.getString(result.getColumnIndex("ItemNo"));
                    myRouteShipments.TypeID = Integer.parseInt(result.getString(result.getColumnIndex("TypeID")));
                    myRouteShipments.CODAmount = Double.parseDouble(result.getString(result.getColumnIndex("CODAmount")));
                    myRouteShipments.DeliverySheetID = Integer.parseInt(result.getString(result.getColumnIndex("DeliverySheetID")));
                    myRouteShipments.Date = DateTime.parse(result.getString(result.getColumnIndex("Date")));
                    myRouteShipments.ExpectedTime = DateTime.parse(result.getString(result.getColumnIndex("ExpectedTime")));
                    myRouteShipments.Latitude = result.getString(result.getColumnIndex("Latitude"));
                    myRouteShipments.Longitude = result.getString(result.getColumnIndex("Longitude"));
                    myRouteShipments.ClientID = Integer.parseInt(result.getString(result.getColumnIndex("ClientID")));
                    myRouteShipments.ClientName = result.getString(result.getColumnIndex("ClientName"));
                    myRouteShipments.ClientFName = result.getString(result.getColumnIndex("ClientFName"));
                    myRouteShipments.ClientAddressPhoneNumber = result.getString(result.getColumnIndex("ClientAddressPhoneNumber"));
                    myRouteShipments.ClientAddressFirstAddress = result.getString(result.getColumnIndex("ClientAddressFirstAddress"));
                    myRouteShipments.ClientAddressSecondAddress = result.getString(result.getColumnIndex("ClientAddressSecondAddress"));
                    myRouteShipments.ClientContactName = result.getString(result.getColumnIndex("ClientContactName"));
                    myRouteShipments.ClientContactFName = result.getString(result.getColumnIndex("ClientContactFName"));
                    myRouteShipments.ClientContactPhoneNumber = result.getString(result.getColumnIndex("ClientContactPhoneNumber"));
                    myRouteShipments.ClientContactMobileNo = result.getString(result.getColumnIndex("ClientContactMobileNo"));
                    myRouteShipments.ConsigneeName = result.getString(result.getColumnIndex("ConsigneeName"));
                    myRouteShipments.ConsigneeFName = result.getString(result.getColumnIndex("ConsigneeFName"));
                    myRouteShipments.ConsigneePhoneNumber = result.getString(result.getColumnIndex("ConsigneePhoneNumber"));
                    myRouteShipments.ConsigneeFirstAddress = result.getString(result.getColumnIndex("ConsigneeFirstAddress"));
                    myRouteShipments.ConsigneeSecondAddress = result.getString(result.getColumnIndex("ConsigneeSecondAddress"));
                    myRouteShipments.ConsigneeNear = result.getString(result.getColumnIndex("ConsigneeNear"));
                    myRouteShipments.ConsigneeMobile = result.getString(result.getColumnIndex("ConsigneeMobile"));
                    myRouteShipments.Origin = result.getString(result.getColumnIndex("Origin"));
                    myRouteShipments.Destination = result.getString(result.getColumnIndex("Destination"));
                    myRouteShipments.PODNeeded = Boolean.parseBoolean(result.getString(result.getColumnIndex("PODNeeded")));
                    myRouteShipments.PODDetail = result.getString(result.getColumnIndex("PODDetail"));
                    myRouteShipments.PODTypeCode = result.getString(result.getColumnIndex("PODTypeCode"));
                    myRouteShipments.PODTypeName = result.getString(result.getColumnIndex("PODTypeName"));
                    myRouteShipments.IsDelivered = Boolean.parseBoolean(result.getString(result.getColumnIndex("IsDelivered")));
                    myRouteShipments.NotDelivered = Boolean.parseBoolean(result.getString(result.getColumnIndex("NotDelivered")));
                    myRouteShipments.CourierDailyRouteID = Integer.parseInt(result.getString(result.getColumnIndex("CourierDailyRouteID")));
                    myRouteShipments.OptimzeSerialNo = Integer.parseInt(result.getString(result.getColumnIndex("OptimzeSerialNo")));
                    myRouteShipments.HasComplaint = Boolean.parseBoolean(result.getString(result.getColumnIndex("HasComplaint")));
                    myRouteShipments.HasDeliveryRequest = Boolean.parseBoolean(result.getString(result.getColumnIndex("CourierDailyRouteID")));

                    GlobalVar.GV().myRouteShipmentList.add(myRouteShipments);
                }
                while (result.moveToNext());

                ReOrderMyRouteShipments(CheckComplaintandDeliveryRequest);
            }
        }
    }


    public void LoadMyBooking()
    {

//            Cursor result = dbConnections.Fill("select * from MyRouteShipments Where CourierDailyRouteID = "+ GlobalVar.GV().CourierDailyRouteID + " order by " + orderBy);
//            if (result.getCount() > 0)
//            {
//GlobalVar.GV().myBookingList = new ArrayList<>();
//
//        result.moveToFirst();
//        do
//        {
//            Booking mybooking= new Booking();
//
//
//            GlobalVar.GV().myRouteShipmentList.add(mybooking);
//        }
//        while (result.moveToNext());
//
//            }

            GlobalVar.GV().myBookingList = new ArrayList<>();


            Booking mybooking= new Booking();
            mybooking.ID=201;
            mybooking.ClientID=87999;
            mybooking.ClientName="Test client namd";
            mybooking.RefNo="122412";
            mybooking.BookingDate=DateTime.now();
            mybooking.PicesCount=20;
            mybooking.Weight=45.0;
            mybooking.OfficeUpTo=DateTime.now();
            mybooking.PickUpReqDT=DateTime.now();
            mybooking.SpecialInstruction="Test ing dummy data";
            mybooking.ContactPerson="Shabbir";
            mybooking.ContactNumber="+966553724713";
            mybooking.Address="testtt";
            mybooking.Latitude = "20.5";
            mybooking.Longitude = "85.1";
            mybooking.Status=0;
            mybooking.Orgin="JED";
            mybooking.Destination="RUH";
            mybooking.LoadType="AIR";
            mybooking.BillType="C";
            GlobalVar.GV().myBookingList.add(mybooking);

            Booking mybooking1= new Booking();
            mybooking1.ID=205;
             mybooking.ClientID=87469;
            mybooking.ClientName="Test client ytytytytyty";
            mybooking1.RefNo="erere";
            mybooking1.BookingDate=DateTime.now();
            mybooking1.PicesCount=15;
            mybooking1.Weight=47.0;
            mybooking1.OfficeUpTo=DateTime.now();
            mybooking1.PickUpReqDT=DateTime.now();
            mybooking1.SpecialInstruction="Test ing  data Testing ";
            mybooking1.ContactPerson="SIRAJ";
            mybooking1.ContactNumber="+966553724745";
            mybooking1.Address="tesdfsdfsdf";
            mybooking1.Latitude = "28.5";
            mybooking1.Longitude = "84.1";
            mybooking1.Status=0;
            mybooking1.Orgin="MMA";
            mybooking1.Destination="KSD";
            mybooking1.LoadType="AIR";
            mybooking1.BillType="A";
            GlobalVar.GV().myBookingList.add(mybooking1);
    }
    private void ReOrderMyRouteShipments(boolean CheckComplaintandDeliveryRequest)
    {
        //Has Complaint and Has Delivery Request has high priority.
        //Has Complaint
        // Has Delivery Request
        //Normal Order
        //Has Delivery Exception
        //Delivered will be removed from the list.

        //GTranslation gTranslation = new GTranslation(AddressText,languageCode);
        if (CheckComplaintandDeliveryRequest)
        {
            String jsonData = JsonSerializerDeserializer.serialize(GlobalVar.GV().myRouteShipmentList, true);
            ProjectAsyncTask task = new ProjectAsyncTask("Common/CheckComplaintandDeliveryRequest", "Post", jsonData);
            task.setUpdateListener(new OnUpdateListener() {
                public void onPostExecuteUpdate(String obj) {
                    new MyRouteShipments(obj, MyRouteShipments.UpdateType.DeliveryRequestAndComplaint);
                    GlobalVar.GV().ShowSnackbar(GlobalVar.GV().rootView, "Finish Optimizing Shipments", GlobalVar.AlertType.Info);
                }

                public void onPreExecuteUpdate()
                {
                    GlobalVar.GV().ShowSnackbar(GlobalVar.GV().rootView, "Checking Complaint and Delivery Requests", GlobalVar.AlertType.Info);
                }
            });
            task.execute();
        }

        ArrayList<MyRouteShipments> tmpRouteShipmentList = new ArrayList<>();
        //tmpRouteShipmentList = GlobalVar.GV().myRouteShipmentList;

        for (int i = 0;i < GlobalVar.GV().myRouteShipmentList.size(); i++)
        {
            if (GlobalVar.GV().myRouteShipmentList.get(i).HasComplaint &&
                    GlobalVar.GV().myRouteShipmentList.get(i).HasDeliveryRequest &&
                    !GlobalVar.GV().myRouteShipmentList.get(i).IsDelivered &&
                    !GlobalVar.GV().myRouteShipmentList.get(i).NotDelivered)
                tmpRouteShipmentList.add(GlobalVar.GV().myRouteShipmentList.get(i));
        }

        for (int i = 0;i < GlobalVar.GV().myRouteShipmentList.size(); i++)
        {
            if (GlobalVar.GV().myRouteShipmentList.get(i).HasComplaint &&
                    !GlobalVar.GV().myRouteShipmentList.get(i).HasDeliveryRequest &&
                    !GlobalVar.GV().myRouteShipmentList.get(i).IsDelivered &&
                    !GlobalVar.GV().myRouteShipmentList.get(i).NotDelivered)
                tmpRouteShipmentList.add(GlobalVar.GV().myRouteShipmentList.get(i));
        }

        for (int i = 0;i < GlobalVar.GV().myRouteShipmentList.size(); i++)
        {
            if (!GlobalVar.GV().myRouteShipmentList.get(i).HasComplaint &&
                    GlobalVar.GV().myRouteShipmentList.get(i).HasDeliveryRequest &&
                    !GlobalVar.GV().myRouteShipmentList.get(i).IsDelivered &&
                    !GlobalVar.GV().myRouteShipmentList.get(i).NotDelivered)
                tmpRouteShipmentList.add(GlobalVar.GV().myRouteShipmentList.get(i));
        }

        for (int i = 0;i < GlobalVar.GV().myRouteShipmentList.size(); i++)
        {
            if (!GlobalVar.GV().myRouteShipmentList.get(i).HasComplaint &&
                    !GlobalVar.GV().myRouteShipmentList.get(i).HasDeliveryRequest &&
                    !GlobalVar.GV().myRouteShipmentList.get(i).IsDelivered &&
                    !GlobalVar.GV().myRouteShipmentList.get(i).NotDelivered)
                tmpRouteShipmentList.add(GlobalVar.GV().myRouteShipmentList.get(i));
        }

        for (int i = 0;i < GlobalVar.GV().myRouteShipmentList.size(); i++)
        {
            if (!GlobalVar.GV().myRouteShipmentList.get(i).HasComplaint &&
                    !GlobalVar.GV().myRouteShipmentList.get(i).HasDeliveryRequest &&
                    !GlobalVar.GV().myRouteShipmentList.get(i).IsDelivered &&
                    GlobalVar.GV().myRouteShipmentList.get(i).NotDelivered)
                tmpRouteShipmentList.add(GlobalVar.GV().myRouteShipmentList.get(i));
        }

        GlobalVar.GV().myRouteShipmentList = tmpRouteShipmentList;
    }

    //    public String DateFormat = "dd/MMM/yyyy HH:mm:ss";
//    public java.sql.Date getCurrentSQLDate()
//    {
//        DateTime now = new DateTime();
//        java.sql.Date sqlDate = new java.sql.Date(now.getTime());
//        return sqlDate;
//    }

//    public String getDateTime(DateTime date)
//    {
//        return date.toString();
//        //SimpleDateFormat dateFormat = new SimpleDateFormat(this.DateFormat, Locale.getDefault());
//
////        return dateFormat.format(date);
//    }

//    public String ConvertJsonDate1(String jsonDate)
//    {
////        String jsondate="\/Date(1427959670000)\/";
//        jsonDate=jsonDate.replace("/Date(", "").replace(")/", "");
//        long time = Long.parseLong(jsonDate);
//        Date d = new Date(time);
//        //Log.d("Convertd date is:"+new SimpleDateFormat("dd/MM/yyyy").format(d).toString());
//
//        return new SimpleDateFormat(GlobalVar.GV().DateFormat).format(d).toString();
//    }

    //Date Format
    //SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    //GlobalVar.GV().ShowMessage(this,sdf.format(instance.CTime).toString());

    //For Settings Error
    //txtPOS.setError(txtPOS.getText().toString());

    //Open new Intent
    //Intent mainpage = new Intent(this,MainPageActivity.class);
    //startActivity(mainpage);

    // Make Toast
    //Toast.makeText(this, GlobalVar.gv.xTest,Toast.LENGTH_LONG).show();

//    String ackwardDate = "/Date(1376841597000)/";
//    //Dirty convertion
//    Calendar calendar = Calendar.getInstance();
//    String ackwardRipOff = ackwardDate.replace("/Date(", "").replace(")/", "");
//    Long timeInMillis = Long.valueOf(ackwardRipOff);
//    calendar.setTimeInMillis(timeInMillis);
//    GlobalVar.GV().ShowMessage(this,calendar.getTime().toGMTString());




    //                    DateTime dateTime = new DateTime();//new DateTime(1467880743533L, DateTimeZone.forOffsetHours(-5));
//                    System.out.println(dateTime.toString(ISODateTimeFormat.dateTime()));
//                    String json = JsonSerializerDeserializer.serialize(dateTime, Boolean.TRUE);
//                    System.out.println(json);
//                    dateTime = JsonSerializerDeserializer.deserialize(json, DateTime.class);
//                    System.out.println(dateTime.toString(ISODateTimeFormat.dateTime()));
}

// Old Serlization
//                JSONObject jsonObject = new JSONObject();
//                try
//                {
//                    jsonObject.put("ID",userMELoginRequest.ID);
//                    jsonObject.put("EmployID",userMELoginRequest.EmployID);
//                    jsonObject.put("StateID",userMELoginRequest.StateID);
//                    jsonObject.put("HHDName",userMELoginRequest.HHDName);
//                    jsonObject.put("Version",userMELoginRequest.Version);
//                    jsonObject.put("IsSync",userMELoginRequest.IsSync);
//                    jsonObject.put("TruckID",userMELoginRequest.TruckID);
//                    String dateString = JsonSerializerDeserializer.serialize(userMELoginRequest.Date, Boolean.TRUE);
//                    jsonObject.put("Date",JsonSerializerDeserializer.serialize(userMELoginRequest.Date, Boolean.TRUE));
//                    jsonObject.put("AppTypeID",userMELoginRequest.AppTypeID);
//                    jsonObject.put("Version",userMELoginRequest.AppVersion);
//                    jsonObject.put("LanguageID",userMELoginRequest.LanguageID);

//                    String jsonData = jsonObject.toString();
// }
//                catch (JSONException e)
//                {
//                    e.printStackTrace();
//                }



//        try
//        {
//            //Preparing The Data
//            data.MobileNo = "966535638988";
//            data.AppTypeID = 1;
//            data.AppVersion = "1.0";
//            data.LanguageID = 1;
//
//            JSONObject obj = new JSONObject();
//
//            obj.put("MobileNo",data.MobileNo.toString());
//            obj.put("AppVersion",data.AppVersion.toString());
//            obj.put("LanguageID",data.LanguageID.toString());
//            obj.put("AppTypeID",data.AppTypeID.toString());
//
//        }
//        catch (JSONException e)
//        {
//            e.printStackTrace();
//            GlobalVar.GV().ShowMessage(this,e.getMessage());
//        }
