package com.naqelexpress.naqelpointer.DB.DBObjects;

import com.naqelexpress.naqelpointer.GlobalVar;

import org.joda.time.DateTime;
import org.joda.time.DurationFieldType;

/**
 * Created by sofan on 13/10/2017.
 */

public class UserSettings
{
    public int ID = 0 ;
    public int EmployID = GlobalVar.GV().EmployID;
    public String IPAddress = "";
    public boolean ShowScaningCamera = false;
    public DateTime LastBringMasterData = DateTime.now().withFieldAdded(DurationFieldType.days(),-30);

    public UserSettings(){}

    public UserSettings( String IPAddress, boolean showScaningCamera)
    {
        this.IPAddress = IPAddress;
        ShowScaningCamera = showScaningCamera;
    }

    public UserSettings(int ID, int employID, String IPAddress, boolean showScaningCamera, DateTime lastBringMasterData)
    {
        this.ID = ID;
        EmployID = employID;
        this.IPAddress = IPAddress;
        ShowScaningCamera = showScaningCamera;
        LastBringMasterData = lastBringMasterData;
    }
}