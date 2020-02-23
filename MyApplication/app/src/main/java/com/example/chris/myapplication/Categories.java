package com.example.chris.myapplication;

import android.os.StrictMode;
import android.util.Log;
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

public class Categories {
    // List of all known Categories
    // Unknown category to Others
    // Todo Create categories from json/string
    HttpURLConnection httpURLConnection;
    URL url;
    OutputStream outputStream;
    BufferedWriter bufferedWriter;
    int RC, orientation;
    BufferedReader bufferedReader;
    StringBuilder stringBuilder;
    private String server_url = "http://192.168.188.67:1337/getData";

    public static String getCategoryName(StringBuilder stringBuilder, int number) {
        String name;
        Log.d("Categories: ", "number: "+number);
        number = number+1;
        try {
            JSONObject json = new JSONObject(stringBuilder.toString());
            //int count = Integer.parseInt(json.getJSONArray("count").getJSONObject(0).getString("count"));
            //for (int k=1; k<count+1; k++){
            //int receipt_id = Integer.parseInt(json.getJSONArray(Integer.toString(k)).getJSONObject(0).getString("ID"));
            name = json.getJSONArray(Integer.toString(number)).getJSONObject(0).getString("name");
            //String receipt_total = json.getJSONArray(Integer.toString(k)).getJSONObject(0).getString("total");



            //c1x = json.getJSONArray("corner1").getJSONObject(0).getString("x");
            //c1y = json.getJSONArray("corner1").getJSONObject(0).getString("y");

            //size_height = json.getJSONArray("size").getJSONObject(0).getString("height");
            //size_width = json.getJSONArray("size").getJSONObject(0).getString("width");


            Log.d("server", "JSON read");
        }
        catch (
                JSONException e) {
            name = "Error";
            Log.d("Categories: ", "Number in Error: "+number);
            e.printStackTrace();
        }
        return name;

    }
    public static String getCategoryTotal(StringBuilder stringBuilder, int number) {
        String total;
        number = number+1;
        try {
            JSONObject json = new JSONObject(stringBuilder.toString());
            //int count = Integer.parseInt(json.getJSONArray("count").getJSONObject(0).getString("count"));
            //for (int k=1; k<count+1; k++){
            //int receipt_id = Integer.parseInt(json.getJSONArray(Integer.toString(k)).getJSONObject(0).getString("ID"));
            total = json.getJSONArray(Integer.toString(number)).getJSONObject(0).getString("total");
            Log.d("Categories", "total: "+total  + " number: "+Integer.toString(number));
            //String receipt_total = json.getJSONArray(Integer.toString(k)).getJSONObject(0).getString("total");



            //c1x = json.getJSONArray("corner1").getJSONObject(0).getString("x");
            //c1y = json.getJSONArray("corner1").getJSONObject(0).getString("y");

            //size_height = json.getJSONArray("size").getJSONObject(0).getString("height");
            //size_width = json.getJSONArray("size").getJSONObject(0).getString("width");


            Log.d("server", "JSON read");
        }
        catch (
                JSONException e) {
            total = "Error";
            e.printStackTrace();
        }
        return total;

    }
}
