package com.example.chris.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Random;

import static android.graphics.Color.rgb;

//controls the design of the activity
public class Fragment_trend extends Fragment {
    String TAG = "Fragment_trend";
    View fragView;
    ZoneId defaultZoneId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragView = inflater.inflate(R.layout.fragment_trend, container, false); //inflate up here and assign to variable

        String extra = getArguments().getString("CalledFrom");

        defaultZoneId = ZoneId.systemDefault();
        final String data = getData.getData("Trend", extra);
        Log.d(TAG, data);

        // Option  Box
        // layout instances
        Button buttonSubmit = (Button) fragView.findViewById(R.id.buttonSubmit);
        final RadioGroup radioGroupTime = (RadioGroup) fragView.findViewById(R.id.radioGroup_time);
        final RadioGroup radioGroupAgg = (RadioGroup) fragView.findViewById(R.id.radioGroup_aggregation);
        final RadioGroup radioGroupSort = (RadioGroup) fragView.findViewById(R.id.radioGroup_sort);

        /*
            Submit Button
        */
        buttonSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // get the selected RadioButton of the group
                RadioButton selectedTime  = (RadioButton)fragView.findViewById(radioGroupTime.getCheckedRadioButtonId());
                RadioButton selectedAgg  = (RadioButton)fragView.findViewById(radioGroupAgg.getCheckedRadioButtonId());
                RadioButton selectedSort  = (RadioButton)fragView.findViewById(radioGroupSort.getCheckedRadioButtonId());
                //get RadioButton text
                String voteTime = selectedTime.getText().toString();
                String voteAgg = selectedAgg.getText().toString();
                String voteSort = selectedSort.getText().toString();
                if (voteAgg.equals("Specific ")){
                    // get Company Name
                    EditText et_comp = fragView.findViewById(R.id.et_comp);
                    EditText et_prod = fragView.findViewById(R.id.et_product);
                    voteAgg = voteAgg +" of "+ et_comp.getText().toString() +" and " + et_prod.getText().toString();
                    Log.d("Test", "in if");
                }
                // display it as Toast to the user
                //Toast.makeText(MainActivity.this, "Selected Radio Button is: " + voteTime +"\n"+"Company: " + voteAgg +"\n" +"Product: " + voteSort, Toast.LENGTH_LONG).show();
                if (voteAgg.equals("Company")){
                    Date time = new Date();
                    java.time.LocalDate localDate;
                    switch (voteTime){
                        case "Lifetime":
                            localDate = LocalDate.now().minusDays(365*100);

                            time = Date.from(localDate.atStartOfDay(defaultZoneId).toInstant());

                        case "Year":
                            localDate = LocalDate.now().minusDays(365);

                            time = Date.from(localDate.atStartOfDay(defaultZoneId).toInstant());

                        case "Month":
                            localDate = LocalDate.now().minusDays(31);

                            time = Date.from(localDate.atStartOfDay(defaultZoneId).toInstant());

                        case "Week":
                            localDate = LocalDate.now().minusDays(7);

                            time = Date.from(localDate.atStartOfDay(defaultZoneId).toInstant());

                    }

                    companyPlot(data, time, voteSort, "all");
                }

                if (voteAgg.equals("Product")){
                    Date time = new Date();
                    java.time.LocalDate localDate;
                    switch (voteTime){
                        case "Lifetime":
                            localDate = LocalDate.now().minusDays(365*100);

                            time = Date.from(localDate.atStartOfDay(defaultZoneId).toInstant());

                        case "Year":
                            localDate = LocalDate.now().minusDays(365);

                            time = Date.from(localDate.atStartOfDay(defaultZoneId).toInstant());

                        case "Month":
                            localDate = LocalDate.now().minusDays(31);

                            time = Date.from(localDate.atStartOfDay(defaultZoneId).toInstant());

                        case "Week":
                            localDate = LocalDate.now().minusDays(7);

                            time = Date.from(localDate.atStartOfDay(defaultZoneId).toInstant());

                    }

                    productPlot(data, time, voteSort, "all");
                }

                if (voteAgg.equals("Specific")) {
                    Date time = new Date();
                    java.time.LocalDate localDate;
                    switch (voteTime) {
                        case "Lifetime":
                            localDate = LocalDate.now().minusDays(365 * 100);

                            time = Date.from(localDate.atStartOfDay(defaultZoneId).toInstant());

                        case "Year":
                            localDate = LocalDate.now().minusDays(365);

                            time = Date.from(localDate.atStartOfDay(defaultZoneId).toInstant());

                        case "Month":
                            localDate = LocalDate.now().minusDays(31);

                            time = Date.from(localDate.atStartOfDay(defaultZoneId).toInstant());

                        case "Week":
                            localDate = LocalDate.now().minusDays(7);

                            time = Date.from(localDate.atStartOfDay(defaultZoneId).toInstant());

                    }

                    EditText etc = fragView.findViewById(R.id.et_comp);
                    EditText etp = fragView.findViewById(R.id.et_product);
                    if (etc.getText().toString().equals("") && etp.getText().toString().equals("")) {
                        Toast.makeText(getActivity(), "Neither a company nor a product mentioned", Toast.LENGTH_LONG).show();
                    } else if (etc.getText().toString().equals("")) {
                        productPlot(data, time, voteSort, etp.getText().toString());
                    } else if (etp.getText().toString().equals("")) {
                        companyPlot(data, time, voteSort, etc.getText().toString());
                    } else {
                        combinationPlot(data, time, voteSort, etc.getText().toString(), etp.getText().toString());

                    }
                }
            }
        });


        return fragView; //return fragView
    }

    public void companyPlot(String data, Date timeArea, String sort, String company){
        Date start = new Date();

        int amount_of_plots =0;
        GraphView graph = fragView.findViewById(R.id.graph); //change getActivity() to fragView
        //int id = oldGraph.getId();


        //Delete old Graph
        //((ViewGroup) oldGraph.getParent()).removeView(oldGraph);

        //Create new graph
        //GraphView graph = new GraphView(getActivity());
        //graph.setId(id);

        LineGraphSeries series1 = new LineGraphSeries<>();
        LineGraphSeries series2 = new LineGraphSeries<>();
        LineGraphSeries series3 = new LineGraphSeries<>();
        LineGraphSeries series4 = new LineGraphSeries<>();
        LineGraphSeries series0 = new LineGraphSeries<>();

        int[] top5 = new int[5];
        int input = 0;
        String[] top_names = new String[5];

        try {
            JSONObject json = new JSONObject(data);
            int count = Integer.parseInt(json.getJSONArray("count").getJSONObject(0).getString("countCompany"));
            String[] read_names = new String[count];
            for (int k = 0; k < count; k++) {
                String name = json.getJSONArray("c"+Integer.toString(k)).getJSONObject(0).getString("name");

                Date[] x_values = new Date[count];
                double[] y_values = new double[count];

                // For every company the data String is checked once
                // name must be equal to company or all companies should be asked
                if (!Arrays.asList(read_names).contains(name) && (name.equals(company) || company.equals("all"))) {
                    Log.d(TAG, name);
                    read_names[k]=name;
                    amount_of_plots = amount_of_plots +1;
                    int value_count = 0;
                    Double sum_total = 0.00;
                    Log.d(TAG, "Test0");
                    for (int l = 0; l < count ; l++) {
                        if (name.equals(json.getJSONArray("c"+Integer.toString(l)).getJSONObject(0).getString("name")) ) {
                            //int[] x_tmp = new int[x_values.length];
                            //float[] y_tmp = new float[x_values.length];

                            String date_string = json.getJSONArray("c"+Integer.toString(l)).getJSONObject(0).getString("date");
                            String total = json.getJSONArray("c"+Integer.toString(l)).getJSONObject(0).getString("total");

                            Log.d(TAG, "k: "+ k+" l: "+ l);
                            Log.d(TAG, "Date: "+ date_string);
                            Log.d(TAG, "Total: "+ total);
                            DateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");

                            java.time.LocalDate localDate1 = java.time.LocalDate.parse(date_string);
                            x_values[value_count]=Date.from(localDate1.atStartOfDay(defaultZoneId).toInstant());
                            if (x_values[value_count].before(start)){
                                start = x_values[value_count];
                            }
                            if (sort.equals("Most")){
                                y_values[value_count] = value_count;
                            }
                            else {
                                y_values[value_count] = Double.parseDouble(total) + sum_total;
                                sum_total = sum_total + Double.parseDouble(total);
                            }

                            Log.d(TAG, "Sum: "+sum_total);
                            value_count++;


                        }
                    }

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
                        Log.d(TAG, "Value: "+Double.toString(y_values[m]));
                        DataPoint v = new DataPoint(x_values[m], y_values[m]);
                        values[m] = v;

                    }

                    Log.d(TAG, "input: "+input);
                    int index = 0;
                    if (input<4){
                        index = input;
                        input++;
                    }
                    else{
                        index = top5[0];

                        for(int i=0; i<top5.length; i++ ) {
                            if(top5[i] < index) {
                                index = top5[i];
                            }
                        }

                    }

                    Log.d(TAG, "Index: "+index);
                    top_names[index]=name;

                    if (index == 0) {
                        series0 = new LineGraphSeries<>(values);

                        Log.d(TAG, "in series0");
                    }
                    if (index == 1) {
                        series1 = new LineGraphSeries<>(values);

                        Log.d(TAG, "in 1");
                    }
                    if (index == 2) {
                        series2 = new LineGraphSeries<>(values);

                        Log.d(TAG, "in2");
                    }
                    if (index == 3) {
                        series3 = new LineGraphSeries<>(values);

                        Log.d(TAG, "in 3");
                    }
                    if (index == 4){
                            series4 = new LineGraphSeries<>(values);
                            Log.d(TAG, "in 4");
                    }

                }

            }
        }catch (Exception e){
            Log.d(TAG, "Error");
        }

        Log.d(TAG, "Test5");
        if (series0 != null){
            Log.d(TAG, "not Null");
        }
        graph.addSeries(series0);
        series0.setTitle(top_names[0]);
        series0.setColor(R.color.plot0);

        graph.addSeries(series1);
        series1.setTitle(top_names[1]);
        series1.setColor(R.color.plot1);

        graph.addSeries(series2);
        series2.setTitle(top_names[2]);
        series2.setColor(R.color.plot2);

        graph.addSeries(series3);
        series3.setTitle(top_names[3]);
        series3.setColor(R.color.plot3);

        graph.addSeries(series4);
        series4.setTitle(top_names[4]);
        series4.setColor(R.color.plot4);


        // set date label formatter
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
        graph.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space

// set manual x bounds to have nice steps
        Log.d(TAG, start.toString());
        if (timeArea.before(start)){
            graph.getViewport().setMinX(start.getTime());
        }
        else{
            graph.getViewport().setMinX(timeArea.getTime());
        }
        graph.getViewport().setMaxX(new Date().getTime());
        graph.getViewport().setXAxisBoundsManual(true);

// as we use dates as labels, the human rounding to nice readable numbers
// is not necessary
        graph.getGridLabelRenderer().setHumanRounding(false);


        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);

    }



    public void productPlot(String data, Date timeArea, String sort, String product){
        Date start = new Date();

        int amount_of_plots =0;
        GraphView graph = fragView.findViewById(R.id.graph); //change getActivity() to fragView

        LineGraphSeries series1 = new LineGraphSeries();
        LineGraphSeries series2 = new LineGraphSeries();
        LineGraphSeries series3 = new LineGraphSeries();
        LineGraphSeries series4 = new LineGraphSeries();
        LineGraphSeries series0 = new LineGraphSeries();

        int[] top5 = new int[5];
        int input = 0;
        String[] top_names = new String[5];


        try {
            JSONObject json = new JSONObject(data);
            int count = Integer.parseInt(json.getJSONArray("count").getJSONObject(0).getString("countProduct"));
            String[] read_names = new String[count];
            for (int k = 0; k < count; k++) {
                String name = json.getJSONArray("p"+Integer.toString(k)).getJSONObject(0).getString("name");

                Date[] x_values = new Date[count];
                double[] y_values = new double[count];

                if (!Arrays.asList(read_names).contains(name) && (name.equals(product) || product.equals("all"))) {
                    Log.d(TAG, name);
                    read_names[k]=name;
                    amount_of_plots = amount_of_plots +1;
                    int value_count = 0;
                    Double sum_total = 0.00;
                    Log.d(TAG, "Test0");
                    for (int l = 0; l < count ; l++) {
                        if (name.equals(json.getJSONArray("p"+Integer.toString(l)).getJSONObject(0).getString("name")) ) {
                            //int[] x_tmp = new int[x_values.length];
                            //float[] y_tmp = new float[x_values.length];

                            String date_string = json.getJSONArray("p"+Integer.toString(l)).getJSONObject(0).getString("date");
                            String total = json.getJSONArray("p"+Integer.toString(l)).getJSONObject(0).getString("price");

                            Log.d(TAG, "k: "+ k+" l: "+ l);
                            Log.d(TAG, "Date: "+ date_string);
                            Log.d(TAG, "Total: "+ total);
                            DateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");

                            java.time.LocalDate localDate1 = java.time.LocalDate.parse(date_string);
                            x_values[value_count]=Date.from(localDate1.atStartOfDay(defaultZoneId).toInstant());
                            if (x_values[value_count].before(start)){
                                start = x_values[value_count];
                            }
                            if (sort.equals("Most")){
                                y_values[value_count] = value_count;
                            }
                            else {
                                y_values[value_count] = Double.parseDouble(total) + sum_total;
                                sum_total = sum_total + Double.parseDouble(total);
                            }

                            Log.d(TAG, "Sum: "+sum_total);
                            value_count++;


                        }
                    }

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
                        Log.d(TAG, "Value: "+Double.toString(y_values[m]));
                        DataPoint v = new DataPoint(x_values[m], y_values[m]);
                        values[m] = v;

                    }

                    Log.d(TAG, "Test0");

                    int index;
                    if (input<4){
                        index = input;
                        input++;
                    }
                    else{
                        index = top5[0];

                        for(int i=0; i<top5.length; i++ ) {
                            if(top5[i] < index) {
                                index = top5[i];
                            }
                        }
                    }

                    Log.d(TAG, "Index: "+index);
                    top_names[index]=name;
                    switch (index){
                        case 0:
                            series0 = new LineGraphSeries<>(values);

                        case 1:
                            series1 = new LineGraphSeries<>(values);

                        case 2:
                            series2 = new LineGraphSeries<>(values);

                        case 3:
                            series3 = new LineGraphSeries<>(values);

                        case 4:
                            series4 = new LineGraphSeries<>(values);
                    }
                    LineGraphSeries<DataPoint> series = new LineGraphSeries<>(values);

                    Log.d(TAG, "Test2");
                }

            }
        }catch (Exception e){
            Log.d(TAG, "Error");
        }

        graph.addSeries(series0);
        series0.setTitle(top_names[0]);
        series0.setColor(R.color.plot0);

        graph.addSeries(series1);
        series1.setTitle(top_names[1]);
        series1.setColor(R.color.plot1);

        graph.addSeries(series2);
        series2.setTitle(top_names[2]);
        series2.setColor(R.color.plot2);

        graph.addSeries(series3);
        series3.setTitle(top_names[3]);
        series3.setColor(R.color.plot3);

        graph.addSeries(series4);
        series4.setTitle(top_names[4]);
        series4.setColor(R.color.plot4);

        // set date label formatter
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
        graph.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space

// set manual x bounds to have nice steps
        Log.d(TAG, start.toString());
        if (timeArea.before(start)){
            graph.getViewport().setMinX(start.getTime());
        }
        else{
            graph.getViewport().setMinX(timeArea.getTime());
        }
        graph.getViewport().setMaxX(new Date().getTime());
        graph.getViewport().setXAxisBoundsManual(true);

// as we use dates as labels, the human rounding to nice readable numbers
// is not necessary
        graph.getGridLabelRenderer().setHumanRounding(false);


        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);

    }



    public void combinationPlot(String data, Date timeArea, String sort, String product, String company){
        Date start = new Date();

        int amount_of_plots =0;
        GraphView graph = fragView.findViewById(R.id.graph); //change getActivity() to fragView

        LineGraphSeries series1 = new LineGraphSeries();
        LineGraphSeries series2 = new LineGraphSeries();
        LineGraphSeries series3 = new LineGraphSeries();
        LineGraphSeries series4 = new LineGraphSeries();
        LineGraphSeries series0 = new LineGraphSeries();

        int[] top5 = new int[5];
        int input = 0;
        String[] top_names = new String[5];


        try {
            JSONObject json = new JSONObject(data);
            int count = Integer.parseInt(json.getJSONArray("count").getJSONObject(0).getString("countProduct"));
            String[] read_names = new String[count];
            for (int k = 0; k < count; k++) {
                String name = json.getJSONArray("p"+Integer.toString(k)).getJSONObject(0).getString("name");
                String comp = json.getJSONArray("p"+Integer.toString(k)).getJSONObject(0).getString("company");

                Date[] x_values = new Date[count];
                double[] y_values = new double[count];

                if (!Arrays.asList(read_names).contains(name) && name.equals(product) && comp.equals(company)) {
                    Log.d(TAG, name);
                    read_names[k]=name;
                    amount_of_plots = amount_of_plots +1;
                    int value_count = 0;
                    Double sum_total = 0.00;
                    Log.d(TAG, "Test0");
                    for (int l = 0; l < count ; l++) {
                        if (name.equals(json.getJSONArray("p"+Integer.toString(l)).getJSONObject(0).getString("name")) ) {
                            //int[] x_tmp = new int[x_values.length];
                            //float[] y_tmp = new float[x_values.length];

                            String date_string = json.getJSONArray("p"+Integer.toString(l)).getJSONObject(0).getString("date");
                            String total = json.getJSONArray(""+Integer.toString(l)).getJSONObject(0).getString("price");

                            Log.d(TAG, "k: "+ k+" l: "+ l);
                            Log.d(TAG, "Date: "+ date_string);
                            Log.d(TAG, "Total: "+ total);
                            DateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");

                            java.time.LocalDate localDate1 = java.time.LocalDate.parse(date_string);
                            x_values[value_count]=Date.from(localDate1.atStartOfDay(defaultZoneId).toInstant());
                            if (x_values[value_count].before(start)){
                                start = x_values[value_count];
                            }
                            if (sort.equals("Most")){
                                y_values[value_count] = value_count;
                            }
                            else {
                                y_values[value_count] = Double.parseDouble(total) + sum_total;
                                sum_total = sum_total + Double.parseDouble(total);
                            }

                            Log.d(TAG, "Sum: "+sum_total);
                            value_count++;


                        }
                    }

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
                        Log.d(TAG, "Value: "+Double.toString(y_values[m]));
                        DataPoint v = new DataPoint(x_values[m], y_values[m]);
                        values[m] = v;

                    }


                    int index;
                    if (input<4){
                        index = input;
                        input++;
                    }
                    else{
                        index = top5[0];

                        for(int i=0; i<top5.length; i++ ) {
                            if(top5[i] < index) {
                                index = top5[i];
                            }
                        }

                    }

                    top_names[index]=name;
                    switch (index){
                        case 0:
                            series0 = new LineGraphSeries<>(values);

                        case 1:
                            series1 = new LineGraphSeries<>(values);

                        case 2:
                            series2 = new LineGraphSeries<>(values);

                        case 3:
                            series3 = new LineGraphSeries<>(values);

                        case 4:
                            series4 = new LineGraphSeries<>(values);
                    }
                    LineGraphSeries<DataPoint> series = new LineGraphSeries<>(values);

                }

            }
        }catch (Exception e){
            Log.d(TAG, "Error");
        }

        graph.addSeries(series0);
        series0.setTitle(top_names[0]);
        series0.setColor(R.color.plot0);

        graph.addSeries(series1);
        series1.setTitle(top_names[1]);
        series1.setColor(R.color.plot1);

        graph.addSeries(series2);
        series2.setTitle(top_names[2]);
        series2.setColor(R.color.plot2);

        graph.addSeries(series3);
        series3.setTitle(top_names[3]);
        series3.setColor(R.color.plot3);

        graph.addSeries(series4);
        series4.setTitle(top_names[4]);
        series4.setColor(R.color.plot4);
        // set date label formatter
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
        graph.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space

// set manual x bounds to have nice steps
        Log.d(TAG, start.toString());
        if (timeArea.before(start)){
            graph.getViewport().setMinX(start.getTime());
        }
        else{
            graph.getViewport().setMinX(timeArea.getTime());
        }
        graph.getViewport().setMaxX(new Date().getTime());
        graph.getViewport().setXAxisBoundsManual(true);

// as we use dates as labels, the human rounding to nice readable numbers
// is not necessary
        graph.getGridLabelRenderer().setHumanRounding(false);


        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);

    }


}
