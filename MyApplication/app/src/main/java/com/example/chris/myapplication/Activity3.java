package com.example.chris.myapplication;

//import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

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
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

//Todo at moment present image after cut, later upload image an present the text
//Cut the image and stretch so we get only the recipe
public class Activity3 extends Activity {

    Uri picUri;
    String uri;
    String TAG = "Activity3";
    String c1x, c1y, c2x, c2y, c3x, c3y, c4x, c4y, size_height, size_width;
    String ServerUploadPath ="http://192.168.56.1:1337/cornerDetection";   //Todo correct path missing
    ProgressDialog progressDialog ;
    ByteArrayOutputStream byteArrayOutputStream ;
    byte[] byteArray ;
    String ConvertImage ;
    HttpURLConnection httpURLConnection ;
    URL url;
    OutputStream outputStream;
    BufferedWriter bufferedWriter ;
    int RC ;
    BufferedReader bufferedReader ;
    Bitmap FixBitmap;


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

        uri = intent.getStringExtra(Activity2.URI_FILE);
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



            Utils.matToBitmap(warpDst, bitmap);
            FixBitmap = bitmap;

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
                progressDialog = ProgressDialog.show(Activity3.this,"Image is Uploading","Please Wait",false,false);
            }

            @Override
            protected void onPostExecute(String string1) {
                super.onPostExecute(string1);
                progressDialog.dismiss();
                Toast.makeText(Activity3.this,string1,Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(Void... params) {


                Activity3.ImageProcessClass imageProcessClass = new Activity3.ImageProcessClass();

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

    public class ImageProcessClass {

        public String ImageHttpRequest(String requestURL, String PData) {
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


                /*if (PData != null) {              //For ContentValue
                    OutputStream ostream = httpURLConnection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(ostream, "UTF-8"));
                    StringBuilder requestresult = new StringBuilder();
                    boolean first = true;
                    for (Map.Entry<String, Object> entry : PData.valueSet()) {
                        if (first)
                            first = false;
                        else
                            requestresult.append("&");
                        requestresult.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                        requestresult.append("=");
                        requestresult.append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
                    }
                    writer.write(requestresult.toString());
                    writer.flush();
                    writer.close();
                    ostream.close();
                }*/

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
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d(TAG, "Answer corner detection: " + stringBuilder.toString());

            //Read JSON
            try {
                JSONObject json = new JSONObject(stringBuilder.toString());

                /* Example
                c1x = json.getJSONArray("corner1").getJSONObject(0).getString("x");
                size_height = json.getJSONArray("size").getJSONObject(0).getString("height"); */


                Log.d(TAG, "JSON ausgelesen");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return stringBuilder.toString();
        }
    }


}
