package com.naqelexpress.naqelpointer.DB.DBObjects;

import android.database.Cursor;
import android.view.View;

import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.GlobalVar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sofan on 24/10/2017.
 */

public class CheckPointType
{
    public int ID;
    public String Name;
    public String FName;

    public CheckPointType(int id,  String name, String fname)
    {
        ID = id;
        Name = name;
        FName = fname;
    }

    public CheckPointType(){}

    public View rootView;
    public CheckPointType (String finalJson, View view)
    {
        this.rootView = view;
        try
        {
            DBConnections dbConnections = new DBConnections(GlobalVar.GV().context,rootView);
            JSONArray jsonArray = new JSONArray(finalJson);
            if (jsonArray.length() > 0)
            {
                //Delete the existing reasons
                Cursor result = GlobalVar.GV().dbConnections.Fill("select * from CheckPointType");
                if (result.getCount() > 0)
                {
                    result.moveToFirst();
                    do
                    {
                        dbConnections.deleteCheckPointType(Integer.parseInt(result.getString(result.getColumnIndex("ID"))));
                    }
                    while (result.moveToNext());
                }
            }

            for(int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                CheckPointType instance = new CheckPointType();
                try
                {
                    instance.ID = Integer.parseInt(jsonObject.getString("id"));
                    instance.Name = jsonObject.getString("name");
                    instance.FName = jsonObject.getString("fName");

                    dbConnections.InsertCheckPointType(instance);
                }
                catch (JSONException ignored){}
            }
            GlobalVar.GV().GetCheckPointTypeList(false);
        }
        catch (JSONException ignored){}
    }
}