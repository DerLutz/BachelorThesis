package com.example.chris.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class CategoryInfo extends AppCompatActivity
        implements ReceiptRecView.ListItemClickListener {

    //Todo passe Wert an Zahl der Rechnungen an; max 100
    private static final int NUM_LIST_ITEMS = 100;
    static String TAG = "CategoryInfo";
    private TextView tv, tv2;

    /*
     * References to RecyclerView and Adapter to reset the list to its
     * "pretty" state when the reset menu item is clicked.
     */
    private ReceiptRecView mAdapter;
    private RecyclerView mNumbersList;

    // COMPLETED (9) Create a Toast variable called mToast to store the current Toast
    /*
     * If we hold a reference to our Toast, we can cancel it (if it's showing)
     * to display a new Toast. If we didn't do this, Toasts would be delayed
     * in showing up if you clicked many list items in quick succession.
     */
    private Toast mToast;

    static String TOTAL = "TOTAL", NAME= "NAME";
    String total, name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_info);

        Intent intent = getIntent();

        total = intent.getStringExtra(MainActivity.TOTAL);
        name = intent.getStringExtra(MainActivity.NAME);


        tv = (TextView) findViewById(R.id.name_supermarket);
        tv2 = (TextView) findViewById(R.id.total_supermarket);

        tv.setText(name);
        tv2.setText("Total: " + total);
        Log.d(TAG, "Values from Activity 1 succesful loaded");


        /*
         * Using findViewById, we get a reference to our RecyclerView from xml. This allows us to
         * do things like set the adapter of the RecyclerView and toggle the visibility.
         */
        mNumbersList = (RecyclerView) findViewById(R.id.rv_numbers);

        /*
         * A LinearLayoutManager is responsible for measuring and positioning item views within a
         * RecyclerView into a linear list. This means that it can produce either a horizontal or
         * vertical list depending on which parameter you pass in to the LinearLayoutManager
         * constructor. By default, if you don't specify an orientation, you get a vertical list.
         * In our case, we want a vertical list, so we don't need to pass in an orientation flag to
         * the LinearLayoutManager constructor.
         *
         * There are other LayoutManagers available to display your data in uniform grids,
         * staggered grids, and more! See the developer documentation for more details.
         */
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mNumbersList.setLayoutManager(layoutManager);

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mNumbersList.setHasFixedSize(true);

        // COMPLETED (13) Pass in this as the ListItemClickListener to the GreenAdapter constructor
        /*
         * The GreenAdapter is responsible for displaying each item in the list.
         */
        mAdapter = new ReceiptRecView(NUM_LIST_ITEMS, CategoryInfo.this);
        mNumbersList.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_category_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        switch (itemId) {
            /*
             * When you click the reset menu item, we want to start all over
             * and display the pretty gradient again. There are a few similar
             * ways of doing this, with this one being the simplest of those
             * ways. (in our humble opinion)
             */
            case R.id.back:
                // COMPLETED (14) Pass in this as the ListItemClickListener to the GreenAdapter constructor
                //mAdapter = new GreenAdapter2(NUM_LIST_ITEMS, this);
                //mNumbersList.setAdapter(mAdapter);
                Intent changeActivity = new Intent(CategoryInfo.this, MainActivity.class);
                startActivity(changeActivity);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // COMPLETED (10) Override ListItemClickListener's onListItemClick method
    /**
     * This is where we receive our callback from
     * {@link com.example.chris.myapplication.CategoryRecView.ListItemClickListener}
     *
     * This callback is invoked when you click on an item in the list.
     *
     * @param clickedItemIndex Index in the list of the item that was clicked.
     */
    @Override
    public void onListItemClick(int clickedItemIndex) {
        // COMPLETED (11) In the beginning of the method, cancel the Toast if it isn't null
        /*
         * Even if a Toast isn't showing, it's okay to cancel it. Doing so
         * ensures that our new Toast will show immediately, rather than
         * being delayed while other pending Toasts are shown.
         *
         * Comment out these three lines, run the app, and click on a bunch of
         * different items if you're not sure what I'm talking about.
         */
        if (mToast != null) {
            mToast.cancel();
        }

        // COMPLETED (12) Show a Toast when an item is clicked, displaying that item number that was clicked
        /*
         * Create a Toast and store it in our Toast field.
         * The Toast that shows up will have a message similar to the following:
         *
         *                     Item #42 clicked.
         */

        //Todo start new activity
        //String toastMessage = "Item #" + clickedItemIndex + " clicked.";
        //mToast = Toast.makeText(this, toastMessage, Toast.LENGTH_LONG);

        TOTAL = total;
        NAME = name;

        Intent changeActivity = new Intent(CategoryInfo.this, Receipt.class);
        changeActivity.putExtra(TOTAL, Integer.toString(clickedItemIndex));
        //changeActivity.putExtra(NAME, Categories.getCategory(clickedItemIndex));
        changeActivity.putExtra(NAME, name);


        Log.d(TAG, "Name: "+name);
        Log.d(TAG, "Name: "+NAME);

        Log.d(TAG, "start Receipt");
        startActivity(changeActivity);

        //mToast.show();
    }
}
