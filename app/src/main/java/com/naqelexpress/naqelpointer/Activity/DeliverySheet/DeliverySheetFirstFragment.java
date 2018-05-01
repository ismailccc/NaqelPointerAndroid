package com.naqelexpress.naqelpointer.Activity.DeliverySheet;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.naqelexpress.naqelpointer.R;

public class DeliverySheetFirstFragment
        extends Fragment
{
    View rootView;
    public EditText txtCourierID, txtTruckID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        if (rootView == null)
        {
            rootView = inflater.inflate(R.layout.deliverysheetfirstfragment, container, false);
            txtCourierID = (EditText) rootView.findViewById(R.id.txtCourierID);
            txtTruckID = (EditText) rootView.findViewById(R.id.txtTruckID);
        }

        return rootView;
    }
}