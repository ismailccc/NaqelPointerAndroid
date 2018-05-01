package com.naqelexpress.naqelpointer.Activity.MyRoute;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.naqelexpress.naqelpointer.Activity.Booking.BookingPlanActivity;
import com.naqelexpress.naqelpointer.Activity.Waybill.WaybillPlanActivity;
import com.naqelexpress.naqelpointer.Classes.OnUpdateListener;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.Classes.JsonSerializerDeserializer;
import com.naqelexpress.naqelpointer.Classes.MainActivity;
import com.naqelexpress.naqelpointer.DB.DBObjects.MyRouteShipments;
import com.naqelexpress.naqelpointer.JSON.DataSync;
import com.naqelexpress.naqelpointer.JSON.ProjectAsyncTask;
import com.naqelexpress.naqelpointer.JSON.Request.BringMyRouteShipmentsRequest;
import com.naqelexpress.naqelpointer.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MyRouteActivity
        extends MainActivity
{
    private SwipeMenuListView mapListview;
    private RouteListAdapter adapter;
    Button btnStartTrip, btnCloseTrip;
    TextView txtStartTrip, txtCloseTrip;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if(Created)
        {
            adapter = new RouteListAdapter(getApplicationContext(),GlobalVar.GV().myRouteShipmentList);
            mapListview.setAdapter(adapter);
            return;
        }
        Created = true;
        setContentView(R.layout.myroute);

        mapListview = (SwipeMenuListView) findViewById(R.id.myRouteListView);
        adapter = new RouteListAdapter(getApplicationContext(),GlobalVar.GV().myRouteShipmentList);
        mapListview.setAdapter(adapter);

        GlobalVar.GV().rootViewMainPage = mainRootView = findViewById(android.R.id.content);

        btnStartTrip = (Button) findViewById(R.id.btnStartTrip);
        btnCloseTrip = (Button) findViewById(R.id.btnCloseTrip);

        txtStartTrip = (TextView) findViewById(R.id.txtStartTrip);
        txtCloseTrip = (TextView) findViewById(R.id.txtCloseTrip);

        checkCourierDailyRouteID(false);
        if (GlobalVar.GV().CourierDailyRouteID == 0)
        {
            btnStartTrip.setVisibility(View.VISIBLE);
            txtStartTrip.setVisibility(View.VISIBLE);
            btnCloseTrip.setVisibility(View.GONE);
            txtCloseTrip.setVisibility(View.GONE);
        }
        else
        {
            btnStartTrip.setVisibility(View.GONE);
            txtStartTrip.setVisibility(View.GONE);
            if (GlobalVar.GV().myRouteShipmentList.size() > 0)
            {
                btnCloseTrip.setVisibility(View.GONE);
                txtCloseTrip.setVisibility(View.GONE);
            }
            else
            {
                btnCloseTrip.setVisibility(View.VISIBLE);
                txtCloseTrip.setVisibility(View.VISIBLE);
            }
        }

        btnStartTrip.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                checkCourierDailyRouteID(true);
            }
        });

        btnCloseTrip.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (GlobalVar.GV().CourierDailyRouteID > 0)
                {
                    GlobalVar.GV().dbConnections.CloseCurrentCourierDailyRoute();
                    finish();
                }
            }
        });

        mapListview.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if (GlobalVar.GV().CourierDailyRouteID > 0)
                {
                    if (GlobalVar.GV().myRouteShipmentList.get(position).TypeID == 1)
                    {
                        Intent intent = new Intent(getApplicationContext(), WaybillPlanActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("ID", String.valueOf(GlobalVar.GV().myRouteShipmentList.get(position).ID));
                        bundle.putString("WaybillNo",GlobalVar.GV().myRouteShipmentList.get(position).ItemNo);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                    else
                    {
                        Intent intent = new Intent(getApplicationContext(), BookingPlanActivity.class);
                        startActivity(intent);
                    }
                }
                else
                    GlobalVar.GV().ShowSnackbar(GlobalVar.GV().rootView,"You have to start a new trip before", GlobalVar.AlertType.Warning);
            }
        });

        SwipeMenuCreator creator = new SwipeMenuCreator()
        {
            @Override
            public void create(SwipeMenu menu)
            {
                int menuItemWidth = 120;
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                openItem.setBackground(R.color.NaqelBlue);
                        //(new ColorDrawable(Color.rgb(0xC9, 0xC9,0xCE)));
                // set item width
                openItem.setWidth(menuItemWidth);
                // set item title
                openItem.setTitle("Open");
                // set item title fontsize
                openItem.setTitleSize(18);
                // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem);

                // create "Deliver Later" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(R.color.NaqelRed);
                //(new ColorDrawable(Color.rgb(0xF9,0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(menuItemWidth);
                // set a icon
                //deleteItem.setIcon(R.drawable.settings);
                deleteItem.setTitle("Delete");
                // set item title font color
                deleteItem.setTitleColor(Color.WHITE);
                // set item title fontsize
                deleteItem.setTitleSize(18);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

        // set creator
        mapListview.setMenuCreator(creator);
        mapListview.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index)
            {
                switch (index)
                {
                    case 0:
                        if (GlobalVar.GV().myRouteShipmentList.get(position).TypeID == 1)
                        {
                            Intent intent = new Intent(getApplicationContext(), WaybillPlanActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("ID", String.valueOf(GlobalVar.GV().myRouteShipmentList.get(position).ID));
                            bundle.putString("WaybillNo",GlobalVar.GV().myRouteShipmentList.get(position).ItemNo);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                        else
                        {
                            Intent intent = new Intent(getApplicationContext(), BookingPlanActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("ID", String.valueOf(GlobalVar.GV().myRouteShipmentList.get(position).ID));
                            bundle.putString("WaybillNo",GlobalVar.GV().myRouteShipmentList.get(position).ItemNo);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                        break;
                    case 1:
                        MyRouteShipments item = GlobalVar.GV().myRouteShipmentList.get(position);
                        GlobalVar.GV().myRouteShipmentList.remove(item);
                        adapter = new RouteListAdapter(getApplicationContext(),GlobalVar.GV().myRouteShipmentList);
                        mapListview.setAdapter(adapter);
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });
    }

    private void checkCourierDailyRouteID(boolean CreateNewRoute)
    {
        if (GlobalVar.GV().CourierDailyRouteID == 0)
        {
            GlobalVar.GV().CourierDailyRouteID = GlobalVar.GV().dbConnections.getMaxID("CourierDailyRoute Where EmployID = " + GlobalVar.GV().EmployID + " and EndTime is NULL ");
            GlobalVar.GV().LoadMyRouteShipments("OrderNo",true);

            if (GlobalVar.GV().myRouteShipmentList.size() > 0)
            {
                btnStartTrip.setVisibility(View.GONE);
                txtStartTrip.setVisibility(View.GONE);

                btnCloseTrip.setVisibility(View.GONE);
                txtCloseTrip.setVisibility(View.GONE);

                adapter = new RouteListAdapter(getApplicationContext(),GlobalVar.GV().myRouteShipmentList);
                mapListview.setAdapter(adapter);
            }
        }
        else
            if (GlobalVar.GV().myRouteShipmentList.size() == 0)
            {
                btnStartTrip.setVisibility(View.GONE);
                txtStartTrip.setVisibility(View.GONE);

                btnCloseTrip.setVisibility(View.VISIBLE);
                txtCloseTrip.setVisibility(View.VISIBLE);
            }
            else
            {
                btnStartTrip.setVisibility(View.GONE);
                txtStartTrip.setVisibility(View.GONE);

                btnCloseTrip.setVisibility(View.GONE);
                txtCloseTrip.setVisibility(View.GONE);
            }

        if (GlobalVar.GV().CourierDailyRouteID == 0 && CreateNewRoute)
        {
            BringMyRouteShipmentsRequest bringMyRouteShipmentsRequest = new BringMyRouteShipmentsRequest();
            BringMyRouteShipments(bringMyRouteShipmentsRequest);

//           if(GlobalVar.GV().dbConnections.InsertCourierDailyRoute(courierDailyRoute) == true)
//           {
//               GlobalVar.GV().CourierDailyRouteID = GlobalVar.GV().dbConnections.getMaxID("CourierDailyRoute Where EmployID = " + GlobalVar.GV().EmployID + " and EndTime is NULL ");
//               if (GlobalVar.GV().CourierDailyRouteID > 0)
//               {
//                   btnStartTrip.setVisibility(View.GONE);
//                   txtStartTrip.setVisibility(View.GONE);
//               }
//           }
//           else
//               GlobalVar.GV().ShowSnackbar(mainRootView,getString(R.string.ErrorWhileSaving), GlobalVar.AlertType.Error);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.myroutemenu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    class Optimization
    {
        public String CurrentLocation;
        public String EmployID;
        public String FleetNo;
        public String Waybills;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId())
        {
            case R.id.OptimizeShipments:
                Optimization optimization = new Optimization();
                optimization.CurrentLocation = String.valueOf(Latitude)+","+String.valueOf(Longitude);
                        //"24.774056910456554,46.828073114091524";
                optimization.EmployID = String.valueOf(GlobalVar.GV().EmployID);
                optimization.FleetNo = "test";
                String MyShipments = "";
                for (int i =0;i< GlobalVar.GV().myRouteShipmentList.size();i++) {
                    MyShipments += GlobalVar.GV().myRouteShipmentList.get(i).ItemNo;
                    if (i < GlobalVar.GV().myRouteShipmentList.size() - 1)
                        MyShipments+= ",";
                }
                optimization.Waybills = MyShipments;
                String jsonData = JsonSerializerDeserializer.serialize(optimization, true);
                ProjectAsyncTask task = new ProjectAsyncTask("Optimize", "Post",jsonData,"http://35.188.10.142/NaqelRouteApi/api/");
                task.setUpdateListener(new OnUpdateListener()
                {
                    public void onPostExecuteUpdate(String obj)
                    {
                        new MyRouteShipments(obj, MyRouteShipments.UpdateType.Optimization);
                        adapter = new RouteListAdapter(getApplicationContext(),GlobalVar.GV().myRouteShipmentList);
                        mapListview.setAdapter(adapter);
                        //GlobalVar.GV().ShowSnackbar(GlobalVar.GV().rootView, "Finish Optimizing Shipments", GlobalVar.AlertType.Info);
                    }

                    public void onPreExecuteUpdate()
                    {
                        GlobalVar.GV().ShowSnackbar(GlobalVar.GV().rootView, "Start Optimizing Shipments", GlobalVar.AlertType.Info);
                    }
                });
                task.execute();
                return true;
            case R.id.mnuShowDeliverySheetOrder:
                GlobalVar.GV().LoadMyRouteShipments("OrderNo",true);
                adapter = new RouteListAdapter(getApplicationContext(),GlobalVar.GV().myRouteShipmentList);
                mapListview.setAdapter(adapter);
                return true;
//            case R.id.DeleteAll:
////                //TODO remove delete all from the menu itself.
////                GlobalVar.GV().myRouteShipmentList = new ArrayList<>();
////                adapter = new RouteListAdapter(getApplicationContext(), GlobalVar.GV().myRouteShipmentList);
////                mapListview.setAdapter(adapter);
////                btnCloseTrip.setVisibility(View.VISIBLE);
////                txtCloseTrip.setVisibility(View.VISIBLE);
//                GlobalVar.GV().ShowSnackbar(GlobalVar.GV().rootView,"You don't have a privillages.", GlobalVar.AlertType.Warning);
//                return true;
//            case R.id.CheckNewBooking:
//                //com.naqelexpress.naqelpointer.JSON.DataSync dataSync = new DataSync();
//                //dataSync.CheckOnlineBooking();
//                GlobalVar.GV().ShowSnackbar(GlobalVar.GV().rootView,"You don't have a privillages.", GlobalVar.AlertType.Warning);
//                return true;
            case R.id.mnuSyncData:
                GlobalVar.GV().SyncData(GlobalVar.GV().context,mainRootView);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //------------------------Bring My Route Shipments -------------------------------
    public void BringMyRouteShipments(BringMyRouteShipmentsRequest bringMyRouteShipmentsRequest)
    {
        if (!GlobalVar.GV().HasInternetAccess)
            return;

        String jsonData = JsonSerializerDeserializer.serialize(bringMyRouteShipmentsRequest, true);
        new BringMyRouteShipmentsList().execute(jsonData);
    }

    private class BringMyRouteShipmentsList extends AsyncTask<String,Void,String>
    {
        private ProgressDialog progressDialog;
        String result = "";
        StringBuffer buffer;

        @Override
        protected void onPreExecute()
        {
            progressDialog = ProgressDialog.show(GlobalVar.GV().context, "Please wait.", "Downloading Shipments Details.", true);
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
                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "BringMyRouteShipments");
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
            new MyRouteShipments(finalJson, String.valueOf(Latitude), String.valueOf(Longitude));

            adapter = new RouteListAdapter(getApplicationContext(),GlobalVar.GV().myRouteShipmentList);
            mapListview.setAdapter(adapter);
        }
    }
}