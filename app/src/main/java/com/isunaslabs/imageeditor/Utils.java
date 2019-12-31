package com.isunaslabs.imageeditor;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.isunaslabs.imageeditor.model.ImageToTrace;

import java.util.concurrent.ExecutionException;

public class Utils {
    private static float imageWidth = 0;
    private static float labelTextSize = 0;
    private static boolean isImageResizedByWidth = true;


    public static void hideSystemUI(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            View view = activity.getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    // Set the content to appear under the system bars so that the
                    // content doesn't resize when the system bars hide and show.
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    //|View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    // Hide the nav bar and status bar
                    //| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN);
        } else {
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    public static void showSystemUI(Activity activity) {
        activity.getWindow().getDecorView()
                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }


    public static void loadBitmapFromUrl(final Context context,
                                         final String imgUrl,
                                         final int layoutWidth,
                                         final int layoutHeight,
                                         final BitmapLoadListener bitmapLoadListener) {

        labelTextSize = context.getResources().getDimensionPixelSize(R.dimen.label_size);

        Thread bitmapLoaderThread = new Thread() {
            @Override
            public void run() {
                super.run();
                FutureTarget<Bitmap> target = Glide.with(context)
                        .asBitmap()
                        .load(imgUrl)
                        .submit();
                try {
                    Bitmap resizedBitmap = resizeBitmap(target.get(), layoutWidth,layoutHeight);
                    bitmapLoadListener.onBitmapLoaded(new ImageToTrace(resizedBitmap),isImageResizedByWidth);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                    bitmapLoadListener.onBitmapLoaded(null,isImageResizedByWidth);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    bitmapLoadListener.onBitmapLoaded(null,isImageResizedByWidth);
                }
            }
        };
        bitmapLoaderThread.start();
    }


    private static Bitmap resizeBitmap(Bitmap bitmap, int layoutWidth,int layoutHeight) {
        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        float imageRatio = originalWidth * 1f / originalHeight * 1f;
        float layoutRatio = layoutWidth / layoutHeight;

        float newImageWidth = 0;
        float newImageHeight = 0;
        //determine how to resize the image
        if(layoutRatio < imageRatio) {
            //wide image,resize by width
            newImageWidth = layoutWidth;
            newImageHeight = newImageWidth / imageRatio;
            isImageResizedByWidth = true;
        }else {
            //tall image,resize by height
            newImageHeight = layoutHeight;
            newImageWidth = newImageHeight * imageRatio;
            isImageResizedByWidth = false;
        }
        imageWidth = newImageWidth;

        return Bitmap.createScaledBitmap(bitmap, (int) newImageWidth, (int) newImageHeight, false);
    }

    public static int getLabelColor(int pollutionCode) {
        switch (pollutionCode) {
            case 1:
                return Color.MAGENTA;
            case 2:
                return Color.BLUE;
            case 3:
                return Color.GREEN;
            case 4:
                return Color.RED;
        }
        return Color.MAGENTA;
    }


    public interface BitmapLoadListener {
        void onBitmapLoaded(ImageToTrace deviceImage,boolean isResizedByWidth);
    }

    public static float getImageWidth() {
        return imageWidth;
    }

    public static float getLabelTextSize() {
        return labelTextSize;
    }
}
