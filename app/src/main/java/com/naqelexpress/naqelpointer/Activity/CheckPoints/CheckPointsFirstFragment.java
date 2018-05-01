package com.naqelexpress.naqelpointer.Activity.CheckPoints;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.naqelexpress.naqelpointer.Classes.OnSpinerItemClick;
import com.naqelexpress.naqelpointer.Classes.SpinnerDialog;
import com.naqelexpress.naqelpointer.DB.DBObjects.CheckPointTypeDetail;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;


import java.util.Iterator;

/**
 * Created by sofan on 21/03/2018.
 */

public class CheckPointsFirstFragment
        extends Fragment
{
    View rootView;
    public EditText txtCheckPointType, txtCheckPointTypeDetail, txtCheckPointTypeDDetail;
    SpinnerDialog checkPointTypeSpinnerDialog, checkPointTypeDetailSpinnerDialog, checkPointTypeDDetailSpinnerDialog;
    public int CheckPointTypeID = 0, CheckPointTypeDetailID = 0, CheckPointTypeDDetailID = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        if (rootView == null)
        {
            rootView = inflater.inflate(R.layout.checkpointsfirstfragment, container, false);
            txtCheckPointType = (EditText) rootView.findViewById(R.id.txtCheckPointType);
            txtCheckPointTypeDetail = (EditText) rootView.findViewById(R.id.txtCheckPointTypeDetail);
            txtCheckPointTypeDDetail = (EditText) rootView.findViewById(R.id.txtCheckPointTypeDDetail);

            txtCheckPointType.setInputType(InputType.TYPE_NULL);
            txtCheckPointTypeDetail.setInputType(InputType.TYPE_NULL);
            txtCheckPointTypeDDetail.setInputType(InputType.TYPE_NULL);

            txtCheckPointTypeDetail.setVisibility(View.INVISIBLE);
            txtCheckPointTypeDDetail.setVisibility(View.INVISIBLE);

            txtCheckPointType.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    checkPointTypeSpinnerDialog.showSpinerDialog(false);
                }
            });

            txtCheckPointTypeDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    checkPointTypeDetailSpinnerDialog.showSpinerDialog(false);
                }
            });

            txtCheckPointTypeDDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    checkPointTypeDDetailSpinnerDialog.showSpinerDialog(false);
                }
            });

            if (GlobalVar.GV().IsEnglish())
                checkPointTypeSpinnerDialog = new SpinnerDialog(GlobalVar.GV().activity,GlobalVar.GV().CheckPointTypeNameList,"Select Check Point Type",R.style.DialogAnimations_SmileWindow);
            else
                checkPointTypeSpinnerDialog = new SpinnerDialog(GlobalVar.GV().activity,GlobalVar.GV().CheckPointTypeFNameList,"Select Check Point Type",R.style.DialogAnimations_SmileWindow);

            checkPointTypeSpinnerDialog.bindOnSpinerListener(new OnSpinerItemClick()
            {
                @Override
                public void onClick(String item, int position) {
                    if (GlobalVar.GV().IsEnglish())
                        txtCheckPointType.setText(GlobalVar.GV().CheckPointTypeNameList.get(position));
                    else
                        txtCheckPointType.setText(GlobalVar.GV().CheckPointTypeFNameList.get(position));
                    CheckPointTypeID = GlobalVar.GV().CheckPointTypeList.get(position).ID;

                    txtCheckPointTypeDetail.setText("");
                    txtCheckPointTypeDDetail.setText("");
                    CheckPointTypeDetailID = 0;
                    CheckPointTypeDDetailID = 0;

//                    boolean hasDetails = false;

//                    for (int i = 0; i < GlobalVar.GV().CheckPointTypeDetailList.size();i++)
//                        if (GlobalVar.GV().CheckPointTypeDetailList.get(i).CheckPointTypeID == CheckPointTypeID)
//                        {
//                            hasDetails = true;
//                            break;
//                        }

                    GlobalVar.GV().GetCheckPointTypeDetailList(false,CheckPointTypeID);
                    if (GlobalVar.GV().CheckPointTypeDetailList.size() > 0 )
                    {
                        txtCheckPointTypeDetail.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        txtCheckPointTypeDetail.setVisibility(View.INVISIBLE);
                        txtCheckPointTypeDDetail.setVisibility(View.INVISIBLE);
                        CheckPointTypeDetailID = 0;
                        CheckPointTypeDDetailID = 0;
                    }
                }
            });

            if (GlobalVar.GV().IsEnglish())
                checkPointTypeDetailSpinnerDialog = new SpinnerDialog(GlobalVar.GV().activity,GlobalVar.GV().CheckPointTypeDetailNameList,"Select Reason",R.style.DialogAnimations_SmileWindow);
            else
                checkPointTypeDetailSpinnerDialog = new SpinnerDialog(GlobalVar.GV().activity,GlobalVar.GV().CheckPointTypeDetailFNameList,"Select Reason",R.style.DialogAnimations_SmileWindow);

            checkPointTypeDetailSpinnerDialog.bindOnSpinerListener(new OnSpinerItemClick()
            {
                @Override
                public void onClick(String item, int position)
                {
                    if (GlobalVar.GV().IsEnglish())
                        txtCheckPointTypeDetail.setText(GlobalVar.GV().CheckPointTypeDetailNameList.get(position));
                    else
                        txtCheckPointTypeDetail.setText(GlobalVar.GV().CheckPointTypeDetailFNameList.get(position));
                    CheckPointTypeDetailID =  GlobalVar.GV().CheckPointTypeDetailList.get(position).ID;
                    txtCheckPointTypeDDetail.setText("");
                    CheckPointTypeDDetailID = 0;

//                    boolean hasDetails = false;
//                    for (int i = 0; i < GlobalVar.GV().CheckPointTypeDDetailList.size();i++)
//                        if (GlobalVar.GV().CheckPointTypeDDetailList.get(i).CheckPointTypeDetailID == CheckPointTypeDetailID)
//                        {
//                            hasDetails = true;
//                            break;
//                        }

                    GlobalVar.GV().GetCheckPointTypeDDetailList(false,CheckPointTypeDetailID);
                    if (GlobalVar.GV().CheckPointTypeDDetailList.size() > 0)
                    {
                        txtCheckPointTypeDDetail.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        txtCheckPointTypeDDetail.setVisibility(View.INVISIBLE);
                        CheckPointTypeDDetailID = 0;
                    }
                }
            });

            if (GlobalVar.GV().IsEnglish())
                checkPointTypeDDetailSpinnerDialog = new SpinnerDialog(GlobalVar.GV().activity,GlobalVar.GV().CheckPointTypeDDetailNameList,"Select Reason",R.style.DialogAnimations_SmileWindow);
            else
                checkPointTypeDDetailSpinnerDialog = new SpinnerDialog(GlobalVar.GV().activity,GlobalVar.GV().CheckPointTypeDDetailFNameList,"Select Reason",R.style.DialogAnimations_SmileWindow);

            checkPointTypeDDetailSpinnerDialog.bindOnSpinerListener(new OnSpinerItemClick()
            {
                @Override
                public void onClick(String item, int position)
                {
                    if (GlobalVar.GV().IsEnglish())
                        txtCheckPointTypeDDetail.setText(GlobalVar.GV().CheckPointTypeDDetailNameList.get(position));
                    else
                        txtCheckPointTypeDDetail.setText(GlobalVar.GV().CheckPointTypeDDetailFNameList.get(position));
                    CheckPointTypeDDetailID =  GlobalVar.GV().CheckPointTypeDDetailList.get(position).ID;
                }
            });
        }

        return rootView;
    }
}