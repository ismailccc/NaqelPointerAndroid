package com.naqelexpress.naqelpointer.Activity.WaybillMeasurments;

import android.content.DialogInterface;
import android.support.constraint.ConstraintLayout;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import com.naqelexpress.naqelpointer.Classes.MainActivity;
import com.naqelexpress.naqelpointer.Classes.OnSpinerItemClick;
import com.naqelexpress.naqelpointer.Classes.SpinnerDialog;
import com.naqelexpress.naqelpointer.DB.DBObjects.WaybillMeasurement;
import com.naqelexpress.naqelpointer.DB.DBObjects.WaybillMeasurementDetail;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;
import java.util.ArrayList;
import java.util.List;

public class WaybillMeasurementActivity
        extends MainActivity
{
    Button btnStart, btnNext, btnPlus, btnPrevious;
    EditText txtWaybillNo, txtTotalPieces, txtReason, txtRemaining, txtSameSize, txtLength, txtWidth, txtHeight;
    TextView lbLengthCM, lbWidthCM, lbHeightCM, lbIndex;
    CheckBox chNoVolume;
    ConstraintLayout constraintLayout;
    List<WaybillMeasurementDetail> history;
    int TotalHistory = 0, CurrentIndex = 0, ReasonID = 0;
    SpinnerDialog reasonSpinnerDialog;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if(Created)
            return;
        Created = true;
        setContentView(R.layout.waybillmeasurementactivity);

        btnStart = (Button) findViewById(R.id.btnStart);
        btnNext = (Button) findViewById(R.id.btnNext);
        btnPlus = (Button) findViewById(R.id.btnPlus);
        btnPrevious = (Button) findViewById(R.id.btnPrevious);
        btnNext.setVisibility(View.INVISIBLE);
        btnPrevious.setVisibility(View.INVISIBLE);

        lbLengthCM = (TextView) findViewById(R.id.lbLengthCM);
        lbWidthCM = (TextView) findViewById(R.id.lbWidthCM);
        lbHeightCM = (TextView) findViewById(R.id.lbHeightCM);
        lbIndex = (TextView) findViewById(R.id.lbIndex);

        chNoVolume = (CheckBox) findViewById(R.id.chNoVolume);
        constraintLayout = (ConstraintLayout) findViewById(R.id.constraintLayout);

        txtWaybillNo = (EditText) findViewById(R.id.txtWaybillNo);
        txtTotalPieces = (EditText) findViewById(R.id.txtTotalPieces);
        txtReason = (EditText) findViewById(R.id.txtReason);
        txtRemaining = (EditText) findViewById(R.id.txtRemaining);
        txtRemaining.setEnabled(false);
        txtSameSize = (EditText) findViewById(R.id.txtSameSize);
        txtLength = (EditText) findViewById(R.id.txtLength);
        txtWidth = (EditText) findViewById(R.id.txtWidth);
        txtHeight = (EditText) findViewById(R.id.txtHeight);

        txtReason.setVisibility(View.INVISIBLE);
        constraintLayout.setVisibility(View.INVISIBLE);
        history = new ArrayList<>();

        bundle = getIntent().getExtras();
        if (bundle != null)
        {
            txtWaybillNo.setText(bundle.getString("WaybillNo"));
            txtWaybillNo.setEnabled(false);
            txtTotalPieces.setText(bundle.getString("PiecesCount"));
            txtTotalPieces.setEnabled(false);
        }

        chNoVolume.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(isChecked)
                {
                    txtReason.setVisibility(View.VISIBLE);
                    constraintLayout.setVisibility(View.INVISIBLE);
                    btnStart.setVisibility(View.INVISIBLE);
                }
                else
                {
                    txtReason.setVisibility(View.INVISIBLE);
                    constraintLayout.setVisibility(View.INVISIBLE);
                    btnStart.setVisibility(View.VISIBLE);
                }
            }
        });

        txtReason.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //if (hasFocus)
                reasonSpinnerDialog.showSpinerDialog(false);
            }
        });

        txtReason.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (hasFocus)
                    reasonSpinnerDialog.showSpinerDialog(false);
            }
        });

        if (GlobalVar.GV().IsEnglish())
            reasonSpinnerDialog = new SpinnerDialog(GlobalVar.GV().activity,GlobalVar.GV().NoNeedVolumeReasonNameList,"Select Reason",R.style.DialogAnimations_SmileWindow);
        else
            reasonSpinnerDialog = new SpinnerDialog(GlobalVar.GV().activity,GlobalVar.GV().NoNeedVolumeReasonFNameList,"Select Reason",R.style.DialogAnimations_SmileWindow);
        reasonSpinnerDialog.bindOnSpinerListener(new OnSpinerItemClick()
        {
            @Override
            public void onClick(String item, int position)
            {
                if (GlobalVar.GV().IsEnglish())
                    txtReason.setText(GlobalVar.GV().NoNeedVolumeReasonNameList.get(position));
                else
                    txtReason.setText(GlobalVar.GV().NoNeedVolumeReasonFNameList.get(position));
                ReasonID =  GlobalVar.GV().NoNeedVolumeReasonList.get(position).ID;
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(isValidToContinue())
                {
                    txtReason.setVisibility(View.INVISIBLE);
                    chNoVolume.setVisibility(View.INVISIBLE);
                    ReasonID = 0;
                    constraintLayout.setVisibility(View.VISIBLE);
                    btnStart.setVisibility(View.INVISIBLE);
                    txtRemaining.setText(txtTotalPieces.getText());
                    txtTotalPieces.setEnabled(false);
                    history = new ArrayList<>();
                    TotalHistory = 0;
                }
            }
        });

        btnPlus.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                double lenght = GlobalVar.GV().getDoubleFromString(txtLength.getText().toString());
                if(txtLength.getText().toString().equals("") || lenght <= 0)
                {
                    GlobalVar.GV().ShowSnackbar(mainRootView,"You have to check the value of length " + txtLength.getText().toString(), GlobalVar.AlertType.Error);
                    return;
                }

                double height = GlobalVar.GV().getDoubleFromString(txtHeight.getText().toString());
                if(txtHeight.getText().toString().equals("") || height <= 0)
                {
                    GlobalVar.GV().ShowSnackbar(mainRootView,"You have to check the value of Height " + txtHeight.getText().toString(), GlobalVar.AlertType.Error);
                    return;
                }

                double width = GlobalVar.GV().getDoubleFromString(txtWidth.getText().toString());
                if(txtWidth.getText().toString().equals("") || width <= 0)
                {
                    GlobalVar.GV().ShowSnackbar(mainRootView,"You have to check the value of Width "+ txtWidth.getText().toString(), GlobalVar.AlertType.Error);
                    return;
                }

                int TotalPieces = GlobalVar.GV().getIntegerFromString(txtTotalPieces.getText().toString());
                int sameSize = GlobalVar.GV().getIntegerFromString(txtSameSize.getText().toString());
                int remaining = GlobalVar.GV().getIntegerFromString(txtRemaining.getText().toString());
                if(txtSameSize.getText().toString().equals("") ||
                        sameSize <= 0 ||
                        sameSize > remaining)
                {
                    GlobalVar.GV().ShowSnackbar(mainRootView,"You have to check the value of Same Size "+ txtSameSize.getText().toString(), GlobalVar.AlertType.Error);
                    return;
                }

                history.add(new WaybillMeasurementDetail(sameSize, width, lenght, height, 0 ));
                CurrentIndex ++;
                TotalHistory = TotalHistory + sameSize;

                txtRemaining.setText(String.valueOf( TotalPieces - TotalHistory));
                if(txtRemaining.getText().toString().equals("0"))
                {
                    //btnPlus.setVisibility(View.INVISIBLE);
                    btnNext.setVisibility(View.VISIBLE);
                    btnPrevious.setVisibility(View.VISIBLE);
                }
                txtWidth.setText("");
                txtLength.setText("");
                txtHeight.setText("");
                txtSameSize.setText("");
                lbIndex.setText("Index " + String.valueOf(history.size()));
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (history.size() > CurrentIndex)
                {
                    txtSameSize.setText(String.valueOf(history.get(CurrentIndex).PiecesCount));
                    txtWidth.setText(String.valueOf(history.get(CurrentIndex).Width));
                    txtHeight.setText(String.valueOf(history.get(CurrentIndex).Height));
                    txtLength.setText(String.valueOf(history.get(CurrentIndex).Height));

                    lbIndex.setText("Index " + String.valueOf(CurrentIndex + 1));
                }
                if (history.size() > CurrentIndex + 1)
                    CurrentIndex = CurrentIndex + 1;
            }
        });

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (CurrentIndex > 0)
                    CurrentIndex = CurrentIndex - 1;

                if ( CurrentIndex >= 0)
                {
                    txtSameSize.setText(String.valueOf(history.get(CurrentIndex).PiecesCount));
                    txtWidth.setText(String.valueOf(history.get(CurrentIndex).Width));
                    txtHeight.setText(String.valueOf(history.get(CurrentIndex).Height));
                    txtLength.setText(String.valueOf(history.get(CurrentIndex).Length));

                    lbIndex.setText("Index " + String.valueOf( CurrentIndex + 1));
                }
            }
        });
    }

    private boolean isValidToContinue()
    {
        boolean result = true;
        if(txtWaybillNo.getText().toString().equals("") || txtWaybillNo.getText().toString().length() < 8 )
        {
            GlobalVar.GV().ShowSnackbar(mainRootView,"You have to enter Correct Waybill No", GlobalVar.AlertType.Error);
            result = false;
        }

        if(txtTotalPieces.getText().toString().equals("") || Integer.parseInt(txtTotalPieces.getText().toString()) <= 0)
        {
            GlobalVar.GV().ShowSnackbar(mainRootView,"You have to enter the Total Pieces", GlobalVar.AlertType.Error);
            result = false;
        }
        return result;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.waybillmeasurmentmenu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.mnuSave:
                SaveData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void SaveData()
    {
        boolean IsSaved = true;

        if (txtWaybillNo.getText().toString().equals("") ||
                txtWaybillNo.getText().toString().length() < 8)
        {
            GlobalVar.GV().ShowSnackbar(mainRootView,"You have to enter the Waybill No ", GlobalVar.AlertType.Error);
            return;
        }

        if (txtTotalPieces.getText().toString().equals("") ||
                GlobalVar.GV().getIntegerFromString(txtTotalPieces.getText().toString()) <=  0)
        {
            GlobalVar.GV().ShowSnackbar(mainRootView,"You have to enter the Total Pieces", GlobalVar.AlertType.Error);
            return;
        }

        int remaining = GlobalVar.GV().getIntegerFromString(txtRemaining.getText().toString());
        if(remaining > 0)
        {
            GlobalVar.GV().ShowSnackbar(mainRootView,"You have to enter the remaining pieces " + txtRemaining.getText().toString(), GlobalVar.AlertType.Error);
            return;
        }

        if (chNoVolume.isChecked() && ReasonID <= 0)
        {
            GlobalVar.GV().ShowSnackbar(mainRootView,"You have to enter the reason" , GlobalVar.AlertType.Error);
            return;
        }

        WaybillMeasurement waybillMeasurement = new WaybillMeasurement(GlobalVar.GV().getIntegerFromString(txtWaybillNo.getText().toString()),
                GlobalVar.GV().getIntegerFromString(txtTotalPieces.getText().toString()),"Samsung Mobile",
                0,chNoVolume.isChecked(), ReasonID);

        if (GlobalVar.GV().dbConnections.InsertWaybillMeasurement(waybillMeasurement))
        {
            int waybillMeasurementID = GlobalVar.GV().dbConnections.getMaxID("WaybillMeasurement");
            for (int i = 0; i < history.size(); i++)
            {
                WaybillMeasurementDetail waybillMeasurementDetail = new WaybillMeasurementDetail(history.get(i).PiecesCount,
                        history.get(i).Width, history.get(i).Length,history.get(i).Height,waybillMeasurementID);
                if(!GlobalVar.GV().dbConnections.InsertWaybillMeasurementDetail(waybillMeasurementDetail))
                {
                    GlobalVar.GV().ShowSnackbar(mainRootView,getString(R.string.ErrorWhileSaving), GlobalVar.AlertType.Error);
                    IsSaved = false;
                    break;
                }
            }

            if (IsSaved)
            {
                GlobalVar.GV().ShowSnackbar(GlobalVar.GV().rootViewMainPage, getString(R.string.SaveSuccessfully), GlobalVar.AlertType.Info);
                finish();
            }
            else
                GlobalVar.GV().ShowSnackbar(mainRootView, getString(R.string.NotSaved), GlobalVar.AlertType.Error);
        }
        else
            GlobalVar.GV().ShowSnackbar(mainRootView,getString(R.string.ErrorWhileSaving), GlobalVar.AlertType.Error);
    }

    @Override
    public void onBackPressed()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit")
                .setMessage("Are you sure you want to exit without saving?")
                .setPositiveButton("OK",new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface,int which)
                    {
                        WaybillMeasurementActivity.super.onBackPressed();
                    }
                }).setNegativeButton("Cancel",null).setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}

//class History
//{
//    public int PiecesCount = 0;
//    public double Width = 0;
//    public double Length = 0;
//    public double Height = 0;
//
//    public History(int piecesCount, double width, double length, double height)
//    {
//        PiecesCount = piecesCount;
//        Width = width;
//        Length = length;
//        Height = height;
//    }
//}