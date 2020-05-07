package com.example.chris.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

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

public class ProductActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    String TAG = "ProductActivity";

    String uri_string;
    String mCurrentPhotoPath;
    int REQUEST_TAKE_PHOTO = 4;
    File file1;
    String file;
    int RC, orientation;
    ProgressDialog progressDialog ;
    ByteArrayOutputStream byteArrayOutputStream ;
    byte[] byteArray ;
    String ConvertImage ;
    Uri uri;
    Bitmap FixBitmap;
    public static String URI_FILE="URI_FILE", X1="X1",X2="X2", X3="X3", X4="X4", Y1="Y1", Y2="Y2", Y3="Y3", Y4="Y4", SH="SH", SW="SW", O="O";
    String c1x, c1y, c2x, c2y, c3x, c3y, c4x, c4y, size_height, size_width;

    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 123;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        String callingActivity = getIntent().getStringExtra("CalledFrom");

        if (savedInstanceState == null) {

            Bundle bundle = new Bundle();
            bundle.putString("CalledFrom", callingActivity);
            Fragment_product fragment_product = new Fragment_product();
            fragment_product.setArguments(bundle);

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment_product).commit();

//            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Fragment_setting()).commit();
//            navigationView.setCheckedItem(R.id.setting_frag);
        }


    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        Intent changeActivity;
        Log.d(TAG, "In onOptionsItemSelected");

        switch (menuItem.getItemId()) {
            case R.id.ADD:
                Toast.makeText(getApplicationContext(), "Add Clicked", Toast.LENGTH_LONG).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(ProductActivity.this);
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
                /*Toast.makeText(getApplicationContext(),"Company Clicked",Toast.LENGTH_LONG).show();
                Log.d(TAG, "Start getData in Company");
                name1.setText("Name");
                name2.setText("Total");
                updateView("Company", "0");*/

                changeActivity = new Intent(ProductActivity.this, CompanyActivity.class);

                changeActivity.putExtra("CalledFrom", "0");
                Log.d(TAG, "start CompanyActivity");
                startActivity(changeActivity);
                break;


            case R.id.RECEIPT:
                changeActivity = new Intent(ProductActivity.this, ReceiptActivity.class);

                changeActivity.putExtra("CalledFrom", "0");
                Log.d(TAG, "start ReceiptActivity");
                startActivity(changeActivity);
                break;

            case R.id.PRODUCT:
                changeActivity = new Intent(ProductActivity.this, ProductActivity.class);

                changeActivity.putExtra("CalledFrom", "0");
                Log.d(TAG, "start ProductActivity");
                startActivity(changeActivity);
                break;

            case R.id.OFFER:
                changeActivity = new Intent(ProductActivity.this, OfferActivity.class);

                changeActivity.putExtra("CalledFrom", "0");
                Log.d(TAG, "start OfferActivity");
                startActivity(changeActivity);
                break;

            case R.id.TREND:
                changeActivity = new Intent(ProductActivity.this, TrendActivity.class);

                changeActivity.putExtra("CalledFrom", "0");
                Log.d(TAG, "start TrendActivity");
                startActivity(changeActivity);
                break;

        }

        drawerLayout.closeDrawer(GravityCompat.START);

        return false;
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

        ImageHttpRequest("http://192.168.188.54:1337/cornerDetection", ConvertImage);

    }


    public void ImageHttpRequest(String requestURL,String PData) {
        StringBuilder stringBuilder = new StringBuilder();

        HttpURLConnection httpURLConnection ;
        URL url;
        OutputStream outputStream;
        BufferedWriter bufferedWriter ;
        BufferedReader bufferedReader ;

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


        if (checkPermissionWRITE_EXTERNAL_STORAGE(ProductActivity.this)) {

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            FixBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            file = MediaStore.Images.Media.insertImage(ProductActivity.this.getContentResolver(), FixBitmap, "Title", null);
        }

        Intent changeActivity = new Intent(ProductActivity.this, CornerActivity.class);


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

        //return stringBuilder.toString();
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
