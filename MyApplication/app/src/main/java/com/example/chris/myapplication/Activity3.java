package com.example.chris.myapplication;

//import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

//Cut the image and stretch so we get only the recipe
public class Activity3 extends Activity {

    Uri picUri;
    String uri;
    String TAG = "Activity3";
    String c1x, c1y, c2x, c2y, c3x, c3y, c4x, c4y, size_height, size_width;

    Bitmap newImage;

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu3, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_3);

        Log.d(TAG, "Activity3 started");



        Intent intent = getIntent();

        uri = intent.getStringExtra(MainActivity.URI_FILE);
        Log.d(TAG, uri);

        try {


            c1x = intent.getStringExtra(Activity2.X0);
            c1y = intent.getStringExtra(Activity2.Y0);
            c2x = intent.getStringExtra(Activity2.X1);
            c2y = intent.getStringExtra(Activity2.Y1);
            c3x = intent.getStringExtra(Activity2.X2);
            c3y = intent.getStringExtra(Activity2.Y2);
            c4x = intent.getStringExtra(Activity2.X3);
            c4y = intent.getStringExtra(Activity2.Y3);

            size_height = intent.getStringExtra(Activity2.IMG_HEIGHT);
            size_width = intent.getStringExtra(Activity2.IMG_WIDTH);
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
            disTri[2] = new Point( 0, original_image.rows() );
            disTri[1] = new Point( original_image.cols(), 0 );
            disTri[3] = new Point( original_image.cols(), original_image.rows() );


            Mat warpMat = Imgproc.getPerspectiveTransform(new MatOfPoint2f(srcTri), new MatOfPoint2f(disTri));
            //Mat warpMat = Imgproc.getPerspectiveTransform( new MatOfPoint2f(srcTri), new MatOfPoint2f(disTri) );

            Mat warpDst = Mat.zeros( original_image.rows(), original_image.cols(), original_image.type() );

            Log.d(TAG, "start AffineTransform");
            Imgproc.warpPerspective( original_image, warpDst, warpMat, warpDst.size() );
            Log.d(TAG, "AffineTransform done");


            /*
            //Affine Transform for 4. point (missing in the first)
            srcTri[0] = new Point( height, 0 );
            srcTri[1] = new Point( Integer.parseInt(c4x), Integer.parseInt(c4y) );
            srcTri[2] = new Point( 0, 0 );

            disTri[0] = new Point( height, 0 );
            disTri[1] = new Point( 0, width );
            disTri[2] = new Point( 0, 0 );

            Utils.bitmapToMat(bitmap, original_image);
            Log.d(TAG, "Mat2 created");

            warpMat = Imgproc.getAffineTransform( new MatOfPoint2f(srcTri), new MatOfPoint2f(disTri) );

            warpDst = Mat.zeros( original_image.rows(), original_image.cols(), original_image.type() );

            //AffineTransform works only with 3 points
            Log.d(TAG, "start AffineTransform2");
            Imgproc.warpAffine( original_image, warpDst, warpMat, warpDst.size() );
            Log.d(TAG, "AffineTransform2 done");*/

            Utils.matToBitmap(warpDst, bitmap);

            // Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

            ((ImageView)findViewById(R.id.image)).setImageBitmap(bitmap);
        }
        catch (Exception e){
            Log.d(TAG, "can not load image file");
            e.printStackTrace();
            Toast.makeText(Activity3.this, "Can not load image file",Toast.LENGTH_SHORT).show();
        }


    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_menu) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

        if (id==R.id.back){
            Intent intent = new Intent(this, Activity2.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
