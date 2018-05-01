package com.naqelexpress.naqelpointer.Activity.NotDelivered;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.Classes.NewBarCodeScanner;
import com.naqelexpress.naqelpointer.Classes.OnSpinerItemClick;
import com.naqelexpress.naqelpointer.R;
import com.naqelexpress.naqelpointer.Classes.SpinnerDialog;

import static android.app.Activity.RESULT_OK;

public class NotDeliveredFirstFragment
        extends Fragment
{
    private View rootView;
    SpinnerDialog spinnerDialog;
    TextView txtWaybillNo,txtReason,txtNotes;
    public int ReasonID = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if (rootView == null)
        {
            rootView = inflater.inflate(R.layout.notdeliveredfirstfragment, container, false);
        }

        txtWaybillNo = (TextView) rootView.findViewById(R.id.txtWaybilll);
        txtReason = (TextView) rootView.findViewById(R.id.txtReason);
        txtReason.setInputType(InputType.TYPE_NULL);
        txtNotes = (TextView) rootView.findViewById(R.id.txtNotes);

        txtReason.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (hasFocus)
                    spinnerDialog.showSpinerDialog(false);
            }
        });

        if (GlobalVar.GV().IsEnglish())
            spinnerDialog = new SpinnerDialog(GlobalVar.GV().activity,GlobalVar.GV().DeliveryStatusNameList ,"Select or Search Reason",R.style.DialogAnimations_SmileWindow);
        else
            spinnerDialog = new SpinnerDialog(GlobalVar.GV().activity,GlobalVar.GV().DeliveryStatusFNameList,"Select or Search Reason",R.style.DialogAnimations_SmileWindow);
        spinnerDialog.bindOnSpinerListener(new OnSpinerItemClick()
        {
            @Override
            public void onClick(String item, int position)
            {
                if (GlobalVar.GV().IsEnglish())
                    txtReason.setText(GlobalVar.GV().DeliveryStatusNameList.get(position));
                else
                    txtReason.setText(GlobalVar.GV().DeliveryStatusFNameList.get(position));
                ReasonID =  GlobalVar.GV().DeliveryStatusList.get(position).ID;
                txtNotes.requestFocus();
            }
        });

        Button btnOpenCamera = (Button) rootView.findViewById(R.id.btnOpenCamera);
        btnOpenCamera.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!GlobalVar.GV().checkPermission(getActivity(), GlobalVar.PermissionType.Camera))
                {
                    GlobalVar.GV().ShowSnackbar(rootView, getString(R.string.NeedCameraPermission), GlobalVar.AlertType.Error);
                    GlobalVar.GV().askPermission(getActivity(), GlobalVar.PermissionType.Camera);
                }
                else
                {
                    Intent intent = new Intent(getContext().getApplicationContext(), NewBarCodeScanner.class);
                    startActivityForResult(intent, GlobalVar.GV().CAMERA_PERMISSION_REQUEST);
                }
            }
        });
        Bundle bundle = getArguments();
        if (bundle != null)
        {
            txtWaybillNo.setText(bundle.getString("WaybillNo"));
            txtWaybillNo.setEnabled(false);
            btnOpenCamera.setVisibility(View.GONE);
        }
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == GlobalVar.GV().CAMERA_PERMISSION_REQUEST && resultCode == RESULT_OK)
        {
            if (data != null)
            {
                Bundle extras = data.getExtras();
                if (extras != null)
                {
                    if (extras.containsKey("barcode"))
                    {
                        String barcode = extras.getString("barcode");
                        GlobalVar.GV().MakeSound(GlobalVar.GV().context, R.raw.barcodescanned);
                        txtWaybillNo.setText(barcode);
                    }
                }
//                final Barcode barcode = data.getParcelableExtra("barcode");
//                //final MediaPlayer barcodeSound = MediaPlayer.create(getContext().getApplicationContext(),R.raw.barcodescanned);
//                txtWaybillNo.post(new Runnable()
//                {
//                    @Override
//                    public void run()
//                    {
//                        GlobalVar.GV().MakeSound(GlobalVar.GV().context, R.raw.barcodescanned);
//                        txtWaybillNo.setText(barcode.displayValue);
//                    }
//                });
            }
        }
    }
}