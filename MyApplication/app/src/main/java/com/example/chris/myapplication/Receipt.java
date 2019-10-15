package com.example.chris.myapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import static android.widget.RelativeLayout.TRUE;

public class Receipt extends AppCompatActivity {

    String name, total;
    static String NAME="NAME";
    static String TAG = "Activity Receipt";

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_receipt);

            Intent intent = getIntent();

            total = intent.getStringExtra(CategoryInfo.TOTAL);
            name = intent.getStringExtra(CategoryInfo.NAME);
            Log.d("Activity Receipt: ", name);

            TextView tv = (TextView) findViewById(R.id.name);
            TextView tv2 = (TextView) findViewById(R.id.total);

            tv.setText(name);
            tv2.setText("Total: " + total);


            //Log.d(TAG, "Values from Activity 1 succesful loaded");


            init();
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
                Intent changeActivity = new Intent(Receipt.this, CategoryInfo.class);
                changeActivity.putExtra(NAME, name);
                startActivity(changeActivity);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void init() {
        TableLayout ll = (TableLayout) findViewById(R.id.table);

        int red = Color.parseColor("#FF0000");
        //ll.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 3f));
        //ll.setBackgroundColor(red);

        for (int i = 0; i < 16; i++) {

                TableRow row = new TableRow(this);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
                row.setLayoutParams(lp);
                row.setPadding(10, 10, 10, 10);
                TextView product = new TextView(this);
                TextView price = new TextView(this);
                product.setText("product name " + i);
                price.setText("€ 10");
                product.setTextSize(25);
                price.setTextSize(25);
                int id =1;
                product.setId(id);

                //RelativeLayout rl = new RelativeLayout(this);

                //RelativeLayout.LayoutParams params_price = new RelativeLayout.LayoutParams(RelativeLayout.ALIGN_PARENT_RIGHT, TRUE);

                //params_price.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, TRUE);
                //params_price.addRule(RelativeLayout.RIGHT_OF, 1);

                LinearLayout rl = new LinearLayout(this);
                /*LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        3.0f
                );
                rl.setLayoutParams(param);*/
                //product.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 2f));
                LinearLayout.LayoutParams params = new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 8f);


            rl.addView(product, params);
                rl.addView(price);

                row.addView(rl);
                ll.addView(row, i);
            }
        }}
