package com.naqelexpress.naqelpointer.Activity.MultiDelivery;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.naqelexpress.naqelpointer.R;

public class MultiDeliveryFirstFragment
        extends Fragment
{
    View rootView;
    public EditText txtReceiverName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        if (rootView == null)
        {
            rootView = inflater.inflate(R.layout.multideliveryfirstfragment, container, false);
            txtReceiverName = (EditText) rootView.findViewById(R.id.txtCheckPointType);

        }

        return rootView;
    }
}