package com.example.chris.myapplication;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.FocusFinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

//compare the detected points with the real corners and correct them when necessary
public class Activity2 extends AppCompatActivity {

    private ViewGroup mainLayout;
    private ImageView image;

    Uri picUri;

    private int xDelta;
    private int yDelta;
    int id;
    int minI, minJ, maxI, maxJ;
    int newMinI, newMinJ, newMaxI, newMaxJ;
    int bitmap_height, bitmap_width;
    ImageView imageView1;
    ImageView imageView2;
    ImageView imageView3;
    ImageView imageView4;
    String TAG = "Activity2";
    Bitmap bitmap;
    Bitmap newImage;
    float ratio_height;
    float ratio_width;
    int height_bar;
    String c1x, c1y, c2x, c2y, c3x, c3y, c4x, c4y, size_height, size_width;
    float newc1x, newc1y, newc2x, newc2y, newc3x, newc3y, newc4x, newc4y;
    public static String X0 ="X0", X1 ="X1", X2 ="X2", X3 ="X3", URI="URI", Y0="Y0", Y1="Y1", Y2="Y2", Y3="Y3", URI_FILE="URI_FILE", IMG_HEIGHT="IMG_HEIGHT", IMG_WIDTH="IMG_WIDTH";

    String uri;
    int height, width;
    float height2, width2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);


        Log.d(TAG, "Activity2 started");



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
            Toast.makeText(Activity2.this, "Can not load image file",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }


        mainLayout = (RelativeLayout) findViewById(R.id.activity2);
        image = (ImageView) findViewById(R.id.image);


    }

    // Creates Button for crop
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // image hast to be attached before width and height can be read
    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        width=image.getWidth();
        height=image.getHeight();
        movablePoints(width, height);
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

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.activity2);
        int test = relativeLayout.getWidth();
        Log.d(TAG, Integer.toString(test));
        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(60,60);
        imageView1.setX(newc1x);
        imageView1.setY(newc1y);


        //second imageview (Top right corner)
        imageView2 = new ImageView(context);
        imageView2.setImageResource(R.drawable.icon);
        id=2;
        imageView2.setId(id);

        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(60,60);
        imageView2.setX(newc2x);
        imageView2.setY(newc2y);

        //3. imageview (bottom left corner
        imageView3 = new ImageView(context);
        imageView3.setImageResource(R.drawable.icon);
        id=3;
        imageView3.setId(id);

        RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(60,60);
        imageView3.setX(newc3x);
        imageView3.setY(newc3y);

        //4. imageview (bottom right)
        imageView4 = new ImageView(context);
        imageView4.setImageResource(R.drawable.icon);
        imageView4.setId(id);

        RelativeLayout.LayoutParams layoutParams4 = new RelativeLayout.LayoutParams(60,60);
        imageView4.setX(newc4x);
        imageView4.setY(newc4y);

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
                        if (view == imageView1){
                            Toast.makeText(Activity2.this, "imageView1", Toast.LENGTH_SHORT).show();
                        }

                        if (view == imageView2){
                            Toast.makeText(Activity2.this, "imageView2", Toast.LENGTH_SHORT).show();
                        }
                        Toast.makeText(Activity2.this,"thanks for new location!", Toast.LENGTH_SHORT).show();
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_name) {
            Toast.makeText(this, "Crop started", Toast.LENGTH_SHORT).show();
            int[] locations = new int[2];
            imageView1.getLocationOnScreen(locations);
            int x0 = locations[0];
            int y0 = locations[1];


            imageView2.getLocationOnScreen(locations);
            int x1 = locations[0];
            int y1 = locations[1];

            imageView3.getLocationOnScreen(locations);
            int x2 = locations[0];
            int y2 = locations[1];

            imageView4.getLocationOnScreen(locations);
            int x3 = locations[0];
            int y3 = locations[1];

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

            //coordinates named clockwise starting with top left
            /*bitmap = processImage(bitmap, (int)(x0*ratio_height), (int) (y0*ratio_width1-height_bar),
                    (int) (x1*ratio_height), (int) (y1*ratio_width1-height_bar),
                    (int) (x3*ratio_height), (int) (y3*ratio_width2),
                    (int) (x2*ratio_height), (int) (y2*ratio_width2));
            ((ImageView)findViewById(R.id.image)).setImageBitmap(bitmap);*/


            //TODO richtige Paramter übergeben
            Intent start3Activity = new Intent(this, Activity3.class);
            start3Activity.putExtra(URI_FILE, uri);
            /*start3Activity.putExtra(X0, Integer.toString((int)(x0*ratio_height)));
            start3Activity.putExtra(Y0, Integer.toString((int)(y0*ratio_width1-height_bar)));
            start3Activity.putExtra(X1, Integer.toString((int)(x1*ratio_height)));
            start3Activity.putExtra(Y1, Integer.toString((int)(y1*ratio_width1-height_bar)));
            start3Activity.putExtra(X2, Integer.toString((int)(x3*ratio_height)));
            start3Activity.putExtra(Y2, Integer.toString((int)(y3*ratio_width2)));
            start3Activity.putExtra(X3, Integer.toString((int)(x2*ratio_height)));
            start3Activity.putExtra(Y3, Integer.toString((int)(y2*ratio_width2)));*/

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

            start3Activity.putExtra(X0, Integer.toString((int)(x0*x_value)));
            start3Activity.putExtra(Y0, Integer.toString((int)(y0*y_value - parameter)));
            start3Activity.putExtra(X1, Integer.toString((int)(x1*x_value)));
            start3Activity.putExtra(Y1, Integer.toString((int)(y1*y_value - parameter)));
            start3Activity.putExtra(X2, Integer.toString((int)(x3*x_value)));
            start3Activity.putExtra(Y2, Integer.toString((int)(y3*y_value - parameter)));
            start3Activity.putExtra(X3, Integer.toString((int)(x2*x_value)));
            start3Activity.putExtra(Y3, Integer.toString((int)(y2*y_value - parameter)));

            start3Activity.putExtra(IMG_HEIGHT, Integer.toString(height));
            start3Activity.putExtra(IMG_WIDTH, Integer.toString(width));


            Log.d(TAG, "Start Activity3");
            startActivity(start3Activity);
/*
            newX0 = x0*ratio_height;
            newX1 = x1*ratio_height;
            newX2 = x2*ratio_height;
            newX3 = x3*ratio_height;
            newY0 = y0*ratio_width1-height_bar;
            newY1 = y1*ratio_width1-height_bar;
            newY2 = y2*ratio_width2;
            newY3 = y3*ratio_width2;
            // compute height and with of new image, as as longest edges
            float newHeight1 = (float)Math.sqrt((Math.pow(newX2-newX1,2)+Math.pow((newY2-newY1),2)));
            float newHeight2 = (float)Math.sqrt((Math.pow(newX0-newX3,2)+Math.pow((newY0-newY3),2)));
            float newHeight = Math.max(newHeight1, newHeight2);

            float newWidth1 = (float)Math.sqrt((Math.pow(newX3-newX2,2)+Math.pow((newY3-newY2),2)));
            float newWidth2 = (float)Math.sqrt((Math.pow(newX0-newX1,2)+Math.pow((newY0-newY1),2)));
            float newWidth = Math.max(newWidth1, newWidth2);

            Log.d(TAG, "new Height: "+ Float.toString(newHeight));
            Log.d(TAG, "new Width: "+ Float.toString(newWidth));

            //Only 3 points
            Point[] srcTri = new Point[4];
            srcTri[0] = new Point( x0*ratio_height, y0*ratio_width1-height_bar );
            srcTri[1] = new Point( x1*ratio_height, y1*ratio_width1-height_bar );
            srcTri[2] = new Point( x3*ratio_height, y3*ratio_width1 );
            srcTri[3] = new Point( x2*ratio_height, y2*ratio_width1 );



            height2 = bitmap.getHeight();
            width2 = bitmap.getWidth();

            Point[] disTri = new Point[4];
            disTri[0] = new Point( 0, 0 );
            disTri[1] = new Point( width2, 0 );
            disTri[2] = new Point( width2, height2-(height_bar*ratio_height) );
            disTri[3] = new Point( 0, height2-(height_bar*ratio_height) );

            Mat original_image = new Mat();
            Utils.bitmapToMat(bitmap, original_image);
            Log.d(TAG, "Mat created");

            Imgproc.circle(original_image, new Point(x0*ratio_height, y0*ratio_width1-height_bar), 10, new Scalar(255, 0, 0), 10, 8, 0);
            Imgproc.circle(original_image, new Point(x1*ratio_height, y1*ratio_width1-height_bar), 10, new Scalar(255, 0, 0), 10, 8, 0);
            Imgproc.circle(original_image, new Point(x3*ratio_height, y3*ratio_width1), 10, new Scalar(255, 0, 0), 10, 8, 0);

            Mat warpMat = Imgproc.getPerspectiveTransform(new MatOfPoint2f(srcTri), new MatOfPoint2f(disTri));
//            Mat warpMat = Imgproc.getAffineTransform( new MatOfPoint2f(srcTri), new MatOfPoint2f(disTri) );

            Mat warpDst = Mat.zeros( original_image.rows(), original_image.cols(), original_image.type() );

            Imgproc.warpPerspective(original_image, warpDst, warpMat, warpDst.size());
            Log.d(TAG, "start AffineTransform");
            //Imgproc.warpAffine( original_image, warpDst, warpMat, warpDst.size() );
            Log.d(TAG, "AffineTransform done");


            /*srcTri[0] = new Point( 0,0 );
            srcTri[1] = new Point( x1*ratio_height, 0);
            srcTri[2] = new Point( newWidth, newHeight );
            //srcTri[3] = new Point( x3, y3 );

            height2 = bitmap.getHeight();
            width2 = bitmap.getWidth();

            disTri[0] = new Point( 0, 0 );
            disTri[1] = new Point( newWidth,0 );
            disTri[2] = new Point( newWidth, newHeight );
            //disTri[3] = new Point( height2, width2 );

            original_image = warpDst;
            //Utils.bitmapToMat(bitmap, original_image);
            Log.d(TAG, "Mat created");

            Imgproc.circle(original_image, new Point(x0*ratio_height, y0*ratio_width1-height_bar), 10, new Scalar(255, 0, 0), 10, 8, 0);
            Imgproc.circle(original_image, new Point(x1*ratio_height, y1*ratio_width1-height_bar), 10, new Scalar(255, 0, 0), 10, 8, 0);
            Imgproc.circle(original_image, new Point(x3*ratio_height, y3*ratio_width2), 10, new Scalar(255, 0, 0), 10, 8, 0);

            warpMat = Imgproc.getAffineTransform( new MatOfPoint2f(srcTri), new MatOfPoint2f(disTri) );

            warpDst = Mat.zeros( original_image.rows(), original_image.cols(), original_image.type() );

            Log.d(TAG, "start AffineTransform");
            Imgproc.warpAffine( original_image, warpDst, warpMat, warpDst.size() );*/
/*
            Utils.matToBitmap(warpDst, bitmap);
            //coordinates named clockwise starting with top left
            /*bitmap = processImage(bitmap, (int)(0), (int) (0),
                    (int) (0), (int) (bitmap_height),
                    (int) (newWidth), (int) (bitmap_height),
                    (int) (newWidth), (int) (0));
            ((ImageView)findViewById(R.id.image)).setImageBitmap(bitmap);*/
  /*          ((ImageView)findViewById(R.id.image)).setImageBitmap(bitmap);


            //TODO bitmap to uri
            //String uri = getImageUri(this, bitmap);

            //Log.d(TAG, uri);

            //String uri = picUri.toString();
            //Intent changeActivity = new Intent(this, Activity3.class);
            //changeActivity.putExtra("Uri", uri);
            //Log.d(TAG, "start Activity3");
            //startActivity(changeActivity);

            // TODO stretch image therewith it is a rectangle again then croprequest (+activityOnResult part)

            //croprequest(picUri, minJ, minI, maxJ, maxI);
        */}

        if (id==R.id.back){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


    //CROP REQUEST JAVA
    private void croprequest(Uri imageUri, int left, int top, int right, int bottom) {

        Rect rect = new Rect();
        rect.set(left,top, right, bottom);


        Log.d("MyApplication", "in croprequest");
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .setInitialCropWindowRectangle(rect)
                .start(this);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult started");
        //if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "in first if");
            //if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                Log.d(TAG, "test");
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                Log.d(TAG, "test2");
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), result.getUri());

                        String uri = result.getUri().toString();
                        Intent changeActivity = new Intent(this, Activity3.class);
                        changeActivity.putExtra("Uri", uri);
                        Log.d(TAG, "start Activity3");
                        startActivity(changeActivity);
                        //((ImageView) findViewById(R.id.picture)).setImageBitmap(bitmap);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
              //  }
            //}
        }
    }


    /*public Bitmap processImage(Bitmap bitmap, int x0, int y0, int x1, int y1, int x2, int y2, int x3, int y3) {
        Bitmap bmp;

        bmp = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        BitmapShader shader = new BitmapShader(bitmap,
                BitmapShader.TileMode.CLAMP,
                BitmapShader.TileMode.CLAMP);

        //float radius = Math.min(bitmap.getWidth(),bitmap.getHeight()) / RADIUS_FACTOR;
        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(shader);

        /*RectF rect = new RectF(TRIANGLE_WIDTH, 0,
                bitmap.getWidth(), bitmap.getHeight());
        canvas.drawRoundRect(rect, radius, radius, paint);*/
/*
        Path path = new Path();
        path.moveTo(x0, y0);
        path.lineTo(x1, y1);
        path.lineTo(x2, y2);
        path.lineTo(x3, y3);
        path.close();
        canvas.drawPath(path, paint);

        return bmp;
    }

    public String getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return path;
    }*/
}
