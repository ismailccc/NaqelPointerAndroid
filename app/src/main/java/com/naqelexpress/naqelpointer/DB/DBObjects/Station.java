package com.naqelexpress.naqelpointer.DB.DBObjects;

import android.database.Cursor;
import android.view.View;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.GlobalVar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Station
{
    public int ID;
    public String Code;
    public String Name;
    public String FName;
    public int CountryID;

    public Station(){}

    public Station(int id, String code, String name, String fname, int countryID)
    {
        ID = id;
        Code = code;
        Name = name;
        FName = fname;
        CountryID = countryID;
    }

    public View rootView;
    public Station (String finalJson, View view)
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
                Cursor result = GlobalVar.GV().dbConnections.Fill("select * from Station");
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
                Station instance = new Station();
                try
                {
                    instance.ID = Integer.parseInt(jsonObject.getString("id"));
                    instance.Name = jsonObject.getString("name");
                    instance.FName = jsonObject.getString("fName");
                    instance.CountryID = Integer.parseInt(jsonObject.getString("countryID"));
                    instance.Code = jsonObject.getString("stationCode");

                    dbConnections.InsertStation(instance);
                }
                catch (JSONException ignored){}
            }
            GlobalVar.GV().GetStationList(false);
        }
        catch (JSONException ignored){}
    }
}
