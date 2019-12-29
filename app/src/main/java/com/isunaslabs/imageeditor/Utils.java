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
                                         final int screenWidth,
                                         final BitmapLoadListener bitmapLoadListener) {
        Thread bitmapLoaderThread = new Thread() {
            @Override
            public void run() {
                super.run();
                FutureTarget<Bitmap> target = Glide.with(context)
                        .asBitmap()
                        .load(imgUrl)
                        .submit();
                try {
                    Bitmap resizedBitmap = resizeBitmap(target.get(), screenWidth);
                    bitmapLoadListener.onBitmapLoaded(new ImageToTrace(resizedBitmap));
                } catch (ExecutionException e) {
                    e.printStackTrace();
                    bitmapLoadListener.onBitmapLoaded(null);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    bitmapLoadListener.onBitmapLoaded(null);
                }
            }
        };
        bitmapLoaderThread.start();
    }


    private static Bitmap resizeBitmap(Bitmap bitmap, int desiredWidth) {
        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        float ratio = originalWidth * 1f / originalHeight * 1f;
        int newHeight = (int) (desiredWidth / ratio);
        return Bitmap.createScaledBitmap(bitmap, desiredWidth, newHeight, false);
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
        void onBitmapLoaded(ImageToTrace deviceImage);
    }


}
