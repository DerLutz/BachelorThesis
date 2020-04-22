package com.example.chris.myapplication;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Bundle;

import android.os.StrictMode;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.*;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
//import android.support.v7.widget.RecyclerView


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;


import javax.net.ssl.HttpsURLConnection;

import static android.graphics.Color.rgb;

//Select an image or make a photo, upload it to the server to detect the corner and receive the results

public class MainActivity extends AppCompatActivity
        implements CategoryRecView.ListItemClickListener {

    //Todo passe Anzahl an Categorien an
    private static final int NUM_LIST_ITEMS = 100;
    static String TAG = "MainActivity";
    //For calling next side
    static String TOTAL = "TOTAL", NAME="NAME";
    StringBuilder stringBuilder;


    Button GetImageFromGalleryButton, UploadImageOnServerButton, GetImageFromCameraButton, SelectButton;
    ImageView ShowSelectedImage;
    Bitmap FixBitmap;
    String ServerUploadPath ="http://192.168.188.54:1337/cornerDetection";
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
    String file;
    Uri uri;
    String uri_string;
    String mCurrentPhotoPath;
    int REQUEST_TAKE_PHOTO = 4;
    File file1;
    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 123;

    private CategoryRecView mAdapter;
    private RecyclerView mNumbersList;

    //For the call of Activity 2
    public static String URI_FILE="URI_FILE", X1="X1",X2="X2", X3="X3", X4="X4", Y1="Y1", Y2="Y2", Y3="Y3", Y4="Y4", SH="SH", SW="SW", O="O";
    String c1x, c1y, c2x, c2y, c3x, c3y, c4x, c4y, size_height, size_width;


    //OpenCV has to be loaded at the beginning
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i("OpenCV", "OpenCV loaded successfully");
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d("OpenCV", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d("OpenCV", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView name = (TextView) findViewById(R.id.name);
        name.setText("Company");

        Log.d(TAG, "Start getData in onCreate");
        getData("Category", "0");


        Log.d(TAG, "End of onCreate");

    }


    // COMPLETED (10) Override ListItemClickListener's onListItemClick method
    /**
     * This is where we receive our callback from
     * {@link com.example.chris.myapplication.CategoryRecView.ListItemClickListener}
     *
     * This callback is invoked when you click on an item in the list.
     *
     * @param clickedItemIndex Index in the list of the item that was clicked.
     */
    @Override
    public void onListItemClick(int clickedItemIndex) {
        StringBuilder response = connectServerData();
        //Todo get summe of receipts of category
        Intent changeActivity = new Intent(MainActivity.this, CategoryInfo.class);
        changeActivity.putExtra(TOTAL, Categories.getCategoryTotal(response, clickedItemIndex));
        changeActivity.putExtra(NAME, Categories.getCategoryName(response, clickedItemIndex));

        Log.d(TAG, "start ReceiptRecView");
        startActivity(changeActivity);

    }

    @Override
    protected void onActivityResult(int RC, int RQC, Intent I) {

        super.onActivityResult(RC, RQC, I);

        //onActivityResult for chose image from gallery
        if (RC == 1 && RQC == RESULT_OK && I != null && I.getData() != null) {
            uri = I.getData();

            try {
                FixBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                Log.d("Upload","image chosen");
                file = uri.toString();
                UploadImageToServer();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(RC == REQUEST_TAKE_PHOTO &&RQC == RESULT_OK) {
            try {
                uri = Uri.parse(uri_string);
                FixBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                /*
                for some phones the "normal" camera perspective can be different like landscape instead of portrait.
                Thus the photo has to rotated
                */

                Log.d(TAG, "File: "+uri.getPath());
                ExifInterface ei = new ExifInterface(file1.toString());
                orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);

                Bitmap rotatedBitmap = null;

                Log.d(TAG, "Orientation: "+orientation);
                switch(orientation) {

                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotatedBitmap = rotateImage(FixBitmap, 90);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotatedBitmap = rotateImage(FixBitmap, 180);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotatedBitmap = rotateImage(FixBitmap, 270);
                        break;

                    case ExifInterface.ORIENTATION_NORMAL:
                    default:
                        rotatedBitmap = FixBitmap;
                }

                FixBitmap = rotatedBitmap;

                UploadImageToServer();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public void UploadImageToServer(){

        Log.d(TAG, "FixBitmap: "+ FixBitmap.toString());
        Log.d(TAG, Integer.toString(FixBitmap.getByteCount()));
        byteArrayOutputStream = new ByteArrayOutputStream();
        // Prepare image for upload
        FixBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byteArray = byteArrayOutputStream.toByteArray();
        ConvertImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

        // Class for upload
        class AsyncTaskUploadClass extends AsyncTask<Void,Void,String> {

            @Override
            protected void onPreExecute() {

                super.onPreExecute();
                progressDialog = ProgressDialog.show(MainActivity.this,"Image is Uploading","Please Wait",false,false);
            }

            @Override
            protected void onPostExecute(String string1) {
                super.onPostExecute(string1);
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this,string1,Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(Void... params) {


                ImageProcessClass imageProcessClass = new ImageProcessClass();

                /* If some extra information should be add to upload stream. Not used at the moment

                HashMap<String,String> HashMapParams = new HashMap<String,String>();
                HashMapParams.put(ImageTag, GetImageNameFromEditText+".jpg");
                HashMapParams.put(ImageName, ConvertImage);
                Log.d("Upload", "ConvertImage: "+ConvertImage);
                Log.d("Upload", "ImageName: "+ImageName);
                Log.d("Upload", "ImageTag: "+ImageTag);
                Log.d("Upload", "EditText: "+GetImageNameFromEditText); */

                // Alternative for HashMap
                //ContentValues data= new ContentValues();

                //data.put("ImageName",ImageName);
                //data.put("ImageTag", GetImageNameFromEditText);
                //datas.put("File", file+".jpg");
                Log.d(TAG, "set data");

                //Data for upload
                String FinalData = imageProcessClass.ImageHttpRequest(ServerUploadPath, ConvertImage);
                return FinalData;
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();
        AsyncTaskUploadClassOBJ.execute();

    }


    public class ImageProcessClass{

        public String ImageHttpRequest(String requestURL,String PData) {
            StringBuilder stringBuilder = new StringBuilder();
            try {


                //Create Connection
                url = new URL(requestURL);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(30000);
                httpURLConnection.setConnectTimeout(30000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "image/jpeg");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);

                outputStream = httpURLConnection.getOutputStream();
                bufferedWriter = new BufferedWriter(
                        new OutputStreamWriter(outputStream, "UTF-8"));
                bufferedWriter.write((PData));

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
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d(TAG, "Answer corner detection: " + stringBuilder.toString());

            //Read JSON
            try {
                JSONObject json = new JSONObject(stringBuilder.toString());

                c1x = json.getJSONArray("corner1").getJSONObject(0).getString("x");
                c1y = json.getJSONArray("corner1").getJSONObject(0).getString("y");

                c2x = json.getJSONArray("corner2").getJSONObject(0).getString("x");
                c2y = json.getJSONArray("corner2").getJSONObject(0).getString("y");

                c3x = json.getJSONArray("corner3").getJSONObject(0).getString("x");
                c3y = json.getJSONArray("corner3").getJSONObject(0).getString("y");

                c4x = json.getJSONArray("corner4").getJSONObject(0).getString("x");
                c4y = json.getJSONArray("corner4").getJSONObject(0).getString("y");

                size_height = json.getJSONArray("size").getJSONObject(0).getString("height");
                size_width = json.getJSONArray("size").getJSONObject(0).getString("width");


                Log.d(TAG, "JSON read");
            }
            catch (JSONException e) {
                e.printStackTrace();
                //Dummies (WiFi not running)

                //Top left
                c1x = "51";
                c1y = "51";

                //Bottom left
                c2x = "123";
                c2y = "899";

                //Bottom right
                c3x = "709";
                c3y = "791";

                //Top right
                c4x = "666";
                c4y = "309";

                size_height = "2837";
                size_width = "1234";

            }


            if (checkPermissionWRITE_EXTERNAL_STORAGE(MainActivity.this)) {

                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                FixBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                file = MediaStore.Images.Media.insertImage(MainActivity.this.getContentResolver(), FixBitmap, "Title", null);
            }

            Intent changeActivity = new Intent(MainActivity.this, CornerActivity.class);


            changeActivity.putExtra("URI_FILE", file);
            changeActivity.putExtra(X1, c1x);
            changeActivity.putExtra("X2", c2x);
            changeActivity.putExtra(X3, c3x);
            changeActivity.putExtra(X4, c4x);
            changeActivity.putExtra(Y1, c1y);
            changeActivity.putExtra(Y2, c2y);
            changeActivity.putExtra(Y3, c3y);
            changeActivity.putExtra(Y4, c4y);
            changeActivity.putExtra(O, Integer.toString(orientation));

            changeActivity.putExtra(SH, size_height);
            changeActivity.putExtra(SW, size_width);
            //changeActivity.putExtra("URI", uri);
            Log.d(TAG, uri.toString());
            Log.d(TAG, "start CornerActivity");
            startActivity(changeActivity);

            return stringBuilder.toString();
        }

    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();



            } catch (IOException ex) {
                // Error occurred while creating the File
                Toast toast = Toast.makeText(this, "Error occurred while creating the File", Toast.LENGTH_SHORT);
                toast.show();

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {

                file1=photoFile;
                Uri photoURI = FileProvider.getUriForFile(this,"com.example.android.fileprovider", photoFile);
                uri_string = photoURI.toString();
                Log.e(TAG, "uri:"+ uri_string);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    // Methode for saving the picture in a file
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    public boolean checkPermissionWRITE_EXTERNAL_STORAGE(
            final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    showDialog("External storage", context,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
                                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }

    public void showDialog(final String msg, final Context context,
                           final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[] { permission },
                                MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    public StringBuilder connectServerData (){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //Create Connection
        try{
            String ServerUploadPath ="http://192.168.188.67:1337/getData";
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
            String send = "Category";
            send = send + "\n" + "1";
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
                while ((RC2 = bufferedReader.readLine()) != null){
                    stringBuilder.append(RC2);
                }
            }
            Log.d(TAG, "Answer data category connect to server: " + stringBuilder.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    return stringBuilder;
    }

    private void getData(String data, String extra){

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //Create Connection
        try{
            String ServerUploadPath ="http://192.168.188.67:5000/getData";
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
            String send = data;
            send = send + "\n" + extra;
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
                while ((RC2 = bufferedReader.readLine()) != null){
                    stringBuilder.append(RC2);
                }
            }
            Log.d(TAG, "Answer data server: " + stringBuilder.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        switch (data){
            case "Receipt":
                Log.d(TAG, "Start receipt_visualization");
                receipt_visualization(stringBuilder);
                break;

            case "Category":
                Log.d(TAG, "Start category_visualization");
                category_visualization(stringBuilder);
                break;

            case "Products":
                Log.d(TAG, "Start product_visualization");
                product_visualization(stringBuilder);
                break;

            case "Offers":
                Log.d(TAG, "Start offer_visualization");
                offers_visualization(stringBuilder);
                break;

            default:
                Log.d(TAG, "Something went wrong in the Switch cases before interpreting JSON");
        }

        Log.d(TAG, "Finished getData");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_main_menu,menu);
        Log.d(TAG, "In onCreateOptionsMenu");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.d(TAG, "In onOptionsItemSelected");
        TextView name = (TextView) findViewById(R.id.name);
        LinearLayout ll_h = findViewById(R.id.linLayout_hor);
        TextView name1 = new TextView(this);
        TextView name2 = new TextView(this);
        LinearLayout.LayoutParams param_left = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);

        LinearLayout.LayoutParams param_right = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 2f);


        switch (item.getItemId()) {
            case R.id.ADD:
                Toast.makeText(getApplicationContext(),"Add Clicked",Toast.LENGTH_LONG).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setCancelable(true);
                builder.setTitle("Select");
                builder.setMessage("Camera or Gallaery");
                builder.setPositiveButton("Camera",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dispatchTakePictureIntent();
                            }
                        });
                builder.setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        startActivityForResult(Intent.createChooser(intent, "Select Image From Gallery"), 1);

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();


                return true;

            case R.id.COMPANY:
                Toast.makeText(getApplicationContext(),"Company Clicked",Toast.LENGTH_LONG).show();
                Log.d(TAG, "Start getData in Company");
                updateView("Company", "0");
                return true;

            case R.id.RECEIPT:
                Toast.makeText(getApplicationContext(),"RECEIPT Clicked",Toast.LENGTH_LONG).show();
                Log.d(TAG, "Start getData in RECEIPT");
                updateView("Receipt", "0");
                return true;

            case R.id.PRODUCT:
                Toast.makeText(getApplicationContext(),"Product Clicked",Toast.LENGTH_LONG).show();
                Log.d(TAG, "Start getData in product");
                updateView("Product", "0");
                return true;

            case R.id.OFFER:
                updateView("Offer", "0");
                return true;

            default:

                super.onOptionsItemSelected(item);

        }
        return true;

    }

    private void receipt_visualization(StringBuilder stringBuilder){
        Log.d(TAG, "In receipt_visualization");
        //Read JSON
        TableLayout tableLayout = (TableLayout) findViewById(R.id.table);
        //tableLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        //delete old table if exists
        while (true){
            Log.d(TAG, "Receipt_Vis, Look for old views");
            if (tableLayout.getChildAt(0) != null){
                Log.d(TAG, "Kill old views");
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
            JSONObject json = new JSONObject(stringBuilder.toString());
            int count = Integer.parseInt(json.getJSONArray("count").getJSONObject(0).getString("count"));
            for (int k = 1; k < count + 1; k++) {
                TableRow row = new TableRow(this);
                row.setPadding(10,10,10,10);
                //TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
                //row.setLayoutParams(tableParams);
                //row.setPadding(10, 10, 10, 10);
                row.setBackgroundColor(rgb(255, 255, 255));

                final String receipt_id = json.getJSONArray(Integer.toString(k)).getJSONObject(0).getString("ID");
                //row.setId(receipt_id);
                String receipt_date = json.getJSONArray(Integer.toString(k)).getJSONObject(0).getString("date");
                String receipt_price = json.getJSONArray(Integer.toString(k)).getJSONObject(0).getString("total");
                TextView text_date = new TextView(this);
                text_date.setText(receipt_date);
                text_date.setPadding(10, 10, 10, 10);
                text_date.setTextSize(30f);
                text_date.setTextColor(getResources().getColor(R.color.white));
                text_date.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Log.d(TAG, "ClickListener calls updateView(Product, receipt_id");
                        updateView("Product", receipt_id);
                    }
                });
                //text_date.setTextColor(rgb(0,0,0));
                //text_date.setBackgroundColor(rgb(255, 0, 0));
                TextView text_total = new TextView(this);
                text_total.setText(receipt_price);
                text_total.setTextSize(30f);
                text_total.setTextColor(getResources().getColor(R.color.white));
                text_total.setPadding(10,10,10, 10);
                //text_total.setTextColor(rgb(0,0,0));
                //text_total.setBackgroundColor(rgb(0,0,255));

                Log.d(TAG, receipt_date);
                Log.d(TAG, receipt_price);

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
                LinearLayout.LayoutParams params_ll1 = new TableLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT, 1f);
                LinearLayout.LayoutParams params_ll1a = new TableLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT, 2f);
                //text_date.setLayoutParams(new TableLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT, 2f));

                LinearLayout ll11 = new LinearLayout(this);
                ll11.setBackgroundColor(getResources().getColor(R.color.black));
                LinearLayout ll12 = new LinearLayout(this);
                ll12.setBackgroundColor(getResources().getColor(R.color.black));
                LinearLayout.LayoutParams param_ll11 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);

                LinearLayout.LayoutParams param_ll12 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);

                ll11.addView(text_date, param_ll11);
                ll12.addView(text_total, param_ll12);

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
                row_space.setPadding(0,2,0,2);
                tableLayout.addView(row_space, 2*k-2);

                tableLayout.addView(row, 2* k-1);


            }
            //c1x = json.getJSONArray("corner1").getJSONObject(0).getString("x");
            //c1y = json.getJSONArray("corner1").getJSONObject(0).getString("y");

            //size_height = json.getJSONArray("size").getJSONObject(0).getString("height");
            //size_width = json.getJSONArray("size").getJSONObject(0).getString("width");


            Log.d("server", "JSON read");
        } catch (
                JSONException e) {
            Toast.makeText(getApplicationContext(),"Problem during connecting with internet",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void category_visualization(StringBuilder stringBuilder){
        Log.d(TAG, "In category_visualization");
        //Read JSON
        TableLayout tableLayout = (TableLayout) findViewById(R.id.table);
        //tableLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        while (true){
            Log.d(TAG, "Category_vis, look for old views");
            if (tableLayout.getChildAt(0) != null){
                tableLayout.removeAllViews();
                Log.d(TAG, "Category_vis, remove old views");
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
            JSONObject json = new JSONObject(stringBuilder.toString());
            int count = Integer.parseInt(json.getJSONArray("count").getJSONObject(0).getString("count"));
            for (int k = 1; k < count + 1; k++) {
                TableRow row = new TableRow(this);
                row.setTag("");
                row.setPadding(10,10,10,10);
                //TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
                //row.setLayoutParams(tableParams);
                //row.setPadding(10, 10, 10, 10);
                row.setBackgroundColor(rgb(255, 255, 255));

                //int receipt_id = Integer.parseInt(json.getJSONArray(Integer.toString(k)).getJSONObject(0).getString("ID"));
                //row.setId(receipt_id);
                final String company_name = json.getJSONArray(Integer.toString(k)).getJSONObject(0).getString("name");
                String company_total = json.getJSONArray(Integer.toString(k)).getJSONObject(0).getString("total");
                TextView text_name = new TextView(this);
                text_name.setText(company_name);
                text_name.setPadding(10, 10, 10, 10);
                text_name.setTextSize(30f);
                text_name.setTextColor(getResources().getColor(R.color.white));
                //text_name.setTextColor(rgb(0,0,0));
                row.setTag(company_name);
                text_name.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Log.d(TAG, "ClickListener calls updateView(Receipt, company_name");
                        updateView("Receipt", company_name);
                    }
                });
                //text_date.setBackgroundColor(rgb(255, 0, 0));
                TextView text_total = new TextView(this);
                text_total.setText(company_total);
                text_total.setTextSize(30f);
                text_total.setPadding(10,10,10, 10);
                text_total.setTextColor(getResources().getColor(R.color.white));
                //text_total.setTextColor(rgb(0,0,0));
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
                LinearLayout.LayoutParams params_ll1 = new TableLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT, 1f);
                LinearLayout.LayoutParams params_ll1a = new TableLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT, 2f);
                //text_date.setLayoutParams(new TableLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT, 2f));

                LinearLayout ll11 = new LinearLayout(this);
                ll11.setBackgroundColor(getResources().getColor(R.color.black));
                LinearLayout ll12 = new LinearLayout(this);
                ll12.setBackgroundColor(getResources().getColor(R.color.black));
                LinearLayout.LayoutParams param_ll11 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);

                LinearLayout.LayoutParams param_ll12 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);

                ll11.addView(text_name, param_ll11);
                ll12.addView(text_total, param_ll12);

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
                row_space.setPadding(0,2,0,2);
                tableLayout.addView(row_space, 2*k-2);

                tableLayout.addView(row, 2*k-1);

            }
            //c1x = json.getJSONArray("corner1").getJSONObject(0).getString("x");
            //c1y = json.getJSONArray("corner1").getJSONObject(0).getString("y");

            //size_height = json.getJSONArray("size").getJSONObject(0).getString("height");
            //size_width = json.getJSONArray("size").getJSONObject(0).getString("width");


            Log.d("server", "JSON read");
        } catch (
                JSONException e) {
            Toast.makeText(getApplicationContext(),"Problem during connecting with internet",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }


    private void product_visualization(StringBuilder stringBuilder){
        Log.d(TAG, "In product_visualization");
        //Read JSON
        TableLayout tableLayout = (TableLayout) findViewById(R.id.table);
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
            JSONObject json = new JSONObject(stringBuilder.toString());
            int count = Integer.parseInt(json.getJSONArray("count").getJSONObject(0).getString("count"));
            for (int k = 1; k < count + 1; k++) {
                TableRow row = new TableRow(this);
                row.setPadding(10,10,10,10);
                //TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
                //row.setLayoutParams(tableParams);
                //row.setPadding(10, 10, 10, 10);
                row.setBackgroundColor(rgb(255, 255, 255));

                //int receipt_id = Integer.parseInt(json.getJSONArray(Integer.toString(k)).getJSONObject(0).getString("ID"));
                //row.setId(receipt_id);
                String receipt_name = json.getJSONArray(Integer.toString(k)).getJSONObject(0).getString("name");
                String receipt_price = json.getJSONArray(Integer.toString(k)).getJSONObject(0).getString("price");
                TextView text_name = new TextView(this);
                text_name.setText(receipt_name);
                text_name.setPadding(10, 10, 10, 10);
                text_name.setTextSize(30f);
                text_name.setTextColor(getResources().getColor(R.color.white));
                //text_date.setBackgroundColor(rgb(255, 0, 0));
                TextView text_price = new TextView(this);
                text_price.setText(receipt_price);
                text_price.setTextSize(30f);
                text_price.setTextColor(getResources().getColor(R.color.white));
                text_price.setPadding(10,10,10, 10);
                //text_total.setBackgroundColor(rgb(0,0,255));

                Log.d(TAG, receipt_name);
                Log.d(TAG, receipt_price);

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
                LinearLayout.LayoutParams params_ll1 = new TableLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT, 1f);
                LinearLayout.LayoutParams params_ll1a = new TableLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT, 2f);
                //text_date.setLayoutParams(new TableLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT, 2f));

                LinearLayout ll11 = new LinearLayout(this);
                ll11.setBackgroundColor(getResources().getColor(R.color.black));
                LinearLayout ll12 = new LinearLayout(this);
                ll12.setBackgroundColor(getResources().getColor(R.color.black));
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
                TableRow row_space = new TableRow(this);
                row_space.setPadding(0,2,0,2);
                tableLayout.addView(row_space, 2*k-2);

                tableLayout.addView(row, 2*k-1);

            }
            //c1x = json.getJSONArray("corner1").getJSONObject(0).getString("x");
            //c1y = json.getJSONArray("corner1").getJSONObject(0).getString("y");

            //size_height = json.getJSONArray("size").getJSONObject(0).getString("height");
            //size_width = json.getJSONArray("size").getJSONObject(0).getString("width");


            Log.d("server", "JSON read");
        } catch (
                JSONException e) {
            Toast.makeText(getApplicationContext(),"Problem during connecting with internet",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void offers_visualization(StringBuilder stringBuilder){
        Log.d(TAG, "In offer_visualization");
        //Read JSON
        TableLayout tableLayout = (TableLayout) findViewById(R.id.table);
        //tableLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        while (true){
            if (tableLayout.getChildAt(0) != null){
                tableLayout.removeAllViews();
            }
            else {
                break;
            }
        }
        TableLayout.LayoutParams tableParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);

        //int red = Color.parseColor("#FF0000");
        //ll.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 3f));
        //ll.setBackgroundColor(red);
        //Read JSON
        try {
            JSONObject json = new JSONObject(stringBuilder.toString());
            int count = Integer.parseInt(json.getJSONArray("count").getJSONObject(0).getString("count"));
            for (int k = 1; k < count + 1; k++) {
                TableRow row = new TableRow(this);
                row.setPadding(10,10,10,10);
                //TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
                //row.setLayoutParams(tableParams);
                //row.setPadding(10, 10, 10, 10);
                row.setBackgroundColor(rgb(255, 255, 255));

                //int receipt_id = Integer.parseInt(json.getJSONArray(Integer.toString(k)).getJSONObject(0).getString("ID"));
                //row.setId(receipt_id);
                String offer_name = json.getJSONArray(Integer.toString(k)).getJSONObject(0).getString("product");
                String offer_price = json.getJSONArray(Integer.toString(k)).getJSONObject(0).getString("price");
                String offer_vendor = json.getJSONArray(Integer.toString(k)).getJSONObject(0).getString("company");

                Log.d(TAG, "offer_name: "+ offer_name);
                TextView text_name = new TextView(this);
                text_name.setText(offer_name);
                text_name.setPadding(10, 10, 10, 10);
                text_name.setTextSize(30f);
                text_name.setTextColor(getResources().getColor(R.color.white));
                //text_date.setBackgroundColor(rgb(255, 0, 0));
                TextView text_total = new TextView(this);
                text_total.setText(offer_price);
                text_total.setTextSize(30f);
                text_total.setTextColor(getResources().getColor(R.color.white));
                text_total.setPadding(10,10,10, 10);
                //text_total.setTextColor(rgb(0,0,0));
                //text_total.setBackgroundColor(rgb(0,0,255));

                TextView text_vendor = new TextView(this);
                text_vendor.setText(offer_vendor);
                text_vendor.setPadding(10, 10, 10, 10);
                text_vendor.setTextSize(20f);
                text_vendor.setTextColor(getResources().getColor(R.color.white));
                //text_vendor.setTextColor(rgb(0,0,0));
                //text_vendor.setBackgroundColor(rgb(255,255,255));

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
                LinearLayout.LayoutParams params_ll1 = new TableLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT, 1f);
                LinearLayout.LayoutParams params_ll1a = new TableLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT, 2f);
                //text_date.setLayoutParams(new TableLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT, 2f));

                LinearLayout ll11 = new LinearLayout(this);
                ll11.setBackgroundColor(getResources().getColor(R.color.black));
                LinearLayout ll12 = new LinearLayout(this);
                ll12.setBackgroundColor(getResources().getColor(R.color.black));
                LinearLayout.LayoutParams param_ll11 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);

                LinearLayout.LayoutParams param_ll12 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);

                ll11.addView(text_name, param_ll11);
                ll12.addView(text_total, param_ll12);

                ll1.addView(ll11, params_ll1);
                ll1.addView(ll12, params_ll1a);

                //rl.addView(text_date,params);
                //text_total.setGravity(5);   // 5 means right

                //LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
                //text_total.setBackgroundColor(rgb(0, 0, 255));
                // Add all the rules you need
                //param.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                //rl.addView(text_total, param);

                LinearLayout ll0 = new LinearLayout(this);
                ll0.setOrientation(LinearLayout.VERTICAL);
                ll0.addView(ll1);
                ll0.addView(text_vendor);
                row.addView(ll0);

                TableRow row_space = new TableRow(this);
                row_space.setPadding(0,2,0,2);
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
            Toast.makeText(getApplicationContext(),"Problem during connecting with internet",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void updateView (String category, String extra) {
        Log.d(TAG, "In onOptionsItemSelected");
        TextView name = (TextView) findViewById(R.id.name);
        LinearLayout ll_h = findViewById(R.id.linLayout_hor);
        TextView name1 = new TextView(this);
        TextView name2 = new TextView(this);
        LinearLayout.LayoutParams param_left = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);

        LinearLayout.LayoutParams param_right = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 2f);


        switch (category) {
            case "Company":
                Toast.makeText(getApplicationContext(), "Company Clicked", Toast.LENGTH_LONG).show();
                Log.d(TAG, "Start getData in Company");
                name.setText("Company");

                //delete old column names if exists
                while (true) {
                    if (ll_h.getChildAt(0) != null) {
                        ll_h.removeAllViews();
                    } else {
                        break;
                    }
                }

                //Create new textviews
                name1.setText("Name");
                name1.setTextSize(35f);
                name1.setPadding(10, 10, 10, 10);

                name2.setText("Total");
                name2.setTextSize(35f);
                name2.setPadding(10, 10, 10, 10);

                ll_h.addView(name1, param_left);
                ll_h.addView(name2, param_right);
                getData("Category", extra);
                break;

            case "Receipt":
                Toast.makeText(getApplicationContext(), "RECEIPT Clicked", Toast.LENGTH_LONG).show();
                Log.d(TAG, "Start getData in RECEIPT");
                name.setText("Receipts");
                //delete old column names if exists
                while (true) {
                    if (ll_h.getChildAt(0) != null) {
                        ll_h.removeAllViews();
                    } else {
                        break;
                    }
                }

                //Create new textviews
                if (extra.equals("0")) {
                    name1.setText("Date");
                    name1.setTextSize(35f);
                    name1.setPadding(10, 10, 10, 10);
                }
                else{
                    name1.setText(extra);
                    name1.setTextSize(35f);
                    name1.setPadding(10, 10, 10, 10);
                }
                name2.setText("Total");
                name2.setTextSize(35f);
                name2.setPadding(10, 10, 10, 10);

                ll_h.addView(name1, param_left);
                ll_h.addView(name2, param_right);
                getData("Receipt", extra);
                break;

            case "Product":
                Toast.makeText(getApplicationContext(), "Product Clicked", Toast.LENGTH_LONG).show();
                Log.d(TAG, "Start getData in product");
                name.setText("Products");
                //delete old column names if exists
                while (true) {
                    if (ll_h.getChildAt(0) != null) {
                        ll_h.removeAllViews();
                    } else {
                        break;
                    }
                }

                //Create new textviews
                if (extra.equals("0")) {
                    name1.setText("Name");
                    name1.setTextSize(35f);
                    name1.setPadding(10, 10, 10, 10);
                }
                else{
                    name1.setText(extra);
                    name1.setTextSize(35f);
                    name1.setPadding(10, 10, 10, 10);
                }
                name2.setText("Price");
                name2.setTextSize(35f);
                name2.setPadding(10, 10, 10, 10);

                ll_h.addView(name1, param_left);
                ll_h.addView(name2, param_right);
                getData("Products", extra);
                break;

            case "Offer":
                Log.d(TAG, "Start getData in Offer");
                Toast.makeText(getApplicationContext(), "Offers Clicked", Toast.LENGTH_LONG).show();
                name.setText("Offers");
                //delete old column names if exists
                while (true) {
                    if (ll_h.getChildAt(0) != null) {
                        ll_h.removeAllViews();
                    } else {
                        break;
                    }
                }

                //Create new textviews
                name1.setText("Name");
                name1.setTextSize(35f);
                name1.setPadding(10, 10, 10, 10);

                name2.setText("Price");
                name2.setTextSize(35f);
                name2.setPadding(10, 10, 10, 10);

                ll_h.addView(name1, param_left);
                ll_h.addView(name2, param_right);
                getData("Offers", extra);
                break;
        }
    }
}
