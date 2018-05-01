package com.naqelexpress.naqelpointer.DB.DBObjects;

import android.database.Cursor;

import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MyRouteShipments
{
    public int ID = 0;
    public int OrderNo = 0;
    public String ItemNo = "";
    //1 - Waybill
    //2 - Booking
    public int TypeID = 0;
    public String BillingType = "";
    public double CODAmount = 0;
    public int DeliverySheetID = 0;
    public DateTime Date = DateTime.now();
    public DateTime ExpectedTime = DateTime.now();
    public String Latitude = "0";
    public String Longitude = "0";
    public int ClientID = 0;
    public String ClientName = "";
    public String ClientFName = "";
    public String ClientAddressPhoneNumber = "";
    public String ClientAddressFirstAddress = "";
    public String ClientAddressSecondAddress = "";
    public String ClientContactName = "";
    public String ClientContactFName = "";
    public String ClientContactPhoneNumber = "";
    public String ClientContactMobileNo = "";
    public String ConsigneeName = "";
    public String ConsigneeFName= "";
    public String ConsigneePhoneNumber = "";
    public String ConsigneeFirstAddress = "";
    public String ConsigneeSecondAddress = "";
    public String ConsigneeNear = "";
    public String ConsigneeMobile = "";
    public String Origin = "";
    public String Destination = "";
    public boolean PODNeeded = false;
    public String PODDetail = "";
    public String PODTypeCode = "";
    public String PODTypeName = "";
    public boolean IsDelivered = false;
    public boolean NotDelivered = false;
    public int CourierDailyRouteID ;
    public int OptimzeSerialNo;
    public boolean HasComplaint = false;
    public boolean HasDeliveryRequest = false;

    public MyRouteShipments()
    {

    }

    public MyRouteShipments(int id)
    {
        ID = id;
    }

    public MyRouteShipments(String finalJson, String Latitude, String Longitude)
    {
        JSONObject jsonObject ;
        try
        {
            JSONArray jsonArray = new JSONArray(finalJson);
            for(int i = 0; i < jsonArray.length(); i++)
            {
                MyRouteShipments instance = new MyRouteShipments();
                jsonObject = jsonArray.getJSONObject(i);

                //instance.ID = i + 1;
                instance.BillingType = jsonObject.getString("BillingType");

                instance.ClientAddressFirstAddress = jsonObject.getString("ClientAddressFirstAddress");
                instance.ClientAddressFirstAddress = jsonObject.getString("ClientAddressLocation");
                instance.ClientAddressPhoneNumber = jsonObject.getString("ClientAddressPhoneNumber");
                instance.ClientAddressSecondAddress = jsonObject.getString("ClientAddressSecondAddress");

                instance.ClientContactFName = jsonObject.getString("ClientContactFName");
                instance.ClientContactMobileNo = jsonObject.getString("ClientContactMobileNo");
                instance.ClientContactName = jsonObject.getString("ClientContactName");
                instance.ClientContactPhoneNumber = jsonObject.getString("ClientContactPhoneNumber");

                instance.ClientID = Integer.parseInt(jsonObject.getString("ClientID"));
                instance.ClientName = jsonObject.getString("ClientName");
                instance.ClientFName = jsonObject.getString("ClientFName");

                instance.CODAmount = Double.parseDouble(jsonObject.getString("CODAmount"));

                instance.ConsigneeName = jsonObject.getString("ConsigneeName");
                instance.ConsigneeFName = jsonObject.getString("ConsigneeFName");
                instance.ConsigneePhoneNumber = jsonObject.getString("ConsigneePhoneNumber");
                instance.ConsigneeFirstAddress = jsonObject.getString("ConsigneeFirstAddress");
                instance.ConsigneeSecondAddress = jsonObject.getString("ConsigneeSecondAddress");
                instance.ConsigneeNear = jsonObject.getString("ConsigneeNear");
                instance.ConsigneeMobile = jsonObject.getString("ConsigneeMobile");

                String dt = jsonObject.getString("Date");
                String result = dt.replaceAll("^/Date\\(", "");
//                instance.Date = new DateTime(Long.parseLong(result.substring(0, result.indexOf('+'))));

                instance.DeliverySheetID = Integer.parseInt(jsonObject.getString("DeliverySheetID"));
                instance.Destination = jsonObject.getString("Destination");

                dt = jsonObject.getString("ExpectedTime");
                result = dt.replaceAll("^/Date\\(", "");
//                instance.ExpectedTime = new DateTime(Long.parseLong(result.substring(0, result.indexOf('+'))));

                instance.ItemNo = jsonObject.getString("ItemNo");
                instance.Latitude = jsonObject.getString("Latitude");
                instance.Longitude = jsonObject.getString("Longitude");
                instance.OrderNo = Integer.parseInt(jsonObject.getString("OrderNo"));
                instance.Origin = jsonObject.getString("Origin");
                instance.TypeID = Integer.parseInt(jsonObject.getString("TypeID"));
                instance.PODNeeded = Boolean.parseBoolean(jsonObject.getString("PODNeeded"));
                instance.PODDetail = jsonObject.getString("PODDetail");
                instance.PODTypeCode = jsonObject.getString("PODTypeCode");
                instance.PODTypeName = jsonObject.getString("PODTypeName");

                if (GlobalVar.GV().CourierDailyRouteID == 0)
                {
                    CourierDailyRoute courierDailyRoute = new CourierDailyRoute();
                    courierDailyRoute.StartLatitude = String.valueOf(Latitude);
                    courierDailyRoute.StartLongitude = String.valueOf(Longitude);

                    if (GlobalVar.GV().dbConnections.InsertCourierDailyRoute(courierDailyRoute))
                        GlobalVar.GV().CourierDailyRouteID = GlobalVar.GV().dbConnections.getMaxID("CourierDailyRoute Where EmployID = " + GlobalVar.GV().EmployID + " and EndTime is NULL ");
                    else
                        GlobalVar.GV().ShowSnackbar(GlobalVar.GV().rootViewMainPage, GlobalVar.GV().activity.getString(R.string.ErrorWhileSaving), GlobalVar.AlertType.Error);
                }

                if (GlobalVar.GV().CourierDailyRouteID > 0)
                {
                    instance.CourierDailyRouteID = GlobalVar.GV().CourierDailyRouteID;
                    GlobalVar.GV().dbConnections.InsertMyRouteShipments(instance);
                }
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        GlobalVar.GV().LoadMyRouteShipments("OrderNo",true);
    }

    public enum UpdateType {
        Optimization,
        DeliveryRequestAndComplaint
    }

    public MyRouteShipments(String finalJson, UpdateType updateType)
    {
        JSONObject jsonObject ;
        if (updateType == UpdateType.Optimization)
        {
            try {
                JSONArray jsonArray = new JSONArray(finalJson);
                for (int i = 0; i < jsonArray.length(); i++)
                {
                    jsonObject = jsonArray.getJSONObject(i);

                    int SNO = Integer.parseInt(jsonObject.getString("SerialNo"));
                    DateTime dTime = DateTime.parse(jsonObject.getString("ExpectedTime"));
                    String itemNo = jsonObject.getString("WayBillNo");

                    Cursor resultDetail = GlobalVar.GV().dbConnections.Fill("select * from MyRouteShipments where ItemNo = " + itemNo + " and CourierDailyRouteID = " + GlobalVar.GV().CourierDailyRouteID);

                    if (resultDetail.getCount() > 0)
                    {
                        resultDetail.moveToLast();
                        GlobalVar.GV().dbConnections.UpdateMyRouteShipmentsWithOptimizationSerial(Integer.parseInt(resultDetail.getString(resultDetail.getColumnIndex("ID"))), SNO, dTime);
                    }
                }

                if (jsonArray.length() > 0)
                    GlobalVar.GV().LoadMyRouteShipments("OptimzeSerialNo",true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else
            if (updateType == UpdateType.DeliveryRequestAndComplaint)
            {
                try {
                    JSONArray jsonArray = new JSONArray(finalJson);
                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        jsonObject = jsonArray.getJSONObject(i);

                        boolean hasComplaint = Boolean.parseBoolean(jsonObject.getString("hasComplaint"));
                        boolean hasDeliveryRequest = Boolean.parseBoolean(jsonObject.getString("hasDeliveryRequest"));
                        String itemNo = jsonObject.getString("itemNo");

                        Cursor resultDetail = GlobalVar.GV().dbConnections.Fill("select * from MyRouteShipments where ItemNo = " + itemNo + " and CourierDailyRouteID = " + GlobalVar.GV().CourierDailyRouteID);

                        if (resultDetail.getCount() > 0)
                        {
                            resultDetail.moveToLast();
                            GlobalVar.GV().dbConnections.UpdateMyRouteShipmentsWithComplaintAndDeliveryRequest(Integer.parseInt(resultDetail.getString(resultDetail.getColumnIndex("ID"))), hasComplaint, hasDeliveryRequest);
                        }
                    }

                    if (jsonArray.length() > 0)
                        GlobalVar.GV().LoadMyRouteShipments("OptimzeSerialNo",false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
    }
}