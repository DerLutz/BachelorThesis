package com.example.chris.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

//compare the detected points with the real corners and correct them when necessary
public class CornerActivity extends AppCompatActivity {

    private ViewGroup mainLayout;
    private ImageView image;

    Uri picUri;

    private int xDelta;
    private int yDelta;
    int id;
    int bitmap_height, bitmap_width;
    ImageView imageView1;
    ImageView imageView2;
    ImageView imageView3;
    ImageView imageView4;
    String TAG = "CornerActivity";
    Bitmap bitmap;
    float ratio_height;
    float ratio_width;
    int mActionBarSize;
    float imageHeight;
    float imageWidth;
    String c1x, c1y, c2x, c2y, c3x, c3y, c4x, c4y, size_height, size_width;
    float newc1x, newc1y, newc2x, newc2y, newc3x, newc3y, newc4x, newc4y;
    public static String X0 ="X0", X1 ="X1", X2 ="X2", X3 ="X3", URI="URI", Y0="Y0", Y1="Y1", Y2="Y2", Y3="Y3", URI_FILE="URI_FILE", IMG_HEIGHT="IMG_HEIGHT", IMG_WIDTH="IMG_WIDTH", O="O";

    String uri, orientation;
    int height, width;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_corner);

        Log.d(TAG, "Corner Activity started");

        toolbar = findViewById(R.id.toolbar);

        /*Toolbar toolbar = new Toolbar(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 168);
        toolbar.setLayoutParams(layoutParams);
        toolbar.setPopupTheme(R.style.AppTheme);
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        toolbar.setVisibility(View.VISIBLE);

        // Assuming in activity_main, you are using LinearLayout as root
        RelativeLayout relativLayout = findViewById(R.id.cornerActivity);
        relativLayout.addView(toolbar, 0);*/

        setSupportActionBar(toolbar);




        Intent intent = getIntent();

        uri = intent.getStringExtra(MainActivity.URI_FILE);
        Log.d(TAG, uri);

        try {


            c1x = intent.getStringExtra(MainActivity.X1);

            c1y = intent.getStringExtra(MainActivity.Y1);
            c2x = intent.getStringExtra(MainActivity.X2);
            c2y = intent.getStringExtra(MainActivity.Y2);
            c3x = intent.getStringExtra(MainActivity.X3);
            c3y = intent.getStringExtra(MainActivity.Y3);
            c4x = intent.getStringExtra(MainActivity.X4);
            c4y = intent.getStringExtra(MainActivity.Y4);

            size_height = intent.getStringExtra(MainActivity.SH);
            size_width = intent.getStringExtra(MainActivity.SW);

            orientation= intent.getStringExtra(MainActivity.O);
            Log.d(TAG, "Orientation: "+orientation);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        final TypedArray styledAttributes = this.getTheme().obtainStyledAttributes(
                new int[] { android.R.attr.actionBarSize });
        mActionBarSize = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        Log.d(TAG, "Toolbar: "+mActionBarSize);
        Log.d(TAG, "SizeHeight: "+size_height);
        image = (ImageView) findViewById(R.id.image);
        //image.setImageBitmap(bitmap);

        ViewTreeObserver vto = image.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                image.getViewTreeObserver().removeOnPreDrawListener(this);
                imageHeight = image.getMeasuredHeight();
                imageWidth = image.getMeasuredWidth();
                Log.d(TAG, "Height: " + imageHeight + " Width: " + imageWidth);

                return true;
            }
        });

                if (savedInstanceState == null){
            Bundle bundle=new Bundle();
            bundle.putString("uri", uri);
            bundle.putString("c1x", c1x);
            bundle.putString("c1y", c1y);
            bundle.putString("c2x", c2x);
            bundle.putString("c2y", c2y);
            bundle.putString("c3x", c3x);
            bundle.putString("c3y", c3y);
            bundle.putString("c4x", c4x);
            bundle.putString("c4y", c4y);

            bundle.putString("size_height", size_height);
            bundle.putString("size_width", size_width);
            Fragment_corner fragment_corner = new Fragment_corner();
            fragment_corner.setArguments(bundle);
            //set Fragmentclass Arguments
            //Fragment_setting fragobj = new Fragment_setting();
            //fragobj.setArguments(bundle);

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment_corner).commit();

//            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Fragment_setting()).commit();
//            navigationView.setCheckedItem(R.id.setting_frag);
        }

    }


    //ActionBar Button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Log.d(TAG, "In onOptionsItemSelected");
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_name) {
            Toast.makeText(this, "Crop started", Toast.LENGTH_SHORT).show();
            imageView1 = findViewById(R.id.View1Id);
            int[] locations = new int[2];
            imageView1.getLocationOnScreen(locations);

            float x0 = (Fragment_corner.x1()+20)*(Float.parseFloat(size_width)/imageWidth);       // Undo calculation of fragment_corner
            float y0 = (Fragment_corner.y1()+20)*(Float.parseFloat(size_height)/(imageHeight+mActionBarSize));;
            Log.d(TAG, "gelesener X Screen wert: "+ locations[0]);
            Log.d(TAG, "gelesener y Screen wert: "+ locations[1]);

            //Log.d(TAG, Fragment_corner.test());

            imageView2 = findViewById(R.id.View2Id);
            imageView2.getLocationOnScreen(locations);
            float x1 = (Fragment_corner.x2()+20)*(Float.parseFloat(size_width)/imageWidth);       // Undo calculation of fragment_corner
            float y1 = (Fragment_corner.y2()+20)*(Float.parseFloat(size_height)/(mActionBarSize+imageHeight));
            Log.d(TAG, "gelesener X Screen wert: "+ x1);
            Log.d(TAG, "gelesener y Screen wert: "+ y1);


            imageView3 = findViewById(R.id.View3Id);
            imageView3.getLocationOnScreen(locations);
            float x2 = (Fragment_corner.x3()+20)*(Float.parseFloat(size_width)/imageWidth);       // Undo calculation of fragment_corner
            float y2 = (Fragment_corner.y3()+20)*(Float.parseFloat(size_height)/(imageHeight+mActionBarSize));
            Log.d(TAG, "gelesener X Screen wert: "+ y2);
            Log.d(TAG, "gelesener y Screen wert: "+ y2);



            imageView4 = findViewById(R.id.View4Id);
            imageView4.getLocationOnScreen(locations);
            float x3 = (Fragment_corner.x4()+20)*(Float.parseFloat(size_width)/imageWidth);       // Undo calculation of fragment_corner
            float y3 = (Fragment_corner.y4()+20)*(Float.parseFloat(size_height)/(imageHeight+mActionBarSize));
            Log.d(TAG, "gelesener X Screen wert: "+ x3);
            Log.d(TAG, "gelesener y Screen wert: "+ x3);


            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

            Log.d(TAG, "crop started");


            //TODO richtige Paramter übergeben
            Intent startOCRActivity = new Intent(this, OCRActivity.class);
            startOCRActivity.putExtra(URI_FILE, uri);

            float ratio = Float.parseFloat(size_height)/imageHeight;
            Log.d(TAG, "imageHeight: "+imageHeight);
            Log.d(TAG, "ratio: "+ ratio);
            Log.d(TAG, "x0: "+x0);
            Log.d(TAG, "y0: "+y0);
            Log.d(TAG, "x1: "+x1);
            Log.d(TAG, "y1: "+y1);
            Log.d(TAG, "x2: "+x2);
            Log.d(TAG, "y2: "+y2);
            Log.d(TAG, "x3: "+x3);
            Log.d(TAG, "y3: "+y3);

            startOCRActivity.putExtra(X0, Float.toString(x0));
            startOCRActivity.putExtra(Y0, Float.toString(y0));
            startOCRActivity.putExtra(X1, Float.toString(x1));
            startOCRActivity.putExtra(Y1, Float.toString(y1));
            startOCRActivity.putExtra(X2, Float.toString(x2));
            startOCRActivity.putExtra(Y2, Float.toString(y2));
            startOCRActivity.putExtra(X3, Float.toString(x3));
            startOCRActivity.putExtra(Y3, Float.toString(y3));
            startOCRActivity.putExtra(IMG_HEIGHT, size_height);
            startOCRActivity.putExtra(IMG_WIDTH, size_width);

            Log.d(TAG, "Start OCR Activity");
            startActivity(startOCRActivity);
        }

        if (id==R.id.back){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

}

