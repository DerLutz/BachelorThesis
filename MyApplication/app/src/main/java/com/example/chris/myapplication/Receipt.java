package com.example.chris.myapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import static android.widget.RelativeLayout.TRUE;

public class Receipt extends AppCompatActivity {

    String name, total, id;
    static String NAME = "NAME";
    static String TAG = "Activity Receipt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);

        Intent intent = getIntent();

        id = intent.getStringExtra(CategoryInfo.ID);
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

        //int red = Color.parseColor("#FF0000");
        //ll.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 3f));
        //ll.setBackgroundColor(red);

        StringBuilder stringBuilder = response("Receipt", Integer.parseInt(id));
        //Read JSON
        try {
            JSONObject json = new JSONObject(stringBuilder.toString());
            int count = Integer.parseInt(json.getJSONArray("count").getJSONObject(0).getString("count"));
            for (int k = 1; k < count + 1; k++) {
                TableRow row = new TableRow(this);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
                row.setLayoutParams(lp);
                row.setPadding(10, 10, 10, 10);

                //int receipt_id = Integer.parseInt(json.getJSONArray(Integer.toString(k)).getJSONObject(0).getString("ID"));
                //row.setId(receipt_id);
                String receipt_name = json.getJSONArray(Integer.toString(k)).getJSONObject(0).getString("name");
                String receipt_price = json.getJSONArray(Integer.toString(k)).getJSONObject(0).getString("price");
                TextView text_date = new TextView(this);
                text_date.setText(receipt_name);
                TextView text_total = new TextView(this);
                text_total.setText(receipt_price);

                Log.d(TAG, receipt_name);
                Log.d(TAG, receipt_price);

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


                rl.addView(text_date, params);
                rl.addView(text_total);

                row.addView(rl);
                ll.addView(row, k);

            }
            //c1x = json.getJSONArray("corner1").getJSONObject(0).getString("x");
            //c1y = json.getJSONArray("corner1").getJSONObject(0).getString("y");

            //size_height = json.getJSONArray("size").getJSONObject(0).getString("height");
            //size_width = json.getJSONArray("size").getJSONObject(0).getString("width");


            Log.d("server", "JSON read");
        } catch (
                JSONException e) {
            e.printStackTrace();
        }



    }

    public StringBuilder response(String type, int id){

        HttpURLConnection httpURLConnection;
        URL url;
        OutputStream outputStream;
        BufferedWriter bufferedWriter;
        int RC, orientation;
        BufferedReader bufferedReader;
        StringBuilder stringBuilder = new StringBuilder();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //Create Connection
        try {
            String ServerUploadPath = "http://192.168.188.67:1337/getData";
            url = new URL(ServerUploadPath);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setReadTimeout(30000);
            httpURLConnection.setConnectTimeout(30000);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "text/plain");
            //httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);

            outputStream = httpURLConnection.getOutputStream();
            bufferedWriter = new BufferedWriter(
                    new OutputStreamWriter(outputStream, "UTF-8"));
            String send = "Products";
            send = send + "\n" + id;
            bufferedWriter.write((send));

            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();


            //Receive answer from server
            RC = httpURLConnection.getResponseCode();
            if (RC == HttpsURLConnection.HTTP_OK) {
                bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                String RC2;
                while ((RC2 = bufferedReader.readLine()) != null) {
                    stringBuilder.append(RC2);
                }
            }
            Log.d("Server", "Answer corner detection: " + stringBuilder.toString());
            Log.d(TAG, stringBuilder.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return stringBuilder;
    }
}
