package com.naqelexpress.naqelpointer.Activity.Booking;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.content.Intent;
import android.widget.AdapterView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.naqelexpress.naqelpointer.Classes.MainActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.naqelexpress.naqelpointer.DB.DBObjects.Booking;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.naqelexpress.naqelpointer.Activity.Booking.BookingListAdapter;
import com.naqelexpress.naqelpointer.R;

public class BookingList extends MainActivity {


    private SwipeMenuListView mapListview;
    private BookingListAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {


            super.onCreate(savedInstanceState);
            setContentView(R.layout.content_booking_list);
            mapListview = (SwipeMenuListView) findViewById(R.id.myBookingListView);


            GetBookingList();


            adapter = new BookingListAdapter(getApplicationContext(), GlobalVar.GV().myBookingList);
            mapListview.setAdapter(adapter);


            GlobalVar.GV().rootViewMainPage = mainRootView = findViewById(android.R.id.content);

            SwipeMenuCreator creator = new SwipeMenuCreator()
            {
                @Override
                public void create(SwipeMenu menu)
                {
                    int menuItemWidth = 120;
                    // create "open" item
                    SwipeMenuItem openItem = new SwipeMenuItem(
                            getApplicationContext());
                    // set item background
                    openItem.setBackground(R.color.NaqelBlue);
                    //(new ColorDrawable(Color.rgb(0xC9, 0xC9,0xCE)));
                    // set item width
                    openItem.setWidth(menuItemWidth);
                    // set item title
                    openItem.setTitle("Open");
                    // set item title fontsize
                    openItem.setTitleSize(18);
                    // set item title font color
                    openItem.setTitleColor(Color.WHITE);
                    // add to menu
                    menu.addMenuItem(openItem);

                    // create "Deliver Later" item
                    SwipeMenuItem deleteItem = new SwipeMenuItem(
                            getApplicationContext());
                    // set item background
                    deleteItem.setBackground(R.color.NaqelRed);
                    //(new ColorDrawable(Color.rgb(0xF9,0x3F, 0x25)));
                    // set item width
                    deleteItem.setWidth(menuItemWidth);
                    // set a icon
                    //deleteItem.setIcon(R.drawable.settings);
                    deleteItem.setTitle("Delete");
                    // set item title font color
                    deleteItem.setTitleColor(Color.WHITE);
                    // set item title fontsize
                    deleteItem.setTitleSize(18);
                    // add to menu
                    menu.addMenuItem(deleteItem);
                }
            };
            mapListview.setMenuCreator(creator);

            mapListview.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {

                            Intent intent = new Intent(getApplicationContext(), BookingDetailActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("ID", String.valueOf(GlobalVar.GV().myBookingList.get(position).ID));
                            intent.putExtras(bundle);
                            startActivity(intent);

                }
            });
        }
        catch
                (Exception ex)
        {
            System.out.println(ex.getMessage());
        }



    }

    private void GetBookingList()
    {
        GlobalVar.GV().LoadMyBooking();
             //GlobalVar.GV().LoadMyBookingList("BookingDate",true);



    }


}


