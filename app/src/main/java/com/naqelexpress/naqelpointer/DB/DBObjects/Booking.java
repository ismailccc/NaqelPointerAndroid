package com.naqelexpress.naqelpointer.DB.DBObjects;

import android.database.Cursor;
import android.view.View;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.GlobalVar;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//Created by sofan on 24/03/2018.

public class Booking
{
    public int ID;
    public String RefNo="";
    public int ClientID;
    public String ClientName="";
    public String ClientFName="";
    public DateTime BookingDate= DateTime.now();
    public int PicesCount;
    public Double Weight;
    public String SpecialInstruction="";
    public DateTime OfficeUpTo= DateTime.now();
    public DateTime PickUpReqDT= DateTime.now();
    public String ContactPerson="";
    public String ContactNumber="";
    public String Address="";
    public String Latitude = "";
    public String Longitude = "";
    public int Status=0;
    public String Orgin="";
    public String Destination="";
    public String LoadType="";
    public String BillType="";
    public int EmployeeId=0;
    public Booking(){}

    public Booking(int id, String refno,int clientID, String clientName, String clientFName,
                   DateTime bookingDate,int picesCount,Double weight,String specialInstruction,
                   DateTime officeUpTo,DateTime pickUpReqDT,String contactPerson,String  contactNumber,
                   String address, String latitude, String  longitude,int status,
                   String orgin,String destination,String loadType,String billType,int employeeId
                   )
    {
        ID = id;
        RefNo = refno;
        ClientID = clientID;
        ClientName = clientName;
        ClientFName = clientFName;
        BookingDate = bookingDate;
        PicesCount = picesCount;
        Weight = weight;
        SpecialInstruction = specialInstruction;
        OfficeUpTo = officeUpTo;
        PickUpReqDT = pickUpReqDT;
        ContactPerson = contactPerson;
        ContactNumber = contactNumber;
        Address = address;
        Latitude = latitude;
        Longitude = longitude;
        Status = status;
        Orgin = orgin;
        Destination = destination;
        LoadType = loadType;
        BillType=billType;
        EmployeeId=employeeId;
    }


    public View rootView;
    public Booking (String finalJson, View view)
    {
        this.rootView = view;
        try
        {
            DBConnections dbConnections = new DBConnections(GlobalVar.GV().context,rootView);
            JSONObject dataObject = new JSONObject(finalJson);
            JSONArray jsonArray = new JSONArray(dataObject.getString("data"));


            if (jsonArray.length() > 0)
            {
                //Delete the existing reasons
                Cursor result = GlobalVar.GV().dbConnections.Fill("select * from Booking");
                if (result.getCount() > 0)
                {
                    result.moveToFirst();
                    do
                    {
                        dbConnections.deleteStation(Integer.parseInt(result.getString(result.getColumnIndex("ID"))));
                    }
                    while (result.moveToNext());
                }
            }

            for(int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Booking instance = new Booking();
                try
                {
                    instance.ID = Integer.parseInt(jsonObject.getString("id"));
                    instance.RefNo = jsonObject.getString("RefNo");
                    instance.ClientID = Integer.parseInt(jsonObject.getString("ClientID"));
                    instance.ClientName = jsonObject.getString("ClientName");
                    instance.BookingDate = DateTime.parse(jsonObject.getString("BookingDate"));
                    instance.PicesCount = Integer.parseInt(jsonObject.getString("PicesCount"));
                    instance.Weight = Double.parseDouble(jsonObject.getString("Weight"));
                    instance.SpecialInstruction = jsonObject.getString("SpecialInstruction");
                    instance.OfficeUpTo = DateTime.parse(jsonObject.getString("OfficeUpTo"));
                    instance.PickUpReqDT = DateTime.parse(jsonObject.getString("PickUpReqDT"));
                    instance.ContactPerson = jsonObject.getString("ContactPerson");
                    instance.ContactNumber = jsonObject.getString("ContactNumber");
                    instance.Address = jsonObject.getString("Address");
                    instance.Latitude = jsonObject.getString("Latitude");
                    instance.Longitude = jsonObject.getString("Longitude");
                    instance.Status = Integer.parseInt(jsonObject.getString("Status"));
                    instance.Orgin = jsonObject.getString("Orgin");
                    instance.Destination = jsonObject.getString("Destination");
                    instance.LoadType = jsonObject.getString("LoadType");
                    instance.BillType = jsonObject.getString("BillType");
                    instance.EmployeeId = Integer.parseInt(jsonObject.getString("EmployeeId"));

                    dbConnections.InsertBooking(instance);
                }
                catch (JSONException ignored){}
            }
          // GlobalVar.GV().LoadMyBookingList("BookingDate",true);

        }
        catch (JSONException ignored){}
    }
}