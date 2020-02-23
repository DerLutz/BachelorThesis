package com.example.chris.myapplication;

import android.os.StrictMode;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class server {
    HttpURLConnection httpURLConnection ;
    URL url;
    OutputStream outputStream;
    BufferedWriter bufferedWriter ;
    int RC, orientation;
    BufferedReader bufferedReader ;
    StringBuilder stringBuilder;
    private String server_url = "http://192.168.89.136:1337/getData";

    public StringBuilder getResponse(String input, int id){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //Create Connection
        try{
            String ServerUploadPath = server_url;
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
            String send = input;
            send = send + "\n" + Integer.toString(id);

            bufferedWriter.write((input));

            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();


            //Receive answer from server
            RC = httpURLConnection.getResponseCode();
            if (RC == HttpsURLConnection.HTTP_OK) {
                bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                stringBuilder = new StringBuilder();
                String RC2;
                while ((RC2 = bufferedReader.readLine()) != null){
                    stringBuilder.append(RC2);
                }
            }
            Log.d("Server", "Answer corner detection: " + stringBuilder.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuilder;
    }
}
