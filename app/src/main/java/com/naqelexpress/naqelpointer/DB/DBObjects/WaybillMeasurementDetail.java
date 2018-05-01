package com.naqelexpress.naqelpointer.DB.DBObjects;

import android.provider.SyncStateContract;

/**
 * Created by sofan on 17/03/2018.
 */

public class WaybillMeasurementDetail
{
    public int ID = 0;
    public int PiecesCount = 0;
    public double Width = 0;
    public double Length = 0;
    public double Height = 0;
    public boolean IsSync = false;
    public int WaybillMeasurementID = 0;

        public WaybillMeasurementDetail (int piecesCount, double width, double length, double height, int waybillMeasurementID) {
        PiecesCount = piecesCount;
        Width = width;
        Length = length;
        Height = height;
        WaybillMeasurementID = waybillMeasurementID;
    }
}