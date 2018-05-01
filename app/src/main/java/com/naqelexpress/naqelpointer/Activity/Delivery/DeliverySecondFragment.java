package com.naqelexpress.naqelpointer.Activity.Delivery;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.R;

public class DeliverySecondFragment
        extends Fragment
        implements TextWatcher
{
    View rootView;
    EditText txtPOS ;
    EditText txtCash ;
    TextView lbTotal ;
    public EditText txtReceiverName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if (rootView == null)
        {
            rootView = inflater.inflate(R.layout.deliverysecondfragment, container, false);
            txtPOS = (EditText) rootView.findViewById(R.id.txtPOSAmount);
            txtCash = (EditText) rootView.findViewById(R.id.txtCashAmount);
            lbTotal = (TextView) rootView.findViewById(R.id.lbTotal);
            txtReceiverName = (EditText) rootView.findViewById(R.id.txtCheckPointType);

            txtPOS.addTextChangedListener(this);
            txtCash.addTextChangedListener(this);
        }
        return rootView;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after)   {    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count)    {    }

    @Override
    public void afterTextChanged(Editable s)
    {
        Integer pos = 0, cash = 0, Total;
        if (txtPOS != null && txtPOS.getText().length() > 0)
        {
            pos = Integer.parseInt(txtPOS.getText().toString());
        }
        if (txtCash != null && txtCash.length() > 0)
        {
            cash = Integer.parseInt(txtCash.getText().toString());
        }

        Total = pos + cash;
        lbTotal.setText(getResources().getString(R.string.TotalCollectedAmount) + Total.toString());
    }
}