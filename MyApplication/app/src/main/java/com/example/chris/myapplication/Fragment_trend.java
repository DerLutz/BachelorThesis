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
public class Fragment_trend extends Fragment {
    String TAG = "Fragment_trend";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d(TAG, "in Fragment_trend");
        String extra = getArguments().getString("CalledFrom");
        View nav_view = inflater.inflate(R.layout.fragment_company, container, false);

        String data = getData.getData("Trend", extra);

        Log.d(TAG, data);
        //Read JSON
        TableLayout tableLayout = nav_view.findViewById(R.id.table);


        return nav_view;
    }
}


