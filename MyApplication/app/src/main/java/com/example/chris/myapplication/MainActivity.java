package com.example.chris.myapplication;
import android.Manifest;
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

import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.*;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
//import android.support.v7.widget.RecyclerView


import androidx.appcompat.app.AlertDialog;
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

//Select an image or make a photo, upload it to the server to detect the corner and receive the results

public class MainActivity extends Activity
        implements CategoryRecView.ListItemClickListener {

    //Todo passe Anzahl an Categorien an
    private static final int NUM_LIST_ITEMS = 100;
    static String TAG = "MainActivity";
    //For calling next side
    static String TOTAL = "TOTAL", NAME="NAME";

    Button GetImageFromGalleryButton, UploadImageOnServerButton, GetImageFromCameraButton, SelectButton;
    ImageView ShowSelectedImage;
    Bitmap FixBitmap;
    String ServerUploadPath ="http://192.168.89.183:1337/cornerDetection";
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

    //TODO bei drehen nicht abstürzen
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Define variables from activity_main.xml
        SelectButton = (Button) findViewById(R.id.buttonS);
        mNumbersList = (RecyclerView) findViewById(R.id.rv_numbers);

        //ShowSelectedImage.setImageResource(R.drawable.example);

        byteArrayOutputStream = new ByteArrayOutputStream();

        SelectButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
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
            }
    });


        /*
         * A LinearLayoutManager is responsible for measuring and positioning item views within a
         * RecyclerView into a linear list. This means that it can produce either a horizontal or
         * vertical list depending on which parameter you pass in to the LinearLayoutManager
         * constructor. By default, if you don't specify an orientation, you get a vertical list.
         * In our case, we want a vertical list, so we don't need to pass in an orientation flag to
         * the LinearLayoutManager constructor.
         *
         * There are other LayoutManagers available to display your data in uniform grids,
         * staggered grids, and more! See the developer documentation for more details.
         */
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mNumbersList.setLayoutManager(layoutManager);

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mNumbersList.setHasFixedSize(true);

        // COMPLETED (13) Pass in this as the ListItemClickListener to the CategoryRecView constructor
        /*
         * The CategoryRecView is responsible for displaying each item in the list.
         */
        mAdapter = new CategoryRecView(NUM_LIST_ITEMS, this);
        mNumbersList.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
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
            case R.id.action_refresh:
                // COMPLETED (14) Pass in this as the ListItemClickListener to the CategoryRecView constructor
                mAdapter = new CategoryRecView(NUM_LIST_ITEMS, this);
                mNumbersList.setAdapter(mAdapter);
                return true;
        }

        return super.onOptionsItemSelected(item);
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

        //Todo get summe of receipts of category
        Intent changeActivity = new Intent(MainActivity.this, CategoryInfo.class);
        changeActivity.putExtra(TOTAL, Integer.toString(clickedItemIndex));
        changeActivity.putExtra(NAME, Categories.getCategory(clickedItemIndex));

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


}
