package com.example.chris.myapplication;

//import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import static android.graphics.Color.rgb;

//Todo at moment present image after cut, later upload image an present the text
//Cut the image and stretch so we get only the recipe
public class OCRActivity extends AppCompatActivity {

    Uri picUri;
    String uri;
    String TAG = "OCRActivity";
    String c1x, c1y, c2x, c2y, c3x, c3y, c4x, c4y, size_height, size_width;
    String ServerUploadPath ="http://192.168.178.44:1337/ocr";
    String FinalData;
    String ResultData;
    ProgressDialog progressDialog ;
    ByteArrayOutputStream byteArrayOutputStream ;
    byte[] byteArray ;
    String ConvertImage ;
    HttpURLConnection httpURLConnection ;
    URL url;
    OutputStream outputStream;
    BufferedWriter bufferedWriter ;
    int RC, orientation;
    BufferedReader bufferedReader ;
    Bitmap FixBitmap;
    int count;
    StringBuilder stringBuilder;
    //Needs to global because it is changed in remove of rows
    int counter;

    Bitmap newImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);

        Log.d(TAG, "OCR Activity started");

        cut_image();
    }

    private void cut_image(){


        Intent intent = getIntent();

        uri = intent.getStringExtra(CornerActivity.URI_FILE);
        Log.d(TAG, uri);

        try {


            c1x = intent.getStringExtra(CornerActivity.X0);
            c1y = intent.getStringExtra(CornerActivity.Y0);
            c2x = intent.getStringExtra(CornerActivity.X1);
            c2y = intent.getStringExtra(CornerActivity.Y1);
            c3x = intent.getStringExtra(CornerActivity.X2);
            c3y = intent.getStringExtra(CornerActivity.Y2);
            c4x = intent.getStringExtra(CornerActivity.X3);
            c4y = intent.getStringExtra(CornerActivity.Y3);

            size_height = intent.getStringExtra(CornerActivity.IMG_HEIGHT);
            size_width = intent.getStringExtra(CornerActivity.IMG_WIDTH);

            orientation =  Integer.parseInt(intent.getStringExtra(CornerActivity.O));
        }
        catch (Exception e){
            e.printStackTrace();
        }

        Log.d(TAG, "read Values");

        Log.d(TAG, uri);
        picUri = Uri.parse(uri);



        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), picUri);
            float bmp_height = bitmap.getHeight();
            float bmp_width = bitmap.getWidth();
            Log.d(TAG, "Height: "+bmp_height);
            Log.d(TAG, "Width: "+bitmap.getWidth());
            Log.d(TAG, "Height Image: "+size_height);
            Log.d(TAG, "Width Image: "+size_width);

            float ratio_height = bmp_height/((float) Integer.parseInt(size_height));
            float ratio_width = bmp_width/((float) Integer.parseInt(size_width));


            //bitmap = rotatedBitmap;

            Mat original_image = new Mat();
            Utils.bitmapToMat(bitmap, original_image);

            Log.d(TAG, "ratio: "+ratio_height);
            Log.d(TAG, "c1y: "+(Integer.parseInt(c1y)*ratio_height));
            Log.d(TAG, "c1x: "+(Integer.parseInt(c1x)*ratio_width));

            /* only draw points for checking that i have the correct at bitmap
            Imgproc.circle(original_image, new Point(Integer.parseInt(c1x)*ratio_width, Integer.parseInt(c1y)*ratio_height), 10, new Scalar(255, 0, 0), 10, 8, 0);
            Imgproc.circle(original_image, new Point(Integer.parseInt(c2x)*ratio_width, Integer.parseInt(c2y)*ratio_height), 10, new Scalar(355, 0, 0), 10, 8, 0);
            Imgproc.circle(original_image, new Point(Integer.parseInt(c3x)*ratio_width, Integer.parseInt(c3y)*ratio_height), 10, new Scalar(255, 0, 0), 10, 8, 0);
            Imgproc.circle(original_image, new Point(Integer.parseInt(c4x)*ratio_width, Integer.parseInt(c4y)*ratio_height), 10, new Scalar(155, 0, 0), 10, 8, 0);
            */

            //Move detected corner in corners of whole image; affine transform
            Point[] srcTri = new Point[4];
            srcTri[0] = new Point(Integer.parseInt(c1x)*ratio_width, Integer.parseInt(c1y)*ratio_height );
            srcTri[1] = new Point( Integer.parseInt(c2x)*ratio_width, Integer.parseInt(c2y)*ratio_height );
            srcTri[2] = new Point( Integer.parseInt(c3x)*ratio_width, Integer.parseInt(c3y)*ratio_height );
            srcTri[3] = new Point( Integer.parseInt(c4x)*ratio_width, Integer.parseInt(c4y)*ratio_height );

            //Where the points should be

            Point[] disTri = new Point[4];
            disTri[0] = new Point( 0, 0 );
            disTri[1] = new Point( 0, original_image.rows() );
            disTri[2] = new Point( original_image.cols(), 0 );
            disTri[3] = new Point( original_image.cols(), original_image.rows() );

            if (orientation == 0){
                disTri[0] = new Point( 0, 0 );
                disTri[2] = new Point( 0, original_image.rows() );
                disTri[1] = new Point( original_image.cols(), 0 );
                disTri[3] = new Point( original_image.cols(), original_image.rows() );
            }

            Mat warpMat = Imgproc.getPerspectiveTransform(new MatOfPoint2f(srcTri), new MatOfPoint2f(disTri));
            //Mat warpMat = Imgproc.getPerspectiveTransform( new MatOfPoint2f(srcTri), new MatOfPoint2f(disTri) );

            Mat warpDst = Mat.zeros( original_image.rows(), original_image.cols(), original_image.type() );

            Log.d(TAG, "start AffineTransform");
            Imgproc.warpPerspective( original_image, warpDst, warpMat, warpDst.size() );
            Log.d(TAG, "AffineTransform done");



            Utils.matToBitmap(warpDst, bitmap);

            if (bitmap == null)
                Log.d(TAG, "Bitmap bitmap Empty");
            FixBitmap = bitmap;

            if (FixBitmap == null)
                Log.d(TAG, "Bitmap FixBitmap Empty");
            // Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

            //((ImageView)findViewById(R.id.image)).setImageBitmap(bitmap);

        }
        catch (Exception e){
            Log.d(TAG, "can not load image file");
            e.printStackTrace();
            Toast.makeText(OCRActivity.this, "Can not load image file",Toast.LENGTH_SHORT).show();
        }

        UploadImageToServer();
        visualize_results();
        Button addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        addRow();
                    }
                }
        );

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu3, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.save) {
            save();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

        if (id == R.id.add){
            TableLayout tableLayout = findViewById(R.id.table);
            TableRow row = new TableRow(this);
            row.setPadding(10,10,10,10);
            EditText text_a = new EditText(this);
            text_a.setPadding(10, 10, 10, 10);
            text_a.setTextSize(30f);
            text_a.setId(count+1);
            //text_date.setBackgroundColor(rgb(255, 0, 0));
            EditText text_b = new EditText(this);
            text_b.setTextSize(30f);
            text_b.setPadding(10,10,10, 10);
            text_b.setId(-count);
            //text_total.setBackgroundColor(rgb(0,0,255));

            LinearLayout ll1 = new LinearLayout(this);
            //ll1.setBackgroundColor(rgb(0, 255,0));
            LinearLayout.LayoutParams params_ll1 = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
            LinearLayout.LayoutParams params_ll1a = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 2f);

            LinearLayout ll11 = new LinearLayout(this);
            //ll11.setBackgroundColor(rgb(255,255,0));
            LinearLayout ll12 = new LinearLayout(this);
            //ll12.setBackgroundColor(rgb(0,255,255));

            LinearLayout.LayoutParams param_ll11 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);

            LinearLayout.LayoutParams param_ll12 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);

            ll11.addView(text_a, param_ll11);
            ll12.addView(text_b, param_ll12);

            ll1.addView(ll11, params_ll1);
            ll1.addView(ll12, params_ll1a);

            row.addView(ll1);
            tableLayout.addView(row, count-1);
            count++;

        }

        if (id==R.id.back){
            Intent intent = new Intent(this, CornerActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public void UploadImageToServer() {

        Log.d(TAG, "Start upload to server");

        byteArrayOutputStream = new ByteArrayOutputStream();

        // Prepare image for upload
        FixBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byteArray = byteArrayOutputStream.toByteArray();
        ConvertImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
        uploadImage(ConvertImage);
    }

        private void uploadImage(String data) {

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //Create Connection
            try {
                String ServerUploadPath = "http://192.168.188.54:5001/ocr";
                url = new URL(ServerUploadPath);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(60000);
                httpURLConnection.setConnectTimeout(60000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "image/jpeg");
                //httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);

                outputStream = httpURLConnection.getOutputStream();
                bufferedWriter = new BufferedWriter(
                        new OutputStreamWriter(outputStream, "UTF-8"));
                String send = data;
                bufferedWriter.write((send));

                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();


                //Receive answer from server
                RC = httpURLConnection.getResponseCode();
                if (RC == HttpsURLConnection.HTTP_OK) {
                    bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    stringBuilder = new StringBuilder();
                    String RC2;
                    while ((RC2 = bufferedReader.readLine()) != null) {
                        stringBuilder.append(RC2);
                    }
                }
                Log.d(TAG, "Answer OCR result: " + stringBuilder.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    private void visualize_results(){
        final TableLayout tableLayout = (TableLayout) findViewById(R.id.table);

        try {
            JSONObject json = new JSONObject(stringBuilder.toString());
            count = Integer.parseInt(json.getJSONArray("count").getJSONObject(0).getString("count"));

            // Distinguish between first 3 Lines and he others because Company, Date and Total have to bet set for server
            //counter shows what
            counter = 1;
            String currentLine = json.getJSONArray(Integer.toString(counter)).getJSONObject(0).getString("a");
            if (currentLine.equals("Company")){
                TableRow row = new TableRow(this);
                row.setPadding(10,10,10,10);
                row.setBackgroundColor(rgb(255, 255, 255));

                // Add Row Option
                row.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(OCRActivity.this);
                        builder.setCancelable(true);
                        builder.setTitle("Select");
                        builder.setMessage("Add missing products or remove current row");
                        builder.setPositiveButton("Add",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        addRow();
                                    }
                                });

                        AlertDialog dialog = builder.create();
                        dialog.show();

                        return true;
                    }
                });

                //a represent first column, b second column
                String a = json.getJSONArray(Integer.toString(1)).getJSONObject(0).getString("a");
                Log.d(TAG, a);
                String b = json.getJSONArray(Integer.toString(1)).getJSONObject(1).getString("b");
                Log.d(TAG, b);
                TextView text_a = new TextView(this);
                text_a.setText(a);
                text_a.setPadding(10, 10, 10, 10);
                text_a.setTextSize(20f);
                text_a.setId(2*1-1);    //id has to be positive. Order: a1=1, b1=2, a2=3, b2=4, ..

                EditText text_b = new EditText(this);
                text_b.setText(b);
                text_b.setTextSize(20f);
                text_b.setPadding(10,10,10, 10);
                text_b.setId(2*1); //id has to be positive. Order: a1=1, b1=2, a2=3, b2=4, ..

                LinearLayout ll1 = new LinearLayout(this);
                LinearLayout.LayoutParams params_ll1 = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
                LinearLayout.LayoutParams params_ll1a = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 2f);

                LinearLayout ll11 = new LinearLayout(this);
                LinearLayout ll12 = new LinearLayout(this);

                LinearLayout.LayoutParams param_ll11 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);

                LinearLayout.LayoutParams param_ll12 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);

                ll11.addView(text_a, param_ll11);
                ll12.addView(text_b, param_ll12);

                ll1.addView(ll11, params_ll1);
                ll1.addView(ll12, params_ll1a);

                row.addView(ll1);
                TableRow row_space = new TableRow(this);
                row_space.setPadding(10,10,10,10);
                tableLayout.addView(row_space, 2*1-2);

                tableLayout.addView(row, 2*1-1);


                counter++;

            }
            else{
                TableRow row = new TableRow(this);
                row.setPadding(10,10,10,10);
                row.setBackgroundColor(rgb(255, 255, 255));

                // Add Row Option
                row.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(OCRActivity.this);
                        builder.setCancelable(true);
                        builder.setTitle("Select");
                        builder.setMessage("Add missing products or remove current row");
                        builder.setPositiveButton("Add",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        addRow();
                                    }
                                });

                        AlertDialog dialog = builder.create();
                        dialog.show();

                        return true;
                    }
                });

                TextView text_a = new TextView(this);
                text_a.setText("Company");
                text_a.setPadding(10, 10, 10, 10);
                text_a.setTextSize(20f);
                text_a.setId(2*1-1);    //id has to be positive. Order: a1=1, b1=2, a2=3, b2=4, ..
                EditText text_b = new EditText(this);
                text_b.setText("");
                text_b.setTextSize(20f);
                text_b.setPadding(10,10,10, 10);
                text_b.setId(2*1); //id has to be positive. Order: a1=1, b1=2, a2=3, b2=4, ..

                LinearLayout ll1 = new LinearLayout(this);
                LinearLayout.LayoutParams params_ll1 = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
                LinearLayout.LayoutParams params_ll1a = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 2f);

                LinearLayout ll11 = new LinearLayout(this);
                LinearLayout ll12 = new LinearLayout(this);

                LinearLayout.LayoutParams param_ll11 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);

                LinearLayout.LayoutParams param_ll12 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);

                ll11.addView(text_a, param_ll11);
                ll12.addView(text_b, param_ll12);

                ll1.addView(ll11, params_ll1);
                ll1.addView(ll12, params_ll1a);

                row.addView(ll1);
                TableRow row_space = new TableRow(this);
                row_space.setPadding(10,10,10,10);
                tableLayout.addView(row_space, 2*1-2);

                tableLayout.addView(row, 2*1-1);

            }

            //Check Date
            currentLine = json.getJSONArray(Integer.toString(counter)).getJSONObject(0).getString("a");
            if (currentLine.equals("Date")){
                TableRow row = new TableRow(this);
                row.setPadding(10,10,10,10);
                row.setBackgroundColor(rgb(255, 255, 255));

                // Add Row Option
                row.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(OCRActivity.this);
                        builder.setCancelable(true);
                        builder.setTitle("Select");
                        builder.setMessage("Add missing products or remove current row");
                        builder.setPositiveButton("Add",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        addRow();
                                    }
                                });

                        AlertDialog dialog = builder.create();
                        dialog.show();

                        return true;
                    }
                });


                //a represent first column, b second column
                String a = json.getJSONArray(Integer.toString(counter)).getJSONObject(0).getString("a");
                Log.d(TAG, a);
                String b = json.getJSONArray(Integer.toString(counter)).getJSONObject(1).getString("b");
                Log.d(TAG, b);
                TextView text_a = new TextView(this);
                text_a.setText(a);
                text_a.setPadding(10, 10, 10, 10);
                text_a.setTextSize(20f);
                text_a.setId(2*2-1);    //id has to be positive. Order: a1=1, b1=2, a2=3, b2=4, ..

                EditText text_b = new EditText(this);
                text_b.setText(b);
                text_b.setTextSize(20f);
                text_b.setPadding(10,10,10, 10);
                text_b.setId(2*2); //id has to be positive. Order: a1=1, b1=2, a2=3, b2=4, ..

                LinearLayout ll1 = new LinearLayout(this);
                LinearLayout.LayoutParams params_ll1 = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
                LinearLayout.LayoutParams params_ll1a = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 2f);

                LinearLayout ll11 = new LinearLayout(this);
                LinearLayout ll12 = new LinearLayout(this);

                LinearLayout.LayoutParams param_ll11 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);

                LinearLayout.LayoutParams param_ll12 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);

                ll11.addView(text_a, param_ll11);
                ll12.addView(text_b, param_ll12);

                ll1.addView(ll11, params_ll1);
                ll1.addView(ll12, params_ll1a);

                row.addView(ll1);
                TableRow row_space = new TableRow(this);
                row_space.setPadding(10,10,10,10);
                tableLayout.addView(row_space, 2*2-2);

                tableLayout.addView(row, 2*2-1);


                counter++;

            }
            else{
                TableRow row = new TableRow(this);
                row.setPadding(10,10,10,10);
                row.setBackgroundColor(rgb(255, 255, 255));

                // Add Row Option
                row.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(OCRActivity.this);
                        builder.setCancelable(true);
                        builder.setTitle("Select");
                        builder.setMessage("Add missing products or remove current row");
                        builder.setPositiveButton("Add",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        addRow();
                                    }
                                });

                        AlertDialog dialog = builder.create();
                        dialog.show();

                        return true;
                    }
                });


                TextView text_a = new TextView(this);
                text_a.setText("Date");
                text_a.setPadding(10, 10, 10, 10);
                text_a.setTextSize(20f);
                text_a.setId(2*2-1);    //id has to be positive. Order: a1=1, b1=2, a2=3, b2=4, ..
                EditText text_b = new EditText(this);
                text_b.setText("");
                text_b.setTextSize(20f);
                text_b.setPadding(10,10,10, 10);
                text_b.setId(2*2); //id has to be positive. Order: a1=1, b1=2, a2=3, b2=4, ..

                LinearLayout ll1 = new LinearLayout(this);
                LinearLayout.LayoutParams params_ll1 = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
                LinearLayout.LayoutParams params_ll1a = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 2f);

                LinearLayout ll11 = new LinearLayout(this);
                LinearLayout ll12 = new LinearLayout(this);

                LinearLayout.LayoutParams param_ll11 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);

                LinearLayout.LayoutParams param_ll12 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);

                ll11.addView(text_a, param_ll11);
                ll12.addView(text_b, param_ll12);

                ll1.addView(ll11, params_ll1);
                ll1.addView(ll12, params_ll1a);

                row.addView(ll1);
                TableRow row_space = new TableRow(this);
                row_space.setPadding(10,10,10,10);
                tableLayout.addView(row_space, 2*2-2);

                tableLayout.addView(row, 2*2-1);
            }

            // For Total
            currentLine = json.getJSONArray(Integer.toString(counter)).getJSONObject(0).getString("a");
            if (currentLine.equals("Total")){
                TableRow row = new TableRow(this);
                row.setPadding(10,10,10,10);
                row.setBackgroundColor(rgb(255, 255, 255));

                // Add Row Option
                row.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(OCRActivity.this);
                        builder.setCancelable(true);
                        builder.setTitle("Select");
                        builder.setMessage("Add missing products or remove current row");
                        builder.setPositiveButton("Add",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        addRow();
                                    }
                                });

                        AlertDialog dialog = builder.create();
                        dialog.show();

                        return true;
                    }
                });


                //a represent first column, b second column
                String a = json.getJSONArray(Integer.toString(counter)).getJSONObject(0).getString("a");
                Log.d(TAG, a);
                String b = json.getJSONArray(Integer.toString(counter)).getJSONObject(1).getString("b");
                Log.d(TAG, b);
                TextView text_a = new TextView(this);
                text_a.setText(a);
                text_a.setPadding(10, 10, 10, 10);
                text_a.setTextSize(20f);
                text_a.setId(2*3-1);    //id has to be positive. Order: a1=1, b1=2, a2=3, b2=4, ..

                EditText text_b = new EditText(this);
                text_b.setText(b);
                text_b.setTextSize(20f);
                text_b.setPadding(10,10,10, 10);
                text_b.setId(2*3); //id has to be positive. Order: a1=1, b1=2, a2=3, b2=4, ..

                LinearLayout ll1 = new LinearLayout(this);
                LinearLayout.LayoutParams params_ll1 = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
                LinearLayout.LayoutParams params_ll1a = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 2f);

                LinearLayout ll11 = new LinearLayout(this);
                LinearLayout ll12 = new LinearLayout(this);

                LinearLayout.LayoutParams param_ll11 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);

                LinearLayout.LayoutParams param_ll12 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);

                ll11.addView(text_a, param_ll11);
                ll12.addView(text_b, param_ll12);

                ll1.addView(ll11, params_ll1);
                ll1.addView(ll12, params_ll1a);

                row.addView(ll1);
                TableRow row_space = new TableRow(this);
                row_space.setPadding(10,10,10,10);
                tableLayout.addView(row_space, 2*3-2);

                tableLayout.addView(row, 2*3-1);

                counter++;

            }
            else{
                TableRow row = new TableRow(this);
                row.setPadding(10,10,10,10);
                row.setBackgroundColor(rgb(255, 255, 255));

                row.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(OCRActivity.this);
                        builder.setCancelable(true);
                        builder.setTitle("Select");
                        builder.setMessage("Add missing products");
                        builder.setPositiveButton("Add",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        addRow();
                                    }
                                });


                        AlertDialog dialog = builder.create();
                        dialog.show();

                        return true;
                    }
                });


                TextView text_a = new TextView(this);
                text_a.setText("Total");
                text_a.setPadding(10, 10, 10, 10);
                text_a.setTextSize(20f);
                text_a.setId(2*3-1);    //id has to be positive. Order: a1=1, b1=2, a2=3, b2=4, ..
                EditText text_b = new EditText(this);
                text_b.setText("");
                text_b.setTextSize(20f);
                text_b.setPadding(10,10,10, 10);
                text_b.setId(2*3); //id has to be positive. Order: a1=1, b1=2, a2=3, b2=4, ..

                LinearLayout ll1 = new LinearLayout(this);
                LinearLayout.LayoutParams params_ll1 = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
                LinearLayout.LayoutParams params_ll1a = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 2f);

                LinearLayout ll11 = new LinearLayout(this);
                LinearLayout ll12 = new LinearLayout(this);

                LinearLayout.LayoutParams param_ll11 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);

                LinearLayout.LayoutParams param_ll12 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);

                ll11.addView(text_a, param_ll11);
                ll12.addView(text_b, param_ll12);

                ll1.addView(ll11, params_ll1);
                ll1.addView(ll12, params_ll1a);

                row.addView(ll1);
                TableRow row_space = new TableRow(this);
                row_space.setPadding(10,10,10,10);
                tableLayout.addView(row_space, 2*3-2);

                tableLayout.addView(row, 2*3-1);
            }

            // k is ID of rows; get all k from 1 to count would be k<count+1;  starting with k=4 -> k<count+1+3
            // if company xor date xor total is inside -> counter = 2 and counter=1 already read -> k<count+1+3-1
            // if two of (company, date, total) are inside -> counter = 3 and counter=1 and counter=2 are already read -> k<count+1+3-2
            // if three of (company, date, total) are inside -> counter = 4 and counter=1, 2, 3 are already read -> k<count+1+3-3

            int alreadyRead = counter;
            count = count + 3 -(alreadyRead -1);
            for (int k = 4; k < (count + 1 + 3-(alreadyRead - 1)); k++) {
                Log.d(TAG, Integer.toString(k));
                Log.d(TAG, "Count: "+ Integer.toString(count));
                final TableRow row = new TableRow(this);
                row.setPadding(10,10,10,10);
                row.setBackgroundColor(rgb(255, 255, 255));

                //Add and Remove Options
                row.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(OCRActivity.this);
                        builder.setCancelable(true);
                        builder.setTitle("Select");
                        builder.setMessage("Add missing products or remove current row");
                        builder.setPositiveButton("Add",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        addRow();
                                    }
                                });
                        // Remove only for product rows
                        builder.setNegativeButton("Remove", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                tableLayout.removeView(row);
                                //Reduce also counter. Only final Variable can be called. S
                                counter--;

                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();

                        return true;
                    }
                });
                //TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
                //row.setLayoutParams(tableParams);
                //row.setPadding(10, 10, 10, 10);
                //row.setBackgroundColor(rgb(0, 255, 255));

                //int receipt_id = Integer.parseInt(json.getJSONArray(Integer.toString(k)).getJSONObject(0).getString("ID"));
                //row.setId(k);
                //a represent first column, v second column
                String a = json.getJSONArray(Integer.toString(counter)).getJSONObject(0).getString("a");
                Log.d(TAG, a);
                String b = json.getJSONArray(Integer.toString(counter)).getJSONObject(1).getString("b");
                Log.d(TAG, b);
                EditText text_a = new EditText(this);
                text_a.setText(a);
                text_a.setPadding(10, 10, 10, 10);
                text_a.setTextSize(20f);
                text_a.setId(2*k-1);    //id has to be positive. Order: a1=1, b1=2, a2=3, b2=4, ..
                //text_date.setBackgroundColor(rgb(255, 0, 0));
                EditText text_b = new EditText(this);
                text_b.setText(b);
                text_b.setTextSize(20f);
                text_b.setPadding(10,10,10, 10);
                text_b.setId(2*k); //id has to be positive. Order: a1=1, b1=2, a2=3, b2=4, ..
                //text_total.setBackgroundColor(rgb(0,0,255));

                //RelativeLayout rl = new RelativeLayout(this);

                //RelativeLayout.LayoutParams params_price = new RelativeLayout.LayoutParams(RelativeLayout.ALIGN_PARENT_RIGHT, TRUE);

                //params_price.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, TRUE);
                //params_price.addRule(RelativeLayout.RIGHT_OF, 1);

                LinearLayout ll1 = new LinearLayout(this);
                //ll1.setBackgroundColor(rgb(0, 255,0));
                /*LinearLayout.LayoutParams param_ll = new LinearLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        3.0f
                );*/
                //rl.setLayoutParams(rowParams);
                //product.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 2f));
                LinearLayout.LayoutParams params_ll1 = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
                LinearLayout.LayoutParams params_ll1a = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 2f);
                //text_date.setLayoutParams(new TableLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT, 2f));

                LinearLayout ll11 = new LinearLayout(this);
                //ll11.setBackgroundColor(rgb(255,255,0));
                LinearLayout ll12 = new LinearLayout(this);
                //ll12.setBackgroundColor(rgb(0,255,255));

                LinearLayout.LayoutParams param_ll11 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);

                LinearLayout.LayoutParams param_ll12 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);

                ll11.addView(text_a, param_ll11);
                ll12.addView(text_b, param_ll12);

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

                TableRow row_space = new TableRow(this);
                row_space.setPadding(10,10,10,10);
                tableLayout.addView(row_space, 2*k-2);

                tableLayout.addView(row, 2*k-1);
                counter++;
                Log.d(TAG, "k: "+ Integer.toString(k));

            }
            //c1x = json.getJSONArray("corner1").getJSONObject(0).getString("x");
            //c1y = json.getJSONArray("corner1").getJSONObject(0).getString("y");

            //size_height = json.getJSONArray("size").getJSONObject(0).getString("height");
            //size_width = json.getJSONArray("size").getJSONObject(0).getString("width");


            Log.d(TAG, "JSON with results read");
            Log.d(TAG, "k: "+ Integer.toString(count));
        } catch (
                JSONException e) {
            Toast.makeText(getApplicationContext(),"Problem during connecting with internet",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void save() {
        Log.d(TAG, "Save results on server");

        String data = collect_results();
        Log.d(TAG, "uploaded Data: " + data);
        uploadData(data);
    }

    private void uploadData(String data) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //Create Connection
        try {
            String ServerUploadPath = "http://192.168.188.67:1337/save";
            url = new URL(ServerUploadPath);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setReadTimeout(60000);
            httpURLConnection.setConnectTimeout(60000);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "text/plain");
            //httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);

            outputStream = httpURLConnection.getOutputStream();
            bufferedWriter = new BufferedWriter(
                    new OutputStreamWriter(outputStream, "UTF-8"));

            bufferedWriter.write(data);

            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();


            //Receive answer from server
            RC = httpURLConnection.getResponseCode();
            if (RC == HttpsURLConnection.HTTP_OK) {
                bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                stringBuilder = new StringBuilder();
                String RC2;
                while ((RC2 = bufferedReader.readLine()) != null) {
                    stringBuilder.append(RC2);
                }
            }
            Log.d(TAG, "Answer save: " + stringBuilder.toString());

            } catch (Exception e) {
            e.printStackTrace();
        }

    }



    private String collect_results() {
        JSONObject obj = new JSONObject();

        try {
            //read edittext views and write the values is json file
            for (int i = 1; i < count + 1; i++) {
                String id = Integer.toString(i + 1);
                EditText etext_a = findViewById(i * 2 - 1);
                EditText etext_b = findViewById(i * 2);
                String text_a = etext_a.getText().toString();
                String text_b = etext_b.getText().toString();

                obj.put(text_a, text_b);
            }
        }
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(OCRActivity.this, "A problem while uploading appeared", Toast.LENGTH_SHORT).show();
        }

        return obj.toString();
    }

    private void addRow(){
        // adding a new line
        count++;
        TableLayout tableLayout = (TableLayout) findViewById(R.id.table);

        TableRow row = new TableRow(this);
        row.setPadding(10,10,10,10);
        row.setBackgroundColor(rgb(255, 255, 255));
        //TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
        //row.setLayoutParams(tableParams);
        //row.setPadding(10, 10, 10, 10);
        //row.setBackgroundColor(rgb(0, 255, 255));

        //int receipt_id = Integer.parseInt(json.getJSONArray(Integer.toString(k)).getJSONObject(0).getString("ID"));
        //row.setId(k);
        //a represent first column, v second column
        EditText text_a = new EditText(this);
        text_a.setText("Product");
        text_a.setPadding(10, 10, 10, 10);
        text_a.setTextSize(20f);
        text_a.setId(2*count-1);    //id has to be positive. Order: a1=1, b1=2, a2=3, b2=4, ..
        //text_date.setBackgroundColor(rgb(255, 0, 0));
        EditText text_b = new EditText(this);
        text_b.setText("Price");
        text_b.setTextSize(20f);
        text_b.setPadding(10,10,10, 10);
        text_b.setId(2*count); //id has to be positive. Order: a1=1, b1=2, a2=3, b2=4, ..
        //text_total.setBackgroundColor(rgb(0,0,255));

        //RelativeLayout rl = new RelativeLayout(this);

        //RelativeLayout.LayoutParams params_price = new RelativeLayout.LayoutParams(RelativeLayout.ALIGN_PARENT_RIGHT, TRUE);

        //params_price.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, TRUE);
        //params_price.addRule(RelativeLayout.RIGHT_OF, 1);

        LinearLayout ll1 = new LinearLayout(this);
        //ll1.setBackgroundColor(rgb(0, 255,0));
                /*LinearLayout.LayoutParams param_ll = new LinearLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        3.0f
                );*/
        //rl.setLayoutParams(rowParams);
        //product.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 2f));
        LinearLayout.LayoutParams params_ll1 = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
        LinearLayout.LayoutParams params_ll1a = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 2f);
        //text_date.setLayoutParams(new TableLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT, 2f));

        LinearLayout ll11 = new LinearLayout(this);
        //ll11.setBackgroundColor(rgb(255,255,0));
        LinearLayout ll12 = new LinearLayout(this);
        //ll12.setBackgroundColor(rgb(0,255,255));

        LinearLayout.LayoutParams param_ll11 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);

        LinearLayout.LayoutParams param_ll12 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);

        ll11.addView(text_a, param_ll11);
        ll12.addView(text_b, param_ll12);

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

        TableRow row_space = new TableRow(this);
        row_space.setPadding(10,10,10,10);
        tableLayout.addView(row_space, 2*count-2);

        tableLayout.addView(row, 2*count-1);
    }
}