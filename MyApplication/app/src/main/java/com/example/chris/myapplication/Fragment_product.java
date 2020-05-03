package com.example.chris.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import static android.graphics.Color.rgb;

//controls the design of the activity
public class Fragment_product extends Fragment {
    String TAG = "Fragment_product";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        String extra = getArguments().getString("CalledFrom");
        View nav_view = inflater.inflate(R.layout.fragment_company, container, false);

        Log.d(TAG, "ask for: " + extra);
        String data = getData.getData("Product", extra);

        TextView name = (TextView) nav_view.findViewById(R.id.name);
        name.setText("PRODUCTS");
        LinearLayout ll_h = nav_view.findViewById(R.id.linLayout_hor);
        TextView name1 = nav_view.findViewById(R.id.name1);
        TextView name2 = nav_view.findViewById(R.id.name2);
        LinearLayout.LayoutParams param_left = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);

        LinearLayout.LayoutParams param_right = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 2f);
        //Create new textviews
        name1.setText("Name");
        name1.setTextSize(35f);
        name1.setPadding(10, 10, 10, 10);

        name2.setText("Price");
        name2.setTextSize(35f);
        name2.setPadding(10, 10, 10, 10);

        //ll_h.addView(name1, param_left);
        //ll_h.addView(name2, param_right);

        Log.d(TAG, data);
        //Read JSON
        TableLayout tableLayout = nav_view.findViewById(R.id.table);
        //tableLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        while (true){
            if (tableLayout.getChildAt(0) != null){
                tableLayout.removeAllViews();
            }
            else {
                break;
            }
        }
        //tableLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        TableLayout.LayoutParams tableParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);

        //int red = Color.parseColor("#FF0000");
        //ll.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 3f));
        //ll.setBackgroundColor(red);
        //Read JSON
        try {
            JSONObject json = new JSONObject(data);
            int count = Integer.parseInt(json.getJSONArray("count").getJSONObject(0).getString("count"));
            for (int k = 1; k < count + 1; k++) {
                TableRow row = new TableRow(getActivity());
                //row.setPadding(10,10,10,10);
                //TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
                //row.setLayoutParams(tableParams);
                //row.setPadding(10, 10, 10, 10);
                row.setBackgroundColor(rgb(255, 255, 255));

                //int receipt_id = Integer.parseInt(json.getJSONArray(Integer.toString(k)).getJSONObject(0).getString("ID"));
                //row.setId(receipt_id);
                String receipt_name = json.getJSONArray(Integer.toString(k)).getJSONObject(0).getString("name");
                String receipt_price = json.getJSONArray(Integer.toString(k)).getJSONObject(0).getString("price");
                TextView text_name = new TextView(getActivity());
                text_name.setText(receipt_name);
                text_name.setPadding(10, 10, 10, 10);
                text_name.setTextSize(30f);
                text_name.setTextColor(getResources().getColor(R.color.font));
                //text_date.setBackgroundColor(rgb(255, 0, 0));
                TextView text_price = new TextView(getActivity());
                text_price.setText(receipt_price);
                text_price.setTextSize(30f);
                text_price.setTextColor(getResources().getColor(R.color.font));
                text_price.setPadding(10,10,10, 10);
                //text_total.setBackgroundColor(rgb(0,0,255));

                Log.d(TAG, receipt_name);
                Log.d(TAG, receipt_price);

                //RelativeLayout rl = new RelativeLayout(this);

                //RelativeLayout.LayoutParams params_price = new RelativeLayout.LayoutParams(RelativeLayout.ALIGN_PARENT_RIGHT, TRUE);

                //params_price.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, TRUE);
                //params_price.addRule(RelativeLayout.RIGHT_OF, 1);

                LinearLayout ll1 = new LinearLayout(getActivity());
                //ll1.setBackgroundColor(rgb(0, 255,0));
                /*LinearLayout.LayoutParams param_ll = new LinearLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        3.0f
                );*/
                //rl.setLayoutParams(rowParams);
                //product.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 2f));
                LinearLayout.LayoutParams params_ll1 = new TableLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT, 1f);
                LinearLayout.LayoutParams params_ll1a = new TableLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT, 2f);
                //text_date.setLayoutParams(new TableLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT, 2f));

                LinearLayout ll11 = new LinearLayout(getActivity());
                ll11.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                LinearLayout ll12 = new LinearLayout(getActivity());
                ll12.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                LinearLayout.LayoutParams param_ll11 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);

                LinearLayout.LayoutParams param_ll12 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);

                ll11.addView(text_name, param_ll11);
                ll12.addView(text_price, param_ll12);

                ll1.addView(ll11, params_ll1);
                ll1.addView(ll12, params_ll1a);

                //rl.addView(text_date,params);
                //text_total.setGravity(5);   // 5 means right

                //LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
                //text_total.setBackgroundColor(rgb(0, 0, 255));
                // Add all the rules you need
                //param.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                //rl.addView(text_total, param);

                row.addView(ll1);
                TableRow row_space = new TableRow(getActivity());
                row_space.setPadding(0,2,0,2);
                row_space.setBackgroundColor(getResources().getColor(R.color.colorSecond));
                tableLayout.addView(row_space, 2*k-2);

                tableLayout.addView(row, 2*k-1);

            }
            //c1x = json.getJSONArray("corner1").getJSONObject(0).getString("x");
            //c1y = json.getJSONArray("corner1").getJSONObject(0).getString("y");

            //size_height = json.getJSONArray("size").getJSONObject(0).getString("height");
            //size_width = json.getJSONArray("size").getJSONObject(0).getString("width");


            Log.d(TAG, "JSON read");
        } catch (
                JSONException e) {
            Toast.makeText(getContext(),"Problem during connecting with internet",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        return nav_view;
    }
}


