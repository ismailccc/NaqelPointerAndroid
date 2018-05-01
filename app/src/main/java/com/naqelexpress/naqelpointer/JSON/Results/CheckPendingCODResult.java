package com.naqelexpress.naqelpointer.JSON.Results;

import com.naqelexpress.naqelpointer.GlobalVar;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sofan on 07/10/2017.
 */

public class CheckPendingCODResult
    extends DefaultResult
{
    public int WaybillNo;
    public DateTime DeliveryDate;
    public double Amount;

    CheckPendingCODResult()
    {

    }

    public CheckPendingCODResult(String finalJson)
    {
        try
        {
            JSONArray jsonArray = new JSONArray(finalJson);
            for (int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                CheckPendingCODResult instance = new CheckPendingCODResult();
                try
                {
                    instance.WaybillNo = Integer.parseInt(jsonObject.getString("WaybillNo"));
                    String dt = jsonObject.getString("DeliveryDate");
                    String result = dt.replaceAll("^/Date\\(", "");
                    instance.DeliveryDate = new DateTime(Long.parseLong(result.substring(0, result.indexOf('+'))));
                    instance.Amount = Double.parseDouble(jsonObject.getString("Amount"));
                }
                catch (JSONException ignored){}
                GlobalVar.GV().checkPendingCODList.add(instance);
            }
        }
        catch (JSONException ignored){}
    }
}