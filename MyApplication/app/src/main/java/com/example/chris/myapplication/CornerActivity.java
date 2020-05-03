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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
    int height_bar;
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

        mark_corner();
    }

    private void mark_corner(){

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

        Log.d(TAG, "read Values");

        Log.d(TAG, uri);
        picUri = Uri.parse(uri);
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), picUri);

            bitmap_height = bitmap.getHeight();
            bitmap_width = bitmap.getWidth();

            Matrix matrix = new Matrix();

            //Necessary?
            matrix.postRotate(90);
            // Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

            ((ImageView)findViewById(R.id.image)).setImageBitmap(bitmap);
        }
        catch (Exception e){
            Toast.makeText(CornerActivity.this, "Can not load image file",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }


        //mainLayout = (RelativeLayout) findViewById(R.id.activity2);
        image = (ImageView) findViewById(R.id.image);

    }


    // image hast to be attached before width and height can be read
    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        width=image.getWidth();
        height=image.getHeight();
        movablePoints(width, height);
    }
    // Creates Button for crop
    /*@Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_main_menu,menu);
        Log.d(TAG, "In onCreateOptionsMenu");

        return true;    }*/
    //ActionBar Button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // method for creating the movable points
    private void movablePoints(int width, int height) {

        // Size bitmap is not equal to size imageview
        //height = Integer.parseInt(size_height);
        //width = Integer.parseInt(size_width);
        Log.d(TAG, "height: "+height);
        Log.d(TAG, "height JSON: "+size_height);
        Log.d(TAG, "height bitmap: "+bitmap_height);

        ratio_height = ((float) (bitmap_height))/ ((float) (height));
        ratio_width = ((float) (bitmap_width))/((float) (width));
        Log.d(TAG, "ratio height: "+ratio_height);
        Log.d(TAG, "ratio width: "+ratio_width);

        newc1x = (int) (Integer.parseInt(c1x) /ratio_width);
        newc1y = (int) (Integer.parseInt(c1y) /ratio_height);
        newc2x = (int) (Integer.parseInt(c2x) /ratio_width);
        newc2y = (int) (Integer.parseInt(c2y) /ratio_height);
        newc3x = (int) (Integer.parseInt(c3x) /ratio_width);
        newc3y = (int) ((Integer.parseInt(c3y)) /ratio_height);
        newc4x = (int) (Integer.parseInt(c4x) /ratio_width);
        newc4y = (int) ((Integer.parseInt(c4y)) /ratio_height);

        Log.d(TAG, "x0: "+newc1x);
        Log.d(TAG, "y0: "+newc1y);
        Log.d(TAG, "x1: "+newc2x);
        Log.d(TAG, "y1: "+newc2y);
        Log.d(TAG, "x2: "+newc3x);
        Log.d(TAG, "y2: "+newc3y);
        Log.d(TAG, "x3: "+newc4x);
        Log.d(TAG, "y3: "+newc4y);

        Context context = this;

        //first imageview (Top left corner)
        imageView1 = new ImageView(context);
        imageView1.setImageResource(R.drawable.icon);
        id=1;
        imageView1.setId(id);

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.cornerActivity);
        if (relativeLayout != null){
            Log.d(TAG, "Relative Layout not 0");}
        else {
            Log.d(TAG, "Relative Layout is 0");}


        int test = relativeLayout.getWidth();
        Log.d(TAG, Integer.toString(test));
        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(60,60);
        imageView1.setX(newc1x-30);     // -30 because x and y are top left corner of image; so x and y are middle point of image
        imageView1.setY(newc1y-30);


        //second imageview (Top right corner)
        imageView2 = new ImageView(context);
        imageView2.setImageResource(R.drawable.icon);
        id=2;
        imageView2.setId(id);

        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(60,60);
        imageView2.setX(newc2x-30);
        imageView2.setY(newc2y-30);

        //3. imageview (bottom left corner
        imageView3 = new ImageView(context);
        imageView3.setImageResource(R.drawable.icon);
        id=3;
        imageView3.setId(id);

        RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(60,60);
        imageView3.setX(newc3x-30);
        imageView3.setY(newc3y-30);

        //4. imageview (bottom right)
        imageView4 = new ImageView(context);
        imageView4.setImageResource(R.drawable.icon);
        imageView4.setId(id);

        RelativeLayout.LayoutParams layoutParams4 = new RelativeLayout.LayoutParams(60,60);
        imageView4.setX(newc4x-30);
        imageView4.setY(newc4y-30);

        relativeLayout.addView(imageView1, layoutParams1);
        relativeLayout.addView(imageView2, layoutParams2);
        relativeLayout.addView(imageView3, layoutParams3);
        relativeLayout.addView(imageView4, layoutParams4);


        imageView1.setOnTouchListener(onTouchListener());
        imageView2.setOnTouchListener(onTouchListener());
        imageView3.setOnTouchListener(onTouchListener());
        imageView4.setOnTouchListener(onTouchListener());
    }

    private View.OnTouchListener onTouchListener() {


        return new View.OnTouchListener() {

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                final int x = (int) event.getRawX();
                final int y = (int) event.getRawY();

                switch (event.getAction() & MotionEvent.ACTION_MASK) {

                    case MotionEvent.ACTION_DOWN:
                        RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams)
                                view.getLayoutParams();

                        xDelta = x - lParams.leftMargin;
                        yDelta = y - lParams.topMargin;
                        break;

                    case MotionEvent.ACTION_UP:

                        Toast.makeText(CornerActivity.this,"thanks for new location!", Toast.LENGTH_SHORT).show();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view
                                .getLayoutParams();
                        layoutParams.leftMargin = x - xDelta;
                        layoutParams.topMargin = y - yDelta;
                        layoutParams.rightMargin = 0;
                        layoutParams.bottomMargin = 0;
                        view.setLayoutParams(layoutParams);
                        break;
                }
                mainLayout.invalidate();
                //Log.d(TAG, "(x,y): ("+ event.getX()+", "+event.getY()+")");
                return true;
            }
        };
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
            int[] locations = new int[2];
            imageView1.getLocationOnScreen(locations);
            int x0 = locations[0]+30;       // +30 for correcting the -30 from the beginning;
            int y0 = locations[1]+30;


            imageView2.getLocationOnScreen(locations);
            int x1 = locations[0]+30;
            int y1 = locations[1]+30;

            imageView3.getLocationOnScreen(locations);
            int x2 = locations[0]+30;
            int y2 = locations[1]+30;

            imageView4.getLocationOnScreen(locations);
            int x3 = locations[0]+30;
            int y3 = locations[1]+30;

            bitmap_height = bitmap.getHeight();
            bitmap_width = bitmap.getWidth();
            //height = image.getHeight();
            //width = image.getWidth();

            final TypedArray styledAttributes = this.getTheme().obtainStyledAttributes(
                    new int[] { android.R.attr.actionBarSize });
            height_bar = (int) styledAttributes.getDimension(0, 0);
            styledAttributes.recycle();

            Log.d(TAG, "Action bar size: "+ Integer.toString(height_bar));
            //Log.d(TAG, "Screen height: "+Integer.toString(height));
            //ratio_height = bitmap_height/height;
            //Log.d(TAG, "Ratio height: "+ Float.toString(ratio_height));



            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);


            //int height = displayMetrics.heightPixels;
            //float width = displayMetrics.widthPixels;
            ratio_height = ((float)bitmap_height)/((float)(height+height_bar));
            float ratio_width1 = ((float)bitmap_width)/((float)width+height_bar); //corners at the top
            float ratio_width2 = ((float)bitmap_width)/((float)width); // for corner at the bottom

            Log.d(TAG, "crop started");


            //TODO richtige Paramter übergeben
            Intent startOCRActivity = new Intent(this, OCRActivity.class);
            startOCRActivity.putExtra(URI_FILE, uri);

            // difference between MainActivity and Activity2 when moveable points are not moved
            // calculated manuel by measuring differences
            double x_value = 1; //0.81;
            double y_value = 1; //0.89;
            double parameter = 210; //-0.89*210;    // needs also to be added to yi*y_value

            Log.d(TAG, "x0: "+x0);
            Log.d(TAG, "y0: "+y0);
            Log.d(TAG, "x1: "+x1);
            Log.d(TAG, "y1: "+y1);
            Log.d(TAG, "x2: "+x2);
            Log.d(TAG, "y2: "+y2);
            Log.d(TAG, "x3: "+x3);
            Log.d(TAG, "y3: "+y3);

            startOCRActivity.putExtra(X0, Integer.toString((int)(x0*x_value)));
            startOCRActivity.putExtra(Y0, Integer.toString((int)(y0*y_value - parameter)));
            startOCRActivity.putExtra(X1, Integer.toString((int)(x1*x_value)));
            startOCRActivity.putExtra(Y1, Integer.toString((int)(y1*y_value - parameter)));
            startOCRActivity.putExtra(X2, Integer.toString((int)(x3*x_value)));
            startOCRActivity.putExtra(Y2, Integer.toString((int)(y3*y_value - parameter)));
            startOCRActivity.putExtra(X3, Integer.toString((int)(x2*x_value)));
            startOCRActivity.putExtra(Y3, Integer.toString((int)(y2*y_value - parameter)));

            startOCRActivity.putExtra(IMG_HEIGHT, Integer.toString(height));
            startOCRActivity.putExtra(IMG_WIDTH, Integer.toString(width));

            startOCRActivity.putExtra(O, orientation);

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

