package com.naqelexpress.naqelpointer.DB.DBObjects;

import android.database.Cursor;
import android.view.View;

import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.GlobalVar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sofan on 13/04/2018.
 */

public class CheckPointTypeDetail
{
    public int ID;
    public String Name;
    public String FName;
    public int CheckPointTypeID;

    public CheckPointTypeDetail(int id,  String name, String fname, int checkPointTypeID)
    {
        ID = id;
        Name = name;
        FName = fname;
        CheckPointTypeID = checkPointTypeID;
    }

    public CheckPointTypeDetail(){}

    public View rootView;
    public CheckPointTypeDetail (String finalJson, View view)
    {
        this.rootView = view;
        try
        {
            DBConnections dbConnections = new DBConnections(GlobalVar.GV().context,rootView);
            JSONArray jsonArray = new JSONArray(finalJson);
            if (jsonArray.length() > 0)
            {
                //Delete the existing reasons
                Cursor result = GlobalVar.GV().dbConnections.Fill("select * from CheckPointTypeDetail");
                if (result.getCount() > 0)
                {
                    result.moveToFirst();
                    do
                    {
                        dbConnections.deleteCheckPointTypeDetail(Integer.parseInt(result.getString(result.getColumnIndex("ID"))));
                    }
                    while (result.moveToNext());
                }
            }

            for(int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                CheckPointTypeDetail instance = new CheckPointTypeDetail();
                try
                {
                    instance.ID = Integer.parseInt(jsonObject.getString("id"));
                    instance.Name = jsonObject.getString("name");
                    instance.FName = jsonObject.getString("fName");
                    instance.CheckPointTypeID = Integer.parseInt(jsonObject.getString("checkPointTypeID"));

                    dbConnections.InsertCheckPointTypeDetail(instance);
                }
                catch (JSONException ignored){}
            }
            GlobalVar.GV().GetCheckPointTypeDetailList(false,0);
        }
        catch (JSONException ignored){}
    }
}