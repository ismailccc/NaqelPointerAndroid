package com.naqelexpress.naqelpointer.DB.DBObjects;

import android.database.Cursor;
import android.view.View;

import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.GlobalVar;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sofan on 17/03/2018.
 */

public class NoNeedVolumeReason
{
    public int ID;
    public String Name;
    public String FName;

    public NoNeedVolumeReason(){}

    public NoNeedVolumeReason(int id, String name, String fname)
    {
        ID = id;
        Name = name;
        FName = fname;
    }

    public View rootView;
    public NoNeedVolumeReason (String finalJson, View view)
    {
        this.rootView = view;
        try
        {
            DBConnections dbConnections = new DBConnections(GlobalVar.GV().context,rootView);
            JSONArray jsonArray = new JSONArray(finalJson);
            if (jsonArray.length() > 0)
            {
                //Delete the existing reasons
                Cursor result = GlobalVar.GV().dbConnections.Fill("select * from NoNeedVolumeReason");
                if (result.getCount() > 0)
                {
                    result.moveToFirst();
                    do
                    {
                        dbConnections.deleteVolumeReason(Integer.parseInt(result.getString(result.getColumnIndex("ID"))));
                    }
                    while (result.moveToNext());
                }
            }

            for(int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                NoNeedVolumeReason instance = new NoNeedVolumeReason();
                try
                {
                    instance.ID = Integer.parseInt(jsonObject.getString("id"));
                    instance.Name = jsonObject.getString("name");
                    instance.FName = jsonObject.getString("fName");

                    dbConnections.InsertVolumeReason(instance);
                }
                catch (JSONException ignored){}
            }
            GlobalVar.GV().GetNoNeedVolumeReasonList(false);

            GlobalVar.GV().currentSettings.LastBringMasterData = DateTime.now();
            GlobalVar.GV().dbConnections.UpdateSettingsLastBringMasterData(GlobalVar.GV().currentSettings);
        }
        catch (JSONException ignored){}
    }
}