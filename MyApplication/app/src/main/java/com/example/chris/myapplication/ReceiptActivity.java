package com.example.chris.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReceiptActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    String TAG = "ReceiptActivity";

    String uri_string;
    String mCurrentPhotoPath;
    int REQUEST_TAKE_PHOTO = 4;
    File file1;

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
            Fragment_receipt fragment_receipt = new Fragment_receipt();
            fragment_receipt.setArguments(bundle);

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment_receipt).commit();

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
                AlertDialog.Builder builder = new AlertDialog.Builder(ReceiptActivity.this);
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

                changeActivity = new Intent(ReceiptActivity.this, CompanyActivity.class);

                changeActivity.putExtra("CalledFrom", "0");
                Log.d(TAG, "start CompanyActivity");
                startActivity(changeActivity);
                break;


            case R.id.RECEIPT:
                changeActivity = new Intent(ReceiptActivity.this, ReceiptActivity.class);

                changeActivity.putExtra("CalledFrom", "0");
                Log.d(TAG, "start ReceiptActivity");
                startActivity(changeActivity);
                break;

            case R.id.PRODUCT:
                changeActivity = new Intent(ReceiptActivity.this, ProductActivity.class);

                changeActivity.putExtra("CalledFrom", "0");
                Log.d(TAG, "start ProductActivity");
                startActivity(changeActivity);
                break;

            case R.id.OFFER:
                changeActivity = new Intent(ReceiptActivity.this, OfferActivity.class);

                changeActivity.putExtra("CalledFrom", "0");
                Log.d(TAG, "start OfferActivity");
                startActivity(changeActivity);
                break;

            case R.id.TREND:
                changeActivity = new Intent(ReceiptActivity.this, TrendActivity.class);

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


}
