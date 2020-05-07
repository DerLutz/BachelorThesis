package com.example.chris.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import static android.graphics.Color.rgb;

//controls the design of the activity
public class Fragment_trend extends Fragment {
    String TAG = "Fragment_trend";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragView = inflater.inflate(R.layout.fragment_trend, container, false); //inflate up here and assign to variable

        String extra = getArguments().getString("CalledFrom");

        ZoneId defaultZoneId = ZoneId.systemDefault();
        String data = getData.getData("Trend", extra);
        Log.d(TAG, data);

        Date start = new Date();

        int amount_of_plots =0;
        GraphView graph = fragView.findViewById(R.id.graph); //change getActivity() to fragView

        try {
            JSONObject json = new JSONObject(data);
            int count = Integer.parseInt(json.getJSONArray("count").getJSONObject(0).getString("count"));
            String[] read_names = new String[count];
            for (int k = 0; k < count; k++) {
                String name = json.getJSONArray(Integer.toString(k)).getJSONObject(0).getString("name");

                Date[] x_values = new Date[count];
                double[] y_values = new double[count];
                Log.d(TAG, "Test");
                if (!Arrays.asList(read_names).contains(name)) {
                    Log.d(TAG, name);
                    read_names[k]=name;
                    amount_of_plots = amount_of_plots +1;
                    int value_count = 0;
                    Double sum_total = 0.00;
                    Log.d(TAG, "Test0");
                    for (int l = 0; l < count ; l++) {
                        if (name.equals(json.getJSONArray(Integer.toString(l)).getJSONObject(0).getString("name")) ) {
                            //int[] x_tmp = new int[x_values.length];
                            //float[] y_tmp = new float[x_values.length];

                            String date_string = json.getJSONArray(Integer.toString(l)).getJSONObject(0).getString("date");
                            String total = json.getJSONArray(Integer.toString(l)).getJSONObject(0).getString("total");

                            Log.d(TAG, "k: "+ k+" l: "+ l);
                            Log.d(TAG, "Date: "+ date_string);
                            Log.d(TAG, "Total: "+ total);
                            DateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");

                            java.time.LocalDate localDate1 = java.time.LocalDate.parse(date_string);
                            x_values[value_count]=Date.from(localDate1.atStartOfDay(defaultZoneId).toInstant());
                            if (x_values[value_count].before(start)){
                                start = x_values[value_count];
                            }

                            y_values[value_count] = Double.parseDouble(total) + sum_total;
                            sum_total = sum_total + Double.parseDouble(total);
                            Log.d(TAG, "Sum: "+sum_total);
                            value_count++;


                        }
                    }

                    Log.d(TAG, "Test1");
                    Log.d(TAG, Integer.toString(value_count));

                    //sort earliest date to today
                    Date temp_x;
                    double temp_y;
                    for(int i=1; i<value_count; i++) {
                        for(int j=0; j<value_count-i; j++) {
                            if(x_values[j].after(x_values[j+1])) {
                                temp_x=x_values[j];
                                x_values[j]=x_values[j+1];
                                x_values[j+1]=temp_x;

                                temp_y=y_values[j];
                                y_values[j]=y_values[j+1];
                                y_values[j+1]=temp_y;
                            }

                        }
                    }
                    DataPoint[] values = new DataPoint[value_count];
                    for (int m =0; m<value_count; m++){
                        Log.d(TAG, "m: "+ Integer.toString(m));
                        Log.d(TAG, "date: "+ x_values[m]);
                        Log.d(TAG, "price: "+Double.toString(y_values[m]));
                        DataPoint v = new DataPoint(x_values[m], y_values[m]);
                        values[m] = v;

                    }


                    BarGraphSeries<DataPoint> series = new BarGraphSeries<>(values);
                    graph.addSeries(series);
                    series.setTitle(name);

                    Random r1 = new Random();
                    Random r2 = new Random();
                    Random r3 = new Random();
                    int low = 0;
                    int high = 255;
                    int result1 = r1.nextInt(high-low) + low;
                    int result2 = r1.nextInt(high-low) + low;
                    int result3 = r1.nextInt(high-low) + low;
                    series.setColor(rgb(result1, result2, result3));
                }
                Log.d(TAG, "Test4");
            }
        }catch (Exception e){
            Log.d(TAG, "Error");
        }

        // set date label formatter
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
        graph.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space

// set manual x bounds to have nice steps
        Log.d(TAG, start.toString());
        graph.getViewport().setMinX(start.getTime());
        graph.getViewport().setMaxX(new Date().getTime());
        graph.getViewport().setXAxisBoundsManual(true);

// as we use dates as labels, the human rounding to nice readable numbers
// is not necessary
        graph.getGridLabelRenderer().setHumanRounding(false);


        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);

        return fragView; //return fragView
    }

}


