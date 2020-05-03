package com.example.chris.myapplication;

import android.app.ProgressDialog;
import android.os.StrictMode;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class getData {

    private static StringBuilder stringBuilder;

    static String TAG = "getData";

    public static String getData(String data, String extra) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //Create Connection
        try {
            String ServerUploadPath = "http://192.168.188.67:5000/getData";
            URL url = new URL(ServerUploadPath);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setReadTimeout(30000);
            httpURLConnection.setConnectTimeout(30000);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "text/plain");
            //httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);

            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(
                    new OutputStreamWriter(outputStream, "UTF-8"));
            String send = data;
            send = send + "\n" + extra;
            bufferedWriter.write((send));

            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();


            //Receive answer from server
            int RC = httpURLConnection.getResponseCode();
            if (RC == HttpsURLConnection.HTTP_OK) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                stringBuilder = new StringBuilder();
                String RC2;
                while ((RC2 = bufferedReader.readLine()) != null) {
                    stringBuilder.append(RC2);
                }
            }
            Log.d(TAG, "Answer data server: " + stringBuilder.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return stringBuilder.toString();
    }
}