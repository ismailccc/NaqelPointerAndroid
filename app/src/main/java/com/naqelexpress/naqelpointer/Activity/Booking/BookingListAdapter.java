package com.naqelexpress.naqelpointer.Activity.Booking;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.DB.DBObjects.Booking;
import com.naqelexpress.naqelpointer.R;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import java.util.List;

public class BookingListAdapter   extends BaseAdapter {
    private Context context;
    private List<Booking> itemList;

    BookingListAdapter(Context context, List<Booking> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Booking getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    protected static class ViewHolderItems {
        private TextView ItemNo;
        private TextView TypeID;
    }

    @Override
    public int getViewTypeCount() {
        // menu type count
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        // current menu type
        return position % 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.bookingitem, null);
            new ViewHolder(convertView);
        }

        ViewHolder holder = (ViewHolder) convertView.getTag();
        Booking item = getItem(position);

        holder.txtReferenceNo.setText(item.RefNo);


        DateTimeFormatter fmtRD = DateTimeFormat.forPattern("dd/mm/yyyy");
        String dateStringRD = fmtRD.print(itemList.get(position).OfficeUpTo);
        holder.txtRequiredDate.setText(dateStringRD);





        DateTimeFormatter fmtRT = DateTimeFormat.forPattern("HH:mm");
        String dateStringRT = fmtRT.print(itemList.get(position).PickUpReqDT);
        holder.txtRequiredTime.setText(dateStringRT);


        DateTimeFormatter fmt = DateTimeFormat.forPattern("HH:mm");
        String dateStringCD = fmt.print(itemList.get(position).OfficeUpTo);
        holder.txtCloseTime.setText(dateStringCD);


        holder.txtPieces.setText(String.valueOf(item.PicesCount));
        holder.txtWeight.setText(String.valueOf(item.Weight));


        //if (position %4 == 0)
        //holder.panel.setBackgroundColor(Color.RED);

//        if (itemList.get(position).HasComplaint) {
//            holder.imgHasComplaint.setVisibility(View.VISIBLE);
//            holder.imgHasComplaint.setImageResource(R.drawable.redstar);
//            holder.imgHasComplaint.refreshDrawableState();
//        } else
//            holder.imgHasComplaint.setVisibility(View.GONE);
//
//        if (itemList.get(position).HasDeliveryRequest) {
//            holder.imgHasDeliveryRequest.setVisibility(View.VISIBLE);
//            holder.imgHasDeliveryRequest.setImageResource(R.drawable.greenstar);
//            holder.imgHasComplaint.refreshDrawableState();
//        } else
//            holder.imgHasDeliveryRequest.setVisibility(View.GONE);
//
//        if (item.Latitude.length() > 3 && item.Longitude.length() > 3) {
//            holder.imgHasLocation.setVisibility(View.VISIBLE);
//            holder.imgHasLocation.setImageResource(R.drawable.haslocation);
//        } else
//            holder.imgHasLocation.setVisibility(View.INVISIBLE);
//
//        if (typeID == 1) {
//            holder.txtType.setText("Delivery");
//        } else if (typeID == 2) {
//            holder.txtType.setText("PickUp");
//        }
        return convertView;
    }

    class ViewHolder {
        TextView txtReferenceNo, txtRequiredTime, txtRequiredDate, txtCloseTime,
                txtWeight,txtPieces;
        ImageView imgHasLocation, imgHasComplaint, imgHasDeliveryRequest;
        //TextView panel;

        public ViewHolder(View view) {
            txtReferenceNo = (TextView) view.findViewById(R.id.txtReferenceNo);
            txtRequiredTime = (TextView) view.findViewById(R.id.txtClient);
            txtRequiredDate = (TextView) view.findViewById(R.id.txtClientId);
            txtCloseTime = (TextView) view.findViewById(R.id.txtCloseTime);
            txtWeight = (TextView) view.findViewById(R.id.txtWeight);
            txtPieces = (TextView) view.findViewById(R.id.txtPiecesCount);
            //panel = (TextView) view.findViewById(R.id.panel);

            view.setTag(this);
        }
    }
}