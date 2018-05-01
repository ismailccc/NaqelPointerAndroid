package com.naqelexpress.naqelpointer.Activity.MyRoute;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.naqelexpress.naqelpointer.DB.DBObjects.MyRouteShipments;
import com.naqelexpress.naqelpointer.R;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import java.util.List;

class RouteListAdapter
        extends BaseAdapter
{
    private Context context;
    private List<MyRouteShipments> itemList;

    RouteListAdapter(Context context, List<MyRouteShipments> itemList)
    {
        this.context = context;
        this.itemList = itemList;
    }

    @Override
    public int getCount()
    {
        return itemList.size();
    }

    @Override
    public MyRouteShipments getItem(int position)
    {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    protected static class ViewHolderItems
    {
        private TextView ItemNo;
        private TextView TypeID;
    }

    @Override
    public int getViewTypeCount()
    {
        // menu type count
        return 2;
    }

    @Override
    public int getItemViewType(int position)
    {
        // current menu type
        return position % 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            convertView = View.inflate(context,R.layout.routeitem, null);
            new ViewHolder(convertView);
        }

        ViewHolder holder = (ViewHolder) convertView.getTag();
        MyRouteShipments item = getItem(position);
        holder.txtWaybill.setText(item.ItemNo);
        holder.lbSerial.setText(String.valueOf(getItemId(position) + 1));
        Integer typeID = itemList.get(position).TypeID;

        DateTimeFormatter fmt = DateTimeFormat.forPattern("HH:mm");
        String dateString = fmt.print(itemList.get(position).ExpectedTime);
        holder.txtExpectedTime.setText(dateString);
        //if (position %4 == 0)
            //holder.panel.setBackgroundColor(Color.RED);

        if (itemList.get(position).HasComplaint)
        {
            holder.imgHasComplaint.setVisibility(View.VISIBLE);
            holder.imgHasComplaint.setImageResource(R.drawable.redstar);
            holder.imgHasComplaint.refreshDrawableState();
        }
        else
            holder.imgHasComplaint.setVisibility(View.GONE);

        if (itemList.get(position).HasDeliveryRequest)
        {
            holder.imgHasDeliveryRequest.setVisibility(View.VISIBLE);
            holder.imgHasDeliveryRequest.setImageResource(R.drawable.greenstar);
            holder.imgHasComplaint.refreshDrawableState();
        }
        else
            holder.imgHasDeliveryRequest.setVisibility(View.GONE);

        if(item.Latitude.length() > 3 && item.Longitude.length() > 3)
        {
            holder.imgHasLocation.setVisibility(View.VISIBLE);
            holder.imgHasLocation.setImageResource(R.drawable.haslocation);
        }
        else
            holder.imgHasLocation.setVisibility(View.INVISIBLE);

        if (typeID == 1)
        {
            holder.txtType.setText("Delivery");
        }
        else
        if (typeID == 2)
        {
            holder.txtType.setText("PickUp");
        }
        return convertView;
    }

    class ViewHolder
    {
        TextView txtWaybill, txtType, lbSerial,txtExpectedTime;
        ImageView imgHasLocation, imgHasComplaint, imgHasDeliveryRequest;
        //TextView panel;

        public ViewHolder(View view)
        {
            txtWaybill = (TextView) view.findViewById(R.id.txtWaybilll);
            lbSerial = (TextView) view.findViewById(R.id.lbSerial);
            txtType = (TextView) view.findViewById(R.id.txtAmount);
            txtExpectedTime = (TextView) view.findViewById(R.id.txtExpectedTime);
            //panel = (TextView) view.findViewById(R.id.panel);
            imgHasLocation = (ImageView) view.findViewById(R.id.imgHasLocation);

            imgHasComplaint = (ImageView) view.findViewById(R.id.imgHasComplaint);
            imgHasDeliveryRequest = (ImageView) view.findViewById(R.id.imgHasRequest);
            view.setTag(this);
        }
    }


//    private int dp2px(int dp)
//    {
//        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
//                getResources().getDisplayMetrics());
//    }
}