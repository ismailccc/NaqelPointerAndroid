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
    public String RefNo;
    public int ClientID;
    public String ClientName;
    public String ClientFName;
    public DateTime BookingDate;
    public int PicesCount;
    public Double Weight;
    public String SpecialInstruction;
    public DateTime OfficeUpTo;
    public DateTime PickUpReqDT;
    public String ContactPerson;
    public String ContactNumber;

    public Booking(){}

    public View rootView;
    public Booking (String finalJson, View view)
    {
        this.rootView = view;
        try
        {
            DBConnections dbConnections = new DBConnections(GlobalVar.GV().context,rootView);
            JSONObject dataObject = new JSONObject(finalJson);
            JSONArray jsonArray = new JSONArray(dataObject.getString("data"));

            for(int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Station instance = new Station();
                try
                {
                    instance.ID = Integer.parseInt(jsonObject.getString("id"));

                    //dbConnections.InsertStation(instance);
                }
                catch (JSONException ignored){}
            }
            //GlobalVar.GV().GetStationList(false);
        }
        catch (JSONException ignored){}
    }
}