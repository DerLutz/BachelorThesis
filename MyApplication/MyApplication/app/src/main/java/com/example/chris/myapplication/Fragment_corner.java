package com.example.chris.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.LongDef;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

public class Fragment_corner extends Fragment {
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
    String TAG = "Fragment_corner";
    Bitmap bitmap;
    float ratio_height;
    float ratio_width;
    int height_bar;
    float imageHeight, imageWidth;
    String c1x, c1y, c2x, c2y, c3x, c3y, c4x, c4y, size_height, size_width;
    float newc1x, newc1y, newc2x, newc2y, newc3x, newc3y, newc4x, newc4y;
    RelativeLayout relativeLayout;
    public static String X0 = "X0", X1 = "X1", X2 = "X2", X3 = "X3", URI = "URI", Y0 = "Y0", Y1 = "Y1", Y2 = "Y2", Y3 = "Y3", URI_FILE = "URI_FILE", IMG_HEIGHT = "IMG_HEIGHT", IMG_WIDTH = "IMG_WIDTH", O = "O";

    static float x4, x1, x2, x3, y4, y1, y2, y3;

    String uri, orientation;
    int display_height, display_width;
    Toolbar toolbar;
    View nav_view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d(TAG, "in Fragment_corner");
        nav_view = inflater.inflate(R.layout.fragment_corner, container, false);

        uri = getArguments().getString("uri");
        c1x = getArguments().getString("c1x");
        c1y = getArguments().getString("c1y");
        c2x = getArguments().getString("c2x");
        c2y = getArguments().getString("c2y");
        c3x = getArguments().getString("c3x");
        c3y = getArguments().getString("c3y");
        c4x = getArguments().getString("c4x");
        c4y = getArguments().getString("c4y");

        Log.d(TAG, "c1x: " +c1x);
        Log.d(TAG, "c1y: " +c1y);

        size_height = getArguments().getString("size_height");
        size_width = getArguments().getString("size_width");

        Log.d(TAG, "Height from detection: "+size_height);


        Log.d(TAG, "read Values");

        Log.d(TAG, uri);
        picUri = Uri.parse(uri);
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), picUri);

            bitmap_height = bitmap.getHeight();
            bitmap_width = bitmap.getWidth();

            Matrix matrix = new Matrix();

            //Necessary?
            matrix.postRotate(90);
            // Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

            //set image as background
            image = (ImageView) nav_view.findViewById(R.id.image);
            image.setImageBitmap(bitmap);

            ViewTreeObserver vto = image.getViewTreeObserver();
            vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    image.getViewTreeObserver().removeOnPreDrawListener(this);
                    imageHeight = image.getMeasuredHeight();
                    imageWidth = image.getMeasuredWidth();
                    Log.d(TAG, "Height: " + imageHeight + " Width: " + imageWidth);
                    WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
                    Display display = wm.getDefaultDisplay();
                    DisplayMetrics metrics = new DisplayMetrics();
                    display.getMetrics(metrics);
                    display_width = metrics.widthPixels;
                    display_height = metrics.heightPixels;

                    Log.d(TAG, "Display Height: "+display_height);
                    Log.d(TAG, "Display width: "+display_width);


                    movablePoints(display_width, display_height);
                    return true;
                }
            });


        }
        catch (Exception e){
            Toast.makeText(getActivity(), "Can not load image file",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }


        Toolbar toolbar = nav_view.findViewById(R.id.toolbar);
        //mainLayout = (RelativeLayout) findViewById(R.id.activity2);


        return nav_view;
    }

    // method for creating the movable points
    private void movablePoints(int width, int height) {

        // Size bitmap is not equal to size imageview
        //height = Integer.parseInt(size_height);
        //width = Integer.parseInt(size_width);
        Log.d(TAG, "height: "+height);
        int sh = Integer.parseInt(size_height);
        Log.d(TAG, "height JSON: "+sh);
        Log.d(TAG, "height bitmap: "+bitmap_height);

        ratio_height =  (float) Integer.parseInt(size_height)/ (float) height;
        ratio_width = ((float) (Integer.parseInt(size_width)))/((float) (width));
        Log.d(TAG, "ratio height: "+ratio_height);
        Log.d(TAG, "ratio width: "+ratio_width);

        final TypedArray styledAttributes = getActivity().getTheme().obtainStyledAttributes(
                new int[] { android.R.attr.actionBarSize });
        height_bar = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();
        Log.d(TAG, "Toolbar size: "+ height_bar);

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

        Context context = getActivity();

        //first imageview (Top left corner)
        imageView1 = new ImageView(context);
        imageView1.setImageResource(R.drawable.icon);
        imageView1.setId(R.id.View1Id);

        relativeLayout = (RelativeLayout) nav_view.findViewById(R.id.corner_frag);
        if (relativeLayout != null){
            Log.d(TAG, "Relative Layout not 0");}
        else {
            Log.d(TAG, "Relative Layout is 0");}


        //int test = relativeLayout.getWidth();
        //Log.d(TAG, Integer.toString(test));
        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(40,40);

        //- 20 (120 = 1/2 * 40  (40 LayoutParams)) weil Top Left corner will be the declared point
        x1 = Float.parseFloat(c1x)/(Float.parseFloat(size_width)/imageWidth)-20;
        y1 = Float.parseFloat(c1y)/(Float.parseFloat(size_height)/imageHeight)-20;
        imageView1.setX(x1);

        imageView1.setY(y1);

        Log.d(TAG, "x on screen: " + (Float.parseFloat(c1x)/(Float.parseFloat(size_width)/imageWidth)-20));
        Log.d(TAG, "y on screen: " + (Float.parseFloat(c1y)/(Float.parseFloat(size_height)/imageHeight)-20));
        //second imageview (Top right corner)
        imageView2 = new ImageView(context);
        imageView2.setImageResource(R.drawable.icon);

        imageView2.setId(R.id.View2Id);

        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(40,40);
        //- 20 (120 = 1/2 * 40  (40 LayoutParams)) weil Top Left corner will be the declared point
        x2 = Float.parseFloat(c2x)/(Float.parseFloat(size_width)/imageWidth)-20;
        y2 = Float.parseFloat(c2y)/(Float.parseFloat(size_height)/imageHeight)-20;
        imageView2.setX(x2);
        imageView2.setY(y2);

        //3. imageview (bottom left corner
        imageView3 = new ImageView(context);
        imageView3.setImageResource(R.drawable.icon);
        imageView3.setId(R.id.View3Id);

        RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(40,40);
        //- 20 (120 = 1/2 * 40  (40 LayoutParams)) weil Top Left corner will be the declared point
        x3 = Float.parseFloat(c3x)/(Float.parseFloat(size_width)/imageWidth)-20;
        y3 = Float.parseFloat(c3y)/(Float.parseFloat(size_height)/imageHeight)-20;
        imageView3.setX(x3);
        imageView3.setY(y3);

        //4. imageview (bottom right)
        imageView4 = new ImageView(context);
        imageView4.setImageResource(R.drawable.icon);
        imageView4.setId(R.id.View4Id);

        RelativeLayout.LayoutParams layoutParams4 = new RelativeLayout.LayoutParams(40,40);
        //- 20 (120 = 1/2 * 40  (40 LayoutParams)) weil Top Left corner will be the declared point
        x4 = Float.parseFloat(c4x)/(Float.parseFloat(size_width)/imageWidth)-20;
        y4 = Float.parseFloat(c4y)/(Float.parseFloat(size_height)/imageHeight)-20;
        imageView4.setX(x4);
        imageView4.setY(y4);


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

                        int[] locations = new int[2];
                        view.getLocationOnScreen(locations);
                        int x0 = locations[0];
                        int y0 = locations[1];

                        if (view.getId()==imageView1.getId()) {
                            x1 = x0;
                            y1 = y0;
                        }
                        if (view.getId()==imageView2.getId()) {
                            x2 = x0;
                            y2 = y0;
                        }
                        if (view.getId()==imageView3.getId()) {
                            x3 = x0;
                            y3 = y0;
                        }
                        if (view.getId()==imageView4.getId()) {
                            x4 = x0;
                            y4 = y0;
                        }
                        Toast.makeText(getActivity(),"thanks for new location of view " + view.getId(), Toast.LENGTH_SHORT).show();
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
                //mainLayout.invalidate();
                relativeLayout.invalidate();
                //Log.d(TAG, "(x,y): ("+ event.getX()+", "+event.getY()+")");
                return true;
            }
        };
    }


    //SaveValues for Crop Button in CornerActivity
    public static float x1() {
        return x1;
    }
    public static float x2() {
        return x2;
    }
    public static float x3() {
        return x3;
    }
    public static float x4() {
        return x4;
    }
    public static float y1() {
        return y1;
    }
    public static float y2() {
        return y2;
    }
    public static float y3() {
        return y3;
    }
    public static float y4() {
        return y4;
    }

}

